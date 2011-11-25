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

import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.util.IVisualProvider;
import rabbit.ui.internal.util.TreePathValueProvider;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;

import java.util.List;

/**
 * Helper class for saving/restoring states.
 */
class StateHelper {

  private static final char SEPARATOR = ',';
  private static final String WIDTHS = "widths";
  private static final String CATEGORIES = "categories";
  private static final String VISUAL_CATEGORY = "visualCategory";

  /**
   * Creates a new {@link StateHelper}.
   * @param parentMemento the parent {@link IMemento}.
   * @param childId the ID of this child element, used when saving/restoring
   *        states.
   * @throws NullPointerException if any argument is null.
   */
  static StateHelper of(IMemento parentMemento, String childId) {
    return new StateHelper(parentMemento, childId);
  }

  private final IMemento parentMemento;
  private final String childId;

  /**
   * @param parentMemento
   * @param childId
   * @throws NullPointerException if any argument is null.
   */
  private StateHelper(IMemento parentMemento, String childId) {
    this.parentMemento = checkNotNull(parentMemento);
    this.childId = checkNotNull(childId);
  }

  /**
   * Restores the previously saved categories. Does nothing if no categories
   * found.
   * @param provider the provider to set the categories.
   * @return this
   * @throws NullPointerException if argument is null.
   * @see #saveCategories(Category...)
   */
  StateHelper restoreCategories(ICategoryProvider provider) {
    checkNotNull(provider);

    List<Category> list = retrieveSavedCategories();
    if (list != null) {
      provider.setSelected(list.toArray(new Category[list.size()]));
    }
    return this;
  }

  /**
   * Retrieve previously saved categories. Note that if no categories has been
   * saved, null is returned, not an empty collection, because an empty
   * collection of categories may have been saved.
   * @return a list of categories, or null if none has been saved.
   */
  List<Category> retrieveSavedCategories() {
    IMemento m = getMemento();

    String string = m.getString(CATEGORIES);
    if (string == null) {
      return null;
    }

    List<Category> result = newArrayList();
    List<String> list = newArrayList(Splitter.on(SEPARATOR).split(string));
    for (String str : list) {
      try {
        result.add(Enum.valueOf(Category.class, str));
      } catch (IllegalArgumentException e) {
        // Ignore invalid ones
        System.err.println(getClass().getSimpleName()
            + " - retrieveSavedCategories: " + e.getMessage());
      }
    }
    return result;
  }

  /**
   * Restores the column widths. If no previously saved widths found, or if the
   * number of widths saved and the number of columns to be restored doesn't
   * match, no action will be performed.
   * @param columns the columns to set the widths.
   * @return this
   * @throws NullPointerException if {@code columns} is null or contains null.
   * @see #saveColumnWidths(TreeColumn[])
   */
  StateHelper restoreColumnWidths(TreeColumn[] columns) {
    checkNotNull(columns);
    for (TreeColumn column : columns) {
      checkNotNull(column);
    }

    IMemento m = getMemento();

    String widthString = m.getString(WIDTHS);
    if (widthString == null) {
      return this;
    }

    List<String> list = newArrayList(Splitter.on(SEPARATOR).split(widthString));
    if (list.size() != columns.length) {
      return this;
    }

    for (int i = 0; i < columns.length; i++) {
      try {
        columns[i].setWidth(Integer.parseInt(list.get(i)));
      } catch (NumberFormatException e) {
        System.err.println(getClass().getSimpleName()
            + " - restoreColumnWidths: " + e.getMessage());
      }
    }
    return this;
  }

  /**
   * Saves the given categories.
   * @param categories the categories to be saved.
   * @return this
   * @throws NullPointerException if argument contains null.
   * @see #restoreCategories(ICategoryProvider)
   */
  StateHelper saveCategories(Category... categories) {
    for (Category c : categories) {
      checkNotNull(c);
    }

    IMemento m = getMemento();
    String string = Joiner.on(SEPARATOR).join(categories);
    m.putString(CATEGORIES, string);
    return this;
  }

  /**
   * Saves the column widths of the given columns.
   * @param columns the columns.
   * @return this
   * @throws NullPointerException if {@code columns} is null or contains null.
   * @see #restoreColumnWidths(TreeColumn[])
   */
  StateHelper saveColumnWidths(TreeColumn[] columns) {
    checkNotNull(columns);
    for (TreeColumn column : columns) {
      checkNotNull(column);
    }

    IMemento m = getMemento();
    List<Integer> widths = newArrayListWithCapacity(columns.length);
    for (TreeColumn column : columns) {
      widths.add(Integer.valueOf(column.getWidth()));
    }
    String widthString = Joiner.on(SEPARATOR).join(widths);
    m.putString(WIDTHS, widthString);
    return this;
  }

  /**
   * Saves the given visual category.
   * @param category the category to be saved.
   * @return this.
   * @throws NullPointerException if argument is null.
   * @see TreePathValueProvider#getVisualCategory()
   * @see TreePathValueProvider#setVisualCategory(ICategory)
   */
  StateHelper saveVisualCategory(Category category) {
    checkNotNull(category);

    IMemento m = getMemento();
    m.putString(VISUAL_CATEGORY, category.toString());
    return this;
  }

  /**
   * Retrieve the previously saved visual category.
   * @return the category, or null.
   */
  Category retrieveSavedVisualCategory() {
    IMemento m = getMemento();

    String str = m.getString(VISUAL_CATEGORY);
    if (str == null) {
      return null;
    }

    try {
      return Enum.valueOf(Category.class, str);
    } catch (IllegalArgumentException e) {
      // Ignore invalid
      System.err.println(getClass().getSimpleName()
          + " - retrieveSavedVisualCategory: " + e.getMessage());
    }
    return null;
  }

  /**
   * Restores the previously saved visual category. If no saved category found,
   * no action will be taken.
   * @param provider the provider to set the category.
   * @return this
   * @throws NullPointerException if argument is null.
   * @see IVisualProvider#setVisualCategory(ICategory)
   */
  StateHelper restoreVisualCategory(IVisualProvider provider) {
    checkNotNull(provider);

    Category c = retrieveSavedVisualCategory();
    if (c != null) {
      provider.setVisualCategory(c);
    }
    return this;
  }

  private IMemento getMemento() {
    IMemento m = parentMemento.getChild(childId);
    if (m == null) {
      return parentMemento.createChild(childId);
    }
    return m;
  }
}
