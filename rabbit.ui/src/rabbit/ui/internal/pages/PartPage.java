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
import static rabbit.ui.internal.pages.Category.WORKBENCH_TOOL;
import static rabbit.ui.internal.pages.Category.WORKSPACE;
import static rabbit.ui.internal.viewers.Viewers.newTreeViewerColumn;

import rabbit.data.access.IAccessor;
import rabbit.data.access.model.IPartData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.handler.DataHandler;
import rabbit.ui.Preference;
import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.treebuilders.PartDataTreeBuilder;
import rabbit.ui.internal.treebuilders.PartDataTreeBuilder.IPartDataProvider;
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
import rabbit.ui.internal.viewers.WorkbenchPartLabelProvider;
import rabbit.ui.internal.viewers.WorkspaceStorageLabelProvider;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.base.Predicates.not;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.views.IViewDescriptor;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A page for displaying workbench part usage.
 */
public class PartPage extends AbsPage {

  private class HideElementsAction extends Action {
    final Predicate<Object> elementsToHide;

    HideElementsAction(
        String text, ImageDescriptor image, Predicate<Object> elementsToHide) {
      super(text, IAction.AS_CHECK_BOX);
      setImageDescriptor(image);
      this.elementsToHide = elementsToHide;
    }

    @Override
    public void run() {
      super.run();
      if (!isChecked()) {
        filteredContentProvider.removeFilter(elementsToHide);
      } else {
        filteredContentProvider.addFilter(elementsToHide);
      }
      refreshViewer();
    }
  }

  private FilteredTree filteredTree;
  private CategoryProvider categoryProvider;
  private TreePathValueProvider durationProvider;

  private TreePathContentProvider realContentProvider;
  /**
   * Wraps {@link #realContentProvider} to filter elements.
   */
  private FilterableTreePathContentProvider filteredContentProvider;

  private final HideElementsAction hideViewsAction;
  private final HideElementsAction hideEditorsAction;

  public PartPage() {
    hideEditorsAction = createHideEditorsAction();
    hideViewsAction = createHideViewsAction();
  }

  @Override
  public void createContents(Composite parent) {
    Category[] supported = {WORKSPACE, DATE, WORKBENCH_TOOL};
    categoryProvider = new CategoryProvider(supported, WORKBENCH_TOOL);
    categoryProvider.addObserver(this);

    realContentProvider = new TreePathContentProvider(
        new PartDataTreeBuilder(categoryProvider));
    realContentProvider.addObserver(this);

    durationProvider = createDurationValueProvider();
    durationProvider.addObserver(this);

    // The main label provider for the first column:
    CompositeCellLabelProvider mainLabels = new CompositeCellLabelProvider(
        new WorkbenchPartLabelProvider(),
        new DateLabelProvider(),
        new WorkspaceStorageLabelProvider());

    // The viewer:
    filteredTree = Viewers.newFilteredTree(parent,
        new TreePathPatternFilter(mainLabels));
    TreeViewer viewer = filteredTree.getViewer();
    filteredContentProvider =
        new FilterableTreePathContentProvider(realContentProvider);
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
        return new Color(display, 49, 132, 155);
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

        .addGroupByAction(WORKBENCH_TOOL)
        .addGroupByAction(DATE, WORKBENCH_TOOL)
        .addGroupByAction(WORKSPACE, WORKBENCH_TOOL)

        .addColorByAction(WORKBENCH_TOOL)
        .addColorByAction(DATE)
        .addColorByAction(WORKSPACE)
        .build();

    items.add(new ActionContributionItem(hideViewsAction));
    items.add(new ActionContributionItem(hideEditorsAction));

    for (IContributionItem item : items) {
      toolBar.add(item);
    }
    return items.toArray(new IContributionItem[items.size()]);
  }

  @Override
  public Job updateJob(Preference pref) {
    TreeViewer viewer = filteredTree.getViewer();
    return new UpdateJob<IPartData>(viewer, pref, getAccessor()) {
      @Override
      protected Object getInput(final Collection<IPartData> data) {
        return new IPartDataProvider() {
          @Override
          public Collection<IPartData> get() {
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
        instanceOf(IWorkbenchPartDescriptor.class), WORKBENCH_TOOL,
        instanceOf(LocalDate.class), DATE,
        instanceOf(WorkspaceStorage.class), WORKSPACE);
    ICategorizer categorizer = new Categorizer(categories);
    return categorizer;
  }

  private TreePathValueProvider createDurationValueProvider() {
    ICategorizer categorizer = createCategorizer();
    IConverter<TreePath> converter = new TreePathDurationConverter();
    return new TreePathValueProvider(
        categorizer, realContentProvider, converter, WORKBENCH_TOOL);
  }

  private HideElementsAction createHideEditorsAction() {
    final Predicate<Object> hideEditorsFilter = instanceOf(IEditorDescriptor.class);
    HideElementsAction hideEditorsAction = new HideElementsAction(
        "Hide Editors", SharedImages.EDITOR, hideEditorsFilter);
    return hideEditorsAction;
  }

  private HideElementsAction createHideViewsAction() {
    final Predicate<Object> hideViewsFilter = instanceOf(IViewDescriptor.class);
    HideElementsAction hideViewsAction = new HideElementsAction("Hide Views",
        SharedImages.VIEW, hideViewsFilter);
    return hideViewsAction;
  }

  private IAccessor<IPartData> getAccessor() {
    return DataHandler.getAccessor(IPartData.class);
  }

  private void refreshViewer() {
    Predicate<Object> predicate = Predicates.alwaysTrue();
    if (hideEditorsAction.isChecked()) {
      predicate = and(predicate, not(hideEditorsAction.elementsToHide));
    }
    if (hideViewsAction.isChecked()) {
      predicate = and(predicate, not(hideViewsAction.elementsToHide));
    }
    durationProvider.setMaxValue(
        durationProvider.getVisualCategory(), predicate);
    filteredTree.getViewer().getTree().setRedraw(false);
    filteredTree.getViewer().refresh(false);
    filteredTree.getViewer().getTree().setRedraw(true);
  }
}
