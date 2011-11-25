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
package rabbit.ui.internal.pages;

import static rabbit.ui.internal.pages.Category.DATE;
import static rabbit.ui.internal.pages.Category.PERSPECTIVE;
import static rabbit.ui.internal.pages.Category.WORKSPACE;
import static rabbit.ui.internal.viewers.Viewers.newTreeViewerColumn;

import rabbit.data.access.IAccessor;
import rabbit.data.access.model.IPerspectiveData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.handler.DataHandler;
import rabbit.ui.Preference;
import rabbit.ui.internal.treebuilders.PerspectiveDataTreeBuilder;
import rabbit.ui.internal.treebuilders.PerspectiveDataTreeBuilder.IPerspectiveDataProvider;
import rabbit.ui.internal.util.Categorizer;
import rabbit.ui.internal.util.CategoryProvider;
import rabbit.ui.internal.util.ICategorizer;
import rabbit.ui.internal.util.IConverter;
import rabbit.ui.internal.util.TreePathDurationConverter;
import rabbit.ui.internal.util.TreePathValueProvider;
import rabbit.ui.internal.viewers.CompositeCellLabelProvider;
import rabbit.ui.internal.viewers.DateLabelProvider;
import rabbit.ui.internal.viewers.FilterableTreePathContentProvider;
import rabbit.ui.internal.viewers.PerspectiveLabelProvider;
import rabbit.ui.internal.viewers.TreePathContentProvider;
import rabbit.ui.internal.viewers.TreePathDurationLabelProvider;
import rabbit.ui.internal.viewers.TreePathPatternFilter;
import rabbit.ui.internal.viewers.TreeViewerCellPainter;
import rabbit.ui.internal.viewers.TreeViewerColumnSorter;
import rabbit.ui.internal.viewers.TreeViewerColumnValueSorter;
import rabbit.ui.internal.viewers.Viewers;
import rabbit.ui.internal.viewers.WorkspaceStorageLabelProvider;

import static com.google.common.base.Predicates.instanceOf;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A page for displaying perspective usage.
 */
public class PerspectivePage extends AbsPage {

  private FilteredTree filteredTree;
  private CategoryProvider categoryProvider;
  private TreePathValueProvider durationProvider;
  private TreePathContentProvider contentProvider;

  public PerspectivePage() {}

  @Override
  public void createContents(Composite parent) {
    Category[] supported = {WORKSPACE, DATE, PERSPECTIVE};
    categoryProvider = new CategoryProvider(supported, PERSPECTIVE);
    categoryProvider.addObserver(this);

    contentProvider = new TreePathContentProvider(
        new PerspectiveDataTreeBuilder(categoryProvider));
    contentProvider.addObserver(this);

    durationProvider = createDurationValueProvider();
    durationProvider.addObserver(this);

    // The main label provider for the first column:
    CompositeCellLabelProvider mainLabels = new CompositeCellLabelProvider(
        new PerspectiveLabelProvider(),
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
        newTreeViewerColumn(viewer, SWT.RIGHT, "Usage", 150);
    durationColumn.getColumn().addSelectionListener(durationSorter);
    durationColumn.setLabelProvider(
        new TreePathDurationLabelProvider(durationProvider, mainLabels));

    TreeViewerColumn durationGraphColumn =
        newTreeViewerColumn(viewer, SWT.LEFT, "", 100);
    durationGraphColumn.getColumn().addSelectionListener(durationSorter);
    durationGraphColumn.setLabelProvider(new TreeViewerCellPainter(
        durationProvider) {
      @Override
      protected Color createColor(Display display) {
        return new Color(display, 218, 176, 0);
      }
    });
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    List<IContributionItem> items = new CommonToolBarBuilder()
        .enableFilterControlAction(filteredTree, true)
        .enableTreeAction(filteredTree.getViewer())
        .enableGroupByAction(categoryProvider)
        .enableColorByAction(durationProvider)

        .addGroupByAction(PERSPECTIVE)
        .addGroupByAction(DATE, PERSPECTIVE)
        .addGroupByAction(WORKSPACE, PERSPECTIVE)

        .addColorByAction(PERSPECTIVE)
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
    return new UpdateJob<IPerspectiveData>(viewer, pref, getAccessor()) {
      @Override
      protected Object getInput(final Collection<IPerspectiveData> data) {
        return new IPerspectiveDataProvider() {
          @Override
          public Collection<IPerspectiveData> get() {
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

  private ICategorizer createCategorizer() {
    Map<Predicate<Object>, Category> categories = ImmutableMap.of(
        instanceOf(IPerspectiveDescriptor.class), PERSPECTIVE,
        instanceOf(LocalDate.class), DATE,
        instanceOf(WorkspaceStorage.class), WORKSPACE);
    ICategorizer categorizer = new Categorizer(categories);
    return categorizer;
  }

  private TreePathValueProvider createDurationValueProvider() {
    ICategorizer categorizer = createCategorizer();
    IConverter<TreePath> converter = new TreePathDurationConverter();
    return new TreePathValueProvider(
        categorizer, contentProvider, converter, PERSPECTIVE);
  }

  private IAccessor<IPerspectiveData> getAccessor() {
    return DataHandler.getAccessor(IPerspectiveData.class);
  }

}
