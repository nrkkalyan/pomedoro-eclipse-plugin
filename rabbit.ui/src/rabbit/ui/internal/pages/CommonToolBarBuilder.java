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

import rabbit.ui.internal.actions.CategoryAction;
import rabbit.ui.internal.actions.CollapseAllAction;
import rabbit.ui.internal.actions.ColorByAction;
import rabbit.ui.internal.actions.DropDownAction;
import rabbit.ui.internal.actions.ExpandAllAction;
import rabbit.ui.internal.actions.GroupByAction;
import rabbit.ui.internal.actions.PaintCategoryAction;
import rabbit.ui.internal.actions.ShowHideFilterControlAction;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.util.IVisualProvider;
import rabbit.ui.internal.util.TreePathValueProvider;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.Lists;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.dialogs.FilteredTree;

import java.util.List;

/**
 * Builder to build common tool bar actions for pages. The order of the tool bar
 * items are predefined by this builder, that means the order of the items will
 * be consistent.
 */
public final class CommonToolBarBuilder {

  private IAction filterAction;
  private IAction treeAction;

  private final List<IAction> groupByActions;
  private final List<IAction> colorByActions;

  private ICategoryProvider categoryProvider;
  private IVisualProvider[] visualProviders;

  public CommonToolBarBuilder() {
    groupByActions = Lists.newLinkedList();
    colorByActions = Lists.newLinkedList();
  }

  /**
   * @throws IllegalStateException if
   *         {@link #enableColorByAction(TreePathValueProvider...)} has not been
   *         called.
   */
  public CommonToolBarBuilder addColorByAction(ICategory visualCategory) {
    checkState(visualProviders != null);
    colorByActions.add(new PaintCategoryAction(visualCategory, visualProviders));
    return this;
  }

  /**
   * @throws IllegalStateException if
   *         {@link #enableGroupByAction(ICategoryProvider)} has not been
   *         called.
   */
  public CommonToolBarBuilder addGroupByAction(ICategory... categories) {
    checkState(categoryProvider != null);
    groupByActions.add(new CategoryAction(categoryProvider, categories));
    return this;
  }

  /**
   * @throws IllegalStateException if
   *         {@link #enableGroupByAction(ICategoryProvider)} has not been
   *         called.
   */
  public CommonToolBarBuilder addGroupByAction(
      String text, ImageDescriptor image, ICategory... categories) {
    IAction action = new CategoryAction(categoryProvider, categories);
    action.setText(text);
    action.setImageDescriptor(image);
    groupByActions.add(action);
    return this;
  }

  /**
   * Builds a collection of actions from the configuration.
   * @return a collection of actions, the returned collection is modifiable.
   */
  public List<IContributionItem> build() {
    List<IContributionItem> items = Lists.newArrayList();
    if (filterAction != null) {
      items.add(new ActionContributionItem(filterAction));
    }
    if (treeAction != null) {
      items.add(new ActionContributionItem(treeAction));
    }
    if (categoryProvider != null) {
      items.add(new ActionContributionItem(
          new GroupByAction(categoryProvider, groupByActions
              .toArray(new IAction[0]))));
    }
    if (visualProviders != null) {
      items.add(new ActionContributionItem(
          new ColorByAction(colorByActions.toArray(new IAction[0]))));
    }
    return items;
  }

  /**
   * Enables the color-by action group.
   * @param valueProviders the provider to accept the action events.
   * @return this
   * @see IVisualProvider#getVisualCategory()
   * @see IVisualProvider#setVisualCategory(ICategory)
   */
  public CommonToolBarBuilder enableColorByAction(
      IVisualProvider... valueProviders) {
    for (IVisualProvider p : valueProviders) {
      checkNotNull(p);
    }
    this.visualProviders = valueProviders.clone();
    return this;
  }

  /**
   * Enables the show/hide filter control action.
   * @param tree the tree hosting the filter control.
   * @param hideControl true to hide the control by default.
   * @return this
   */
  public CommonToolBarBuilder enableFilterControlAction(FilteredTree tree,
      boolean hideControl) {
    filterAction = new ShowHideFilterControlAction(tree, hideControl);
    return this;
  }

  /**
   * Enables the group-by action group.
   * @param categoryProvider the object to receive the action events.
   * @return this
   * @see ICategoryProvider#setSelected(ICategory...)
   * @see ICategoryProvider#getSelected()
   */
  public CommonToolBarBuilder enableGroupByAction(
      ICategoryProvider categoryProvider) {
    this.categoryProvider = categoryProvider;
    return this;
  }

  /**
   * Enables the collapse/expand actions for the given viewer.
   * @param viewer the viewer to receive the action events.
   * @return this
   * @see TreeViewer#expandAll()
   * @see TreeViewer#collapseAll()
   */
  public CommonToolBarBuilder enableTreeAction(TreeViewer viewer) {
    treeAction = new DropDownAction(new CollapseAllAction(viewer),
        new ExpandAllAction(viewer));
    return this;
  }
}
