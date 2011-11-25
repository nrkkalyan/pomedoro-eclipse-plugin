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

import rabbit.data.access.model.IData;
import rabbit.data.access.model.IKey;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.viewers.ITreePathBuilder;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;

import com.google.common.collect.ImmutableMap;

import org.eclipse.jface.viewers.TreePath;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Internal abstract class for implementing a tree builder. This type of builder
 * will usually take an {@code IProvider} as input and builds a collection of
 * tree paths based on the categories provided by an {@link ICategoryProvider}.
 * @param <T> the type of data.
 */
public abstract class AbstractDataTreeBuilder<T extends IData>
    implements ITreePathBuilder {

  private final ICategoryProvider categoryProvider;
  private final Map<ICategory, IKey<? extends Object>> keys;

  /**
   * Constructor.
   * @param categoryProvider the category provider for providing categories.
   * @param keys a map categorizes each category to a key, then the keys will be
   *        used to get the data.
   */
  protected AbstractDataTreeBuilder(
      ICategoryProvider categoryProvider,
      Map<? extends ICategory, ? extends IKey<? extends Object>> keys) {

    this.categoryProvider = checkNotNull(categoryProvider, "category provider");
    this.keys = ImmutableMap.copyOf(checkNotNull(keys, "keys"));
  }

  @Override
  public List<TreePath> build(Object input) {
    Collection<T> data = getData(input);
    if (data == null) {
      data = emptyList();
    }

    List<TreePath> paths = newArrayListWithCapacity(data.size());
    for (T node : data) {
      List<Object> segments = newArrayList();
      for (ICategory category : categoryProvider.getSelected()) {
        segments.add(node.get(keys.get(category)));
      }

      try {
        // Note that new TreePath(...) will also throw an exception if one of
        // the segments is null:
        paths.addAll(transform(node, new TreePath(segments.toArray())));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return paths;
  }

  /**
   * Gets the data from the input. The return value must not be null.
   * @param input the input element.
   * @return a collection of data, may be empty but must not be null.
   */
  protected abstract Collection<T> getData(Object input);

  /**
   * Transforms the given data and path to a new collection of tree paths.
   * Subclasses may override this method to return tree paths to be included in
   * the final collection. The default implementation simply returns the given
   * path.
   * @param data the data to build the paths for.
   * @param path the path that has been built with the data, note that this path
   *        will not be included in the final collection, but the returned paths
   *        will be.
   * @return a collection of paths transformed from the data and the path.
   * @throws Exception if any error occurs.
   */
  protected List<TreePath> transform(T data, TreePath path) throws Exception {
    return asList(path);
  }
}
