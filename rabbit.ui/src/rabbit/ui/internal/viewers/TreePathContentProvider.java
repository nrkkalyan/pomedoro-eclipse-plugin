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

import rabbit.ui.IProvider;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newLinkedHashSet;

import com.google.common.collect.ImmutableList;

import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * This type of content provider takes a collection of leaf {@link TreePath}s
 * from a {@link ITreePathBuilder} each time the input changes and returns
 * elements/children/parents based on the leaves.
 * <ul>
 * <li>
 * The root elements of the input will be the distinct first elements of all the
 * leaves.</li>
 * <li>
 * The children of a tree path {@code p} will be the distinct elements at index
 * {@code p.getSegmentCount()} of all the leaves that has A as their parent
 * path, where {@code leaf.getSegmentCount() > p.getSegmentCount()}</li>
 * <li>
 * The possible parents of a child will be sub paths of all the leaves that
 * contain the child as one of their segments. The range of a sub path will be
 * from segment 0 up to but exclude the child segment.</li>
 * </ul>
 */
public final class TreePathContentProvider
    extends Observable implements ITreePathContentProvider, IProvider<TreePath> {
  
  private static final TreePath[] EMPTY = {};

  private final ITreePathBuilder builder;

  /**
   * Immutable list of leaves, the list may be empty but never null.
   */
  private List<TreePath> leaves;

  /**
   * Constructs a content provider.
   * 
   * @param builder the builder to builder data from input element.
   * @throws NullPointerException if {@code builder == null}.
   */
  public TreePathContentProvider(ITreePathBuilder builder) {
    this.builder = checkNotNull(builder, "builder");
    this.leaves = emptyList();
  }

  @Override
  public void dispose() {
    leaves = emptyList();
  }

  /**
   * Gets the current tree leaves in use by this content provider.
   * @return the leaves.
   */
  @Override
  public Collection<TreePath> get() {
    return leaves;
  }

  @Override
  public Object[] getChildren(@Nullable TreePath branch) {
    return childrenOf(branch).toArray();
  }

  /**
   * A {@link TreePathContentProvider} will always return an array of elements
   * from the latest input, regardless of what the parameter of this method is.
   * 
   * @param inputElement the input element, this parameter is ignored by this
   *        content provider.
   */
  @Override
  public Object[] getElements(@Nullable Object inputElement) {
    Set<Object> elements = newLinkedHashSet();
    for (TreePath leaf : leaves) {
      if (leaf.getFirstSegment() != null) {
        elements.add(leaf.getFirstSegment());
      }
    }
    return elements.toArray();
  }

  @Override
  public TreePath[] getParents(@Nullable Object element) {
    if (element == null) {
      return EMPTY;
    }
    
    Set<TreePath> parents = newLinkedHashSet();
    for (TreePath leaf : leaves) {
      int index = TreePaths.indexOf(leaf, element);
      if (index < 0) {
        continue;
      }
      parents.add(TreePaths.headPath(leaf, index));
    }
    return parents.toArray(new TreePath[parents.size()]);
  }

  @Override
  public boolean hasChildren(@Nullable TreePath branch) {
    if (branch == null) {
      return false;
    }
    for (TreePath leaf : leaves) {
      if (leaf.getSegmentCount() > branch.getSegmentCount()
          && leaf.startsWith(branch, null)) {
        return true;
      }
    }
    return false;
  }

  /**
   * When input changes, the internal data of this content provider will be
   * updated.
   */
  @Override
  public void inputChanged(
      Viewer viewer,
      @Nullable Object oldInput,
      @Nullable Object newInput) {

    leaves = ImmutableList.copyOf(builder.build(newInput));
    
    setChanged();
    notifyObservers();
  }

  /**
   * Gets the children of the given parent.
   * @param branch the parent.
   * @return a collection of children, or an empty collection if no children.
   */
  private Set<Object> childrenOf(@Nullable TreePath branch) {
    if (branch == null) {
      return Collections.emptySet();
    }
    Set<Object> children = newLinkedHashSet();
    for (TreePath leaf : leaves) {
      if (leaf.getSegmentCount() > branch.getSegmentCount()
          && leaf.startsWith(branch, null)) {
        children.add(leaf.getSegment(branch.getSegmentCount()));
      }
    }
    return children;
  }
}
