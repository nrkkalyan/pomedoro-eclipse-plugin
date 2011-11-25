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
package rabbit.ui.internal.viewers;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Predicate;

import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Represents a content provider that can filter elements.
 * <p>
 * The difference between using a {@link ViewerFilter} and this content provider
 * is that a {@link ViewerFilter} hides the filtered elements, but this class
 * will prevent the filtered element from being added to the tree in the first
 * place. Also if all elements of a parent node are filtered, when using a
 * {@link ViewerFilter} the tree will still display a plus/minus sign beside the
 * parent node, when using this class that won't happen.
 * </p>
 */
public class FilterableTreePathContentProvider
    extends ForwardingTreePathContentProvider implements IFilterable {

  private final IFilterable filterable;
  private final ITreePathContentProvider provider;
  
  /**
   * @param provider the real provider to provide elements and children.
   * @param filters the filters to be added to filter out unwanted elements.
   * @see #addFilter(Predicate)
   * @throws NullPointerException if {@code provider} is null, or {@code filters}
   *         contains null elements. 
   */
  public FilterableTreePathContentProvider(
      ITreePathContentProvider provider, Predicate<? super Object>... filters) {
    
    this.provider = checkNotNull(provider);
    this.filterable = new FilterableSupport();
    for (Predicate<Object> filter : filters) {
      addFilter(checkNotNull(filter));
    }
  }

  @Override
  public void addFilter(Predicate<? super Object> filter) {
    filterable.addFilter(filter);
  }

  @Override
  public Object[] filter(Object[] elements) {
    return filterable.filter(elements);
  }

  @Override
  public Object[] getChildren(TreePath parentPath) {
    return filter(super.getChildren(parentPath));
  }

  @Override
  public Object[] getElements(Object inputElement) {
    return filter(super.getElements(inputElement));
  }

  @Override
  public boolean hasChildren(TreePath path) {
    return getChildren(path).length > 0;
  }

  @Override
  public void removeFilter(Predicate<? super Object> filter) {
    filterable.removeFilter(filter);
  }

  @Override
  protected ITreePathContentProvider delegate() {
    return provider;
  }
}