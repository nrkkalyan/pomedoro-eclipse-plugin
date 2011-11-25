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
package rabbit.ui.internal.treebuilders;

import rabbit.data.access.model.ICommandData;
import rabbit.data.access.model.IKey;
import rabbit.ui.IProvider;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.eclipse.jface.viewers.TreePath;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

/**
 * A {@link CommandDataTreeBuilder} takes input as {@link ICommandDataProvider}
 * and builds tree leaves based on the order of the categories provided by the
 * {@link ICategoryProvider}, the last segment of every path will be the
 * {@link Integer} data node ({@link ICommandData#COUNT}) of each
 * {@link ICommandData} provided by the provider.
 */
public final class CommandDataTreeBuilder
    extends AbstractDataTreeBuilder<ICommandData> {

  /**
   * Provides {@link ICommandData}.
   */
  public static interface ICommandDataProvider extends IProvider<ICommandData> {}

  /**
   * @param categoryProvider the provider for providing categories.
   * @throws NullPointerException if argument is null.
   */
  public CommandDataTreeBuilder(ICategoryProvider categoryProvider) {
    super(categoryProvider, ImmutableMap.<ICategory, IKey<?>> of(
        Category.COMMAND, ICommandData.COMMAND,
        Category.DATE, ICommandData.DATE,
        Category.WORKSPACE, ICommandData.WORKSPACE));
  }

  @Override
  protected Collection<ICommandData> getData(Object input) {
    if (input instanceof ICommandDataProvider) {
      return ((ICommandDataProvider) input).get();
    }
    return emptyList();
  }

  @Override
  protected List<TreePath> transform(ICommandData data, TreePath path)
      throws Exception {
    return ImmutableList.of(path.createChildPath(data.get(ICommandData.COUNT)));
  }
}
