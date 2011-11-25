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
import static rabbit.ui.internal.pages.Category.WORKSPACE;
import static rabbit.ui.internal.viewers.Viewers.newTreeViewerColumn;

import rabbit.data.access.IAccessor;
import rabbit.data.access.model.ISessionData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.handler.DataHandler;
import rabbit.ui.Preference;
import rabbit.ui.internal.treebuilders.SessionDataTreeBuilder;
import rabbit.ui.internal.treebuilders.SessionDataTreeBuilder.ISessionDataProvider;
import rabbit.ui.internal.util.Categorizer;
import rabbit.ui.internal.util.CategoryProvider;
import rabbit.ui.internal.util.ICategorizer;
import rabbit.ui.internal.util.IConverter;
import rabbit.ui.internal.util.TreePathDurationConverter;
import rabbit.ui.internal.util.TreePathValueProvider;
import rabbit.ui.internal.viewers.CompositeCellLabelProvider;
import rabbit.ui.internal.viewers.DateLabelProvider;
import rabbit.ui.internal.viewers.FilterableTreePathContentProvider;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A page displaying how much time is spent on using Eclipse every day.
 */
public final class SessionPage extends AbsPage {

  private FilteredTree filteredTree;
  private CategoryProvider categoryProvider;
  private TreePathValueProvider valueProvider;
  private TreePathContentProvider contentProvider;

  public SessionPage() {}

  @Override
  public void createContents(Composite parent) {
    Category[] supported = {WORKSPACE, DATE};
    categoryProvider = new CategoryProvider(supported, DATE);
    categoryProvider.addObserver(this);
    contentProvider = new TreePathContentProvider(new SessionDataTreeBuilder(categoryProvider));
    contentProvider.addObserver(this);
    valueProvider = createValueProvider();
    valueProvider.addObserver(this);

    // The main label provider for the first column:
    CompositeCellLabelProvider mainLabels = new CompositeCellLabelProvider(
        new DateLabelProvider(), new WorkspaceStorageLabelProvider());
    
    // The viewer:
    filteredTree = Viewers.newFilteredTree(parent, new TreePathPatternFilter(mainLabels));
    TreeViewer viewer = filteredTree.getViewer();
    FilterableTreePathContentProvider filteredContentProvider = 
        new FilterableTreePathContentProvider(contentProvider);
    filteredContentProvider.addFilter(instanceOf(Duration.class));
    viewer.setContentProvider(filteredContentProvider);

    // Column sorters:
    TreeViewerColumnSorter labelSorter = new InternalTreeViewerColumnLabelSorter(viewer, mainLabels);
    TreeViewerColumnSorter durationSorter = new TreeViewerColumnValueSorter(viewer, valueProvider);

    // The columns:
    
    TreeViewerColumn mainColumn = newTreeViewerColumn(viewer, SWT.LEFT, "Name", 200);
    mainColumn.getColumn().addSelectionListener(labelSorter);
    ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
    mainColumn.setLabelProvider(new DecoratingStyledCellLabelProvider(mainLabels, decorator, null));
    

    TreeViewerColumn durationColumn = newTreeViewerColumn(viewer, SWT.RIGHT, "Duration", 150);
    durationColumn.getColumn().addSelectionListener(durationSorter);
    durationColumn.setLabelProvider(new TreePathDurationLabelProvider(valueProvider));

    TreeViewerColumn graphColumn = newTreeViewerColumn(viewer, SWT.LEFT, "", 100);
    graphColumn.getColumn().addSelectionListener(durationSorter);
    graphColumn.setLabelProvider(new TreeViewerCellPainter(valueProvider) {
      @Override
      protected Color createColor(Display display) {
        return new Color(display, 208, 145, 60);
      }
    });
  }
  
  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    List<IContributionItem> items = new CommonToolBarBuilder()
        .enableFilterControlAction(filteredTree, true)
        .enableTreeAction(filteredTree.getViewer())
        .enableGroupByAction(categoryProvider)
        .enableColorByAction(valueProvider)
        .addGroupByAction(DATE)
        .addGroupByAction(WORKSPACE, DATE)
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
    return new UpdateJob<ISessionData>(viewer, pref, getAccessor()) {
      @Override
      protected Object getInput(final Collection<ISessionData> data) {
        return new ISessionDataProvider() {
          @Override
          public Collection<ISessionData> get() {
            return data;
          }
        };
      }
    };
  }

  private TreePathValueProvider createValueProvider() {
    Map<Predicate<Object>, Category> categories = ImmutableMap.of(
        instanceOf(LocalDate.class), DATE,
        instanceOf(WorkspaceStorage.class), WORKSPACE);
    ICategorizer categorizer = new Categorizer(categories);
    IConverter<TreePath> converter = new TreePathDurationConverter();
    return new TreePathValueProvider(categorizer, contentProvider, converter, DATE);
  }

  private IAccessor<ISessionData> getAccessor() {
    return DataHandler.getAccessor(ISessionData.class);
  }

  @Override
  protected Category getVisualCategory() {
    return (Category) valueProvider.getVisualCategory();
  }

  @Override
  protected void setVisualCategory(Category category) {
    valueProvider.setVisualCategory(category);
  }

  @Override
  protected void updateMaxValue() {
    valueProvider.setMaxValue(valueProvider.getVisualCategory());
  }

  @Override
  protected void setSelectedCategories(List<Category> categories) {
    categoryProvider.setSelected(categories.toArray(new Category[0]));
  }

  @Override
  protected Category[] getSelectedCategories() {
    return categoryProvider.getSelected().toArray(new Category[0]);
  }

  @Override
  protected FilteredTree getFilteredTree() {
    return filteredTree;
  }
}
