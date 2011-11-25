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
package rabbit.ui.internal.actions;

import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.action.Action;

/**
 * An action to set the categories of a given provider.
 */
public final class CategoryAction extends Action {

  private final ICategoryProvider provider;
  private final ICategory[] categories;

  /**
   * Constructs a new action. When this action is invoked,
   * {@link ICategoryProvider#setSelected(ICategory...)} will be called with the
   * given categories. The text/image of this action will be taken from the
   * first category.
   * @param categoryProvider the provider to set the categories.
   * @param categories the categories to set.
   * @throws NullPointerException if any argument is null.
   */
  public CategoryAction(
      ICategoryProvider categoryProvider, ICategory... categories) {

    checkNotNull(categoryProvider);
    checkNotNull(categories);
    checkArgument(categories.length > 0);
    for (ICategory c : categories) {
      checkNotNull(c);
    }
    this.provider = categoryProvider;
    this.categories = categories.clone();
    setText(categories[0].getText());
    setImageDescriptor(categories[0].getImageDescriptor());
  }

  @Override
  public void run() {
    super.run();
    provider.setSelected(categories);
  }
}
