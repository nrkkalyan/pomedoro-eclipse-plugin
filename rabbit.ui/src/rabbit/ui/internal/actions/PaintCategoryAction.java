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
import rabbit.ui.internal.util.IVisualProvider;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;

import org.eclipse.jface.action.Action;

import java.util.List;

/**
 * An action to call {@link IVisualProvider#setVisualCategory(ICategory)} when
 * it's invoked.
 */
public final class PaintCategoryAction extends Action {

  private final ICategory visualCategory;
  private final List<IVisualProvider> providers;

  /**
   * Constructor. When this action is invoked,
   * {@link IVisualProvider#setVisualCategory(ICategory)} will be called with
   * the given category. The text/image will be taken from the category.
   * @param providers the providers to set the category.
   * @param visualCategory the category to be set.
   * @throws NullPointerException if any argument is null.
   */
  public PaintCategoryAction(
      ICategory visualCategory, IVisualProvider... providers) {
    this.visualCategory = checkNotNull(visualCategory);
    for (IVisualProvider p : providers) {
      checkNotNull(p);
    }
    this.providers = ImmutableList.copyOf(providers);
    setText(visualCategory.getText());
    setImageDescriptor(visualCategory.getImageDescriptor());
  }

  @Override
  public void run() {
    super.run();
    for (IVisualProvider provider : providers) {
      provider.setVisualCategory(visualCategory);
    }
  }
}
