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

import static rabbit.ui.internal.pages.Category.COMMAND;
import static rabbit.ui.internal.pages.Category.DATE;
import static rabbit.ui.internal.pages.Category.WORKSPACE;
import static rabbit.ui.internal.viewers.Viewers.newTreeViewerColumn;

import rabbit.data.access.IAccessor;
import rabbit.data.access.model.ICommandData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.handler.DataHandler;
import rabbit.ui.Preference;
import rabbit.ui.internal.treebuilders.CommandDataTreeBuilder;
import rabbit.ui.internal.treebuilders.CommandDataTreeBuilder.ICommandDataProvider;
import rabbit.ui.internal.util.Categorizer;
import rabbit.ui.internal.util.CategoryProvider;
import rabbit.ui.internal.util.ICategorizer;
import rabbit.ui.internal.util.IConverter;
import rabbit.ui.internal.util.TreePathIntConverter;
import rabbit.ui.internal.util.TreePathValueProvider;
import rabbit.ui.internal.viewers.CommandDescriptionProvider;
import rabbit.ui.internal.viewers.CommandLabelProvider;
import rabbit.ui.internal.viewers.CompositeCellLabelProvider;
import rabbit.ui.internal.viewers.DateLabelProvider;
import rabbit.ui.internal.viewers.FilterableTreePathContentProvider;
import rabbit.ui.internal.viewers.TreePathContentProvider;
import rabbit.ui.internal.viewers.TreePathIntLabelProvider;
import rabbit.ui.internal.viewers.TreePathPatternFilter;
import rabbit.ui.internal.viewers.TreeViewerCellPainter;
import rabbit.ui.internal.viewers.TreeViewerColumnLabelSorter;
import rabbit.ui.internal.viewers.TreeViewerColumnSorter;
import rabbit.ui.internal.viewers.TreeViewerColumnValueSorter;
import rabbit.ui.internal.viewers.Viewers;
import rabbit.ui.internal.viewers.WorkspaceStorageLabelProvider;

import static com.google.common.base.Predicates.instanceOf;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

import org.eclipse.core.commands.Command;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A page for displaying command usage.
 */
public class CommandPage extends AbsPage {

  private FilteredTree filteredTree;
  private CategoryProvider categoryProvider;
  private TreePathValueProvider valueProvider;
  private TreePathContentProvider contentProvider;

  public CommandPage() {}

  @Override
  public void createContents(Composite parent) {
    Category[] supported = {WORKSPACE, DATE, COMMAND};
    categoryProvider = new CategoryProvider(supported, COMMAND);
    categoryProvider.addObserver(this);
    contentProvider = new TreePathContentProvider(
        new CommandDataTreeBuilder(categoryProvider));
    contentProvider.addObserver(this);
    valueProvider = createValueProvider();
    valueProvider.addObserver(this);

    // The main label provider for the first column:
    CompositeCellLabelProvider mainLabels = new CompositeCellLabelProvider(
        new CommandLabelProvider(),
        new DateLabelProvider(),
        new WorkspaceStorageLabelProvider());

    // The viewer:
    filteredTree = Viewers.newFilteredTree(parent,
        new TreePathPatternFilter(mainLabels));
    TreeViewer viewer = filteredTree.getViewer();
    FilterableTreePathContentProvider filteredContentProvider =
        new FilterableTreePathContentProvider(contentProvider);
    filteredContentProvider.addFilter(instanceOf(Integer.class));
    viewer.setContentProvider(filteredContentProvider);

    // Column sorters:
    TreeViewerColumnSorter labelSorter =
        new InternalTreeViewerColumnLabelSorter(viewer, mainLabels);
    TreeViewerColumnSorter countSorter =
        new TreeViewerColumnValueSorter(viewer, valueProvider);

    // The columns:

    TreeViewerColumn mainColumn =
        newTreeViewerColumn(viewer, SWT.LEFT, "Name", 200);
    mainColumn.getColumn().addSelectionListener(labelSorter);
    ILabelDecorator decorator =
        PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
    mainColumn.setLabelProvider(new DecoratingStyledCellLabelProvider(
        mainLabels, decorator, null));

    TreeViewerColumn descriptionColumn =
        newTreeViewerColumn(viewer, SWT.LEFT, "Description", 200);
    ColumnLabelProvider descriptionLabels = new CommandDescriptionProvider();
    descriptionColumn.setLabelProvider(descriptionLabels);
    descriptionColumn.getColumn().addSelectionListener(
        new TreeViewerColumnLabelSorter(viewer, descriptionLabels));

    TreeViewerColumn durationColumn =
        newTreeViewerColumn(viewer, SWT.RIGHT, "Usage Count", 150);
    durationColumn.getColumn().addSelectionListener(countSorter);
    durationColumn.setLabelProvider(
        new TreePathIntLabelProvider(valueProvider, mainLabels));

    TreeViewerColumn graphColumn =
        newTreeViewerColumn(viewer, SWT.LEFT, "", 100);
    graphColumn.getColumn().addSelectionListener(countSorter);
    graphColumn.setLabelProvider(new TreeViewerCellPainter(valueProvider));
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    List<IContributionItem> items = new CommonToolBarBuilder()
        .enableFilterControlAction(filteredTree, true)
        .enableTreeAction(filteredTree.getViewer())
        .enableGroupByAction(categoryProvider)
        .enableColorByAction(valueProvider)
        .addGroupByAction(COMMAND)
        .addGroupByAction(DATE, COMMAND)
        .addGroupByAction(WORKSPACE, COMMAND)
        .addColorByAction(COMMAND)
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
    return new UpdateJob<ICommandData>(viewer, pref, getAccessor()) {
      @Override
      protected Object getInput(final Collection<ICommandData> data) {
        return new ICommandDataProvider() {
          @Override
          public Collection<ICommandData> get() {
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
    return (Category) valueProvider.getVisualCategory();
  }

  @Override
  protected void setSelectedCategories(List<Category> categories) {
    categoryProvider.setSelected(categories.toArray(new Category[0]));
  }

  @Override
  protected void setVisualCategory(Category category) {
    valueProvider.setVisualCategory(category);
  }

  @Override
  protected void updateMaxValue() {
    valueProvider.setMaxValue(valueProvider.getVisualCategory());
  }

  private TreePathValueProvider createValueProvider() {
    Map<Predicate<Object>, Category> categories = ImmutableMap.of(
        instanceOf(Command.class), COMMAND,
        instanceOf(LocalDate.class), DATE,
        instanceOf(WorkspaceStorage.class), WORKSPACE);
    ICategorizer categorizer = new Categorizer(categories);
    IConverter<TreePath> converter = new TreePathIntConverter();
    return new TreePathValueProvider(
        categorizer, contentProvider, converter, COMMAND);
  }

  private IAccessor<ICommandData> getAccessor() {
    return DataHandler.getAccessor(ICommandData.class);
  }
}
