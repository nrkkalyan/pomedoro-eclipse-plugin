/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package rabbit.ui.internal.pages.java;

import static rabbit.ui.internal.pages.Category.DATE;
import static rabbit.ui.internal.pages.Category.JAVA_MEMBER;
import static rabbit.ui.internal.pages.Category.JAVA_METHOD;
import static rabbit.ui.internal.pages.Category.JAVA_PACKAGE;
import static rabbit.ui.internal.pages.Category.JAVA_PACKAGE_ROOT;
import static rabbit.ui.internal.pages.Category.JAVA_TYPE;
import static rabbit.ui.internal.pages.Category.JAVA_TYPE_ROOT;
import static rabbit.ui.internal.pages.Category.PROJECT;
import static rabbit.ui.internal.pages.Category.WORKSPACE;
import static rabbit.ui.internal.viewers.Viewers.newTreeViewerColumn;

import rabbit.data.access.IAccessor;
import rabbit.data.access.model.IJavaData;
import rabbit.data.handler.DataHandler;
import rabbit.ui.Preference;
import rabbit.ui.internal.pages.AbsPage;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.pages.CommonToolBarBuilder;
import rabbit.ui.internal.pages.InternalTreeViewerColumnLabelSorter;
import rabbit.ui.internal.pages.UpdateJob;
import rabbit.ui.internal.treebuilders.JavaDataTreeBuilder;
import rabbit.ui.internal.treebuilders.JavaDataTreeBuilder.IJavaDataProvider;
import rabbit.ui.internal.util.CategoryProvider;
import rabbit.ui.internal.util.ICategorizer;
import rabbit.ui.internal.util.IConverter;
import rabbit.ui.internal.util.JavaVisualCategorizer;
import rabbit.ui.internal.util.TreePathDurationConverter;
import rabbit.ui.internal.util.TreePathValueProvider;
import rabbit.ui.internal.viewers.CompositeCellLabelProvider;
import rabbit.ui.internal.viewers.DateLabelProvider;
import rabbit.ui.internal.viewers.FilterableTreePathContentProvider;
import rabbit.ui.internal.viewers.JavaLabelProvider;
import rabbit.ui.internal.viewers.ResourceLabelProvider;
import rabbit.ui.internal.viewers.TreePathContentProvider;
import rabbit.ui.internal.viewers.TreePathDurationLabelProvider;
import rabbit.ui.internal.viewers.TreePathPatternFilter;
import rabbit.ui.internal.viewers.TreeViewerCellPainter;
import rabbit.ui.internal.viewers.TreeViewerColumnSorter;
import rabbit.ui.internal.viewers.TreeViewerColumnValueSorter;
import rabbit.ui.internal.viewers.Viewers;
import rabbit.ui.internal.viewers.WorkspaceStorageLabelProvider;

import static com.google.common.base.Predicates.instanceOf;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.joda.time.Duration;

import java.util.Collection;
import java.util.List;

/**
 * A page for displaying time spent on java elements.
 */
public final class JavaPage extends AbsPage {

  // @formatter:off
  /*
   * The following categories are used to structure the data:
   * 
   * Category.WORKSPACE
   * Category.DATE,
   * Category.PROJECT,
   * Category.JAVA_PACKAGE_ROOT, 
   * Category.JAVA_PACKAGE,
   * Category.JAVA_TYPE_ROOT,
   * Category.JAVA_MEMBER,
   * 
   * The following categories are used to paint the corresponding elements in
   * the viewer:
   * 
   * Category.WORKSPACE
   * Category.DATE
   * Category.PROJECT, 
   * Category.JAVA_PACKAGE_ROOT, 
   * Category.JAVA_PACKAGE,
   * Category.JAVA_TYPE_ROOT,
   * Category.JAVA_TYPE,
   * Category.JAVA_METHOD,
   * 
   * The difference between the two is that when we structure the data, we use
   * JAVA_MEMBER instead of JAVA_TYPE and JAVA_METHOD, JAVA_MEMBER includes 
   * both TYPE and METHOD so that the structure of the class is maintained when 
   * we build the tree. 
   */
  // @formatter:on

  private FilteredTree filteredTree;
  private CategoryProvider categoryProvider;
  private TreePathValueProvider durationProvider;
  private TreePathContentProvider contentProvider;

  public JavaPage() {}

  @Override
  public void createContents(Composite parent) {
    Category[] supported = {
        WORKSPACE,
        DATE,
        PROJECT,
        JAVA_PACKAGE_ROOT,
        JAVA_PACKAGE,
        JAVA_TYPE_ROOT,
        JAVA_MEMBER,
        };
    categoryProvider = new CategoryProvider(supported,
        PROJECT, JAVA_PACKAGE_ROOT, JAVA_PACKAGE, JAVA_TYPE_ROOT, JAVA_MEMBER);
    categoryProvider.addObserver(this);

    contentProvider = new TreePathContentProvider(
        new JavaDataTreeBuilder(categoryProvider));
    contentProvider.addObserver(this);

    durationProvider = createDurationValueProvider();
    durationProvider.addObserver(this);

    // The main label provider for the first column:
    CompositeCellLabelProvider mainLabels = new CompositeCellLabelProvider(
        new JavaLabelProvider(),
        new ResourceLabelProvider(),
        new DateLabelProvider(),
        new WorkspaceStorageLabelProvider());

    // The viewer:
    filteredTree = Viewers.newFilteredTree(parent,
        new TreePathPatternFilter(mainLabels));
    TreeViewer viewer = filteredTree.getViewer();
    FilterableTreePathContentProvider filteredContentProvider =
        new FilterableTreePathContentProvider(contentProvider);
    filteredContentProvider.addFilter(instanceOf(Duration.class));
    viewer.setContentProvider(filteredContentProvider);

    // Column sorters:
    TreeViewerColumnSorter labelSorter =
        new InternalTreeViewerColumnLabelSorter(viewer, mainLabels);
    TreeViewerColumnSorter durationSorter =
        new TreeViewerColumnValueSorter(viewer, durationProvider);

    // The columns:

    TreeViewerColumn mainColumn =
        newTreeViewerColumn(viewer, SWT.LEFT, "Name", 200);
    mainColumn.getColumn().addSelectionListener(labelSorter);
    ILabelDecorator decorator =
        PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
    mainColumn.setLabelProvider(new DecoratingStyledCellLabelProvider(
        mainLabels, decorator, null));

    TreeViewerColumn durationColumn =
        newTreeViewerColumn(viewer, SWT.RIGHT, "Time Spent", 150);
    durationColumn.getColumn().addSelectionListener(durationSorter);
    durationColumn.setLabelProvider(new TreePathDurationLabelProvider(
        durationProvider, mainLabels));

    TreeViewerColumn durationGraphColumn =
        newTreeViewerColumn(viewer, SWT.LEFT, "", 100);
    durationGraphColumn.getColumn().addSelectionListener(durationSorter);
    durationGraphColumn.setLabelProvider(new TreeViewerCellPainter(
        durationProvider));
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    List<IContributionItem> items = new CommonToolBarBuilder()
        .enableFilterControlAction(filteredTree, true)
        .enableTreeAction(filteredTree.getViewer())
        .enableGroupByAction(categoryProvider)
        .enableColorByAction(durationProvider)

        .addGroupByAction(
            PROJECT,
            JAVA_PACKAGE_ROOT,
            JAVA_PACKAGE,
            JAVA_TYPE_ROOT,
            JAVA_MEMBER)
        .addGroupByAction(
            DATE,
            PROJECT,
            JAVA_PACKAGE_ROOT,
            JAVA_PACKAGE,
            JAVA_TYPE_ROOT,
            JAVA_MEMBER)
        .addGroupByAction(
            WORKSPACE,
            PROJECT,
            JAVA_PACKAGE_ROOT,
            JAVA_PACKAGE,
            JAVA_TYPE_ROOT,
            JAVA_MEMBER)

        .addColorByAction(JAVA_METHOD)
        .addColorByAction(JAVA_TYPE)
        .addColorByAction(JAVA_TYPE_ROOT)
        .addColorByAction(JAVA_PACKAGE)
        .addColorByAction(JAVA_PACKAGE_ROOT)
        .addColorByAction(PROJECT)
        .addColorByAction(DATE)
        .addColorByAction(WORKSPACE)
        .build();

    for (IContributionItem item : items) {
      toolBar.add(item);
    }
    return items.toArray(new IContributionItem[items.size()]);
  }

  @Override
  public Job updateJob(Preference pref) {
    TreeViewer viewer = filteredTree.getViewer();
    return new UpdateJob<IJavaData>(viewer, pref, getAccessor()) {
      @Override
      protected Object getInput(final Collection<IJavaData> data) {
        return new IJavaDataProvider() {
          @Override
          public Collection<IJavaData> get() {
            return data;
          }
        };
      }
    };
  }

  @Override
  protected FilteredTree getFilteredTree() {
    return filteredTree;
  }

  @Override
  protected Category[] getSelectedCategories() {
    return categoryProvider.getSelected().toArray(new Category[0]);
  }

  @Override
  protected Category getVisualCategory() {
    return (Category) durationProvider.getVisualCategory();
  }

  @Override
  protected void setSelectedCategories(List<Category> categories) {
    Category[] selected = categories.toArray(new Category[0]);
    categoryProvider.setSelected(selected);
  }

  @Override
  protected void setVisualCategory(Category category) {
    durationProvider.setVisualCategory(category);
  }

  @Override
  protected void updateMaxValue() {
    durationProvider.setMaxValue(durationProvider.getVisualCategory());
  }

  private TreePathValueProvider createDurationValueProvider() {
    ICategorizer categorizer = new JavaVisualCategorizer();
    IConverter<TreePath> converter = new TreePathDurationConverter();
    return new TreePathValueProvider(
        categorizer, contentProvider, converter, JAVA_METHOD);
  }

  private IAccessor<IJavaData> getAccessor() {
    return DataHandler.getAccessor(IJavaData.class);
  }

}
