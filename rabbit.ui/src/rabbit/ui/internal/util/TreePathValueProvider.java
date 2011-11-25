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
package rabbit.ui.internal.util;

import rabbit.ui.IProvider;
import rabbit.ui.internal.viewers.IValueProvider;
import rabbit.ui.internal.viewers.TreePaths;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.alwaysTrue;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;

import org.eclipse.jface.viewers.TreePath;

import java.util.Collection;
import java.util.Observable;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * A value provider for tree paths.
 * <p>
 * {@link #getValue(Object)} can be used to find out the value of a particular
 * tree path, the path doesn't need to be a complete path from the root of the
 * tree to a leaf node, it can be a sub path such that it's a parent path of one
 * or more child paths, than the value of this parent path will be the sum of
 * all its child paths.
 * </p>
 * <p>
 * This value provider also take a {@link IProvider} for getting the paths, the
 * paths supplied should be complete paths (all are tree leaves), and the value
 * of each of these paths is determined by a {@link IConverter}.
 * </p>
 * <p>
 * This class also accepts a {@link ICategorizer} for getting the category of
 * each path, if the category of a given path is equal to
 * {@link #getVisualCategory()} then {@link #shouldPaint(Object)} will return
 * true on the given path.
 * </p>
 */
public final class TreePathValueProvider extends Observable
    implements IValueProvider, IVisualProvider {

  private final ICategorizer categorizer;
  private final IProvider<TreePath> pathProvider;
  private final IConverter<TreePath> converter;

  private ICategory visual;
  private long max;

  /**
   * Constructor.
   * @param categorizer for categorizing tree paths.
   * @param treePathProvider for getting the tree leaf paths.
   * @param converter for getting value of a tree path supplied by
   *        {@code treePathProvider}.
   * @throws NullPointerException if any argument is null.
   */
  public TreePathValueProvider(
      ICategorizer categorizer,
      IProvider<TreePath> treePathProvider,
      IConverter<TreePath> converter) {
    this(categorizer, treePathProvider, converter, null);
  }

  /**
   * @param categorizer for categorizing tree paths.
   * @param treePathProvider for getting the tree leaf paths.
   * @param converter for getting value of a tree path supplied by
   *        {@code treePathProvider}.
   * @param visualCategory the default visual category.
   * @throws NullPointerException if any of
   *         {@code categorizer, treePathProvider, converter} is null.
   */
  public TreePathValueProvider(
      ICategorizer categorizer,
      IProvider<TreePath> treePathProvider,
      IConverter<TreePath> converter,
      @Nullable ICategory visualCategory) {

    this.categorizer = checkNotNull(categorizer, "categorizer");
    this.pathProvider = checkNotNull(treePathProvider, "treePathProvider");
    this.converter = checkNotNull(converter, "converter");
    this.visual = visualCategory;
  }

  /**
   * @return the categorizer for categorizing the elements.
   */
  public ICategorizer getCategorizer() {
    return categorizer;
  }

  /**
   * @return the converter for converting a tree leaf to a value.
   */
  public IConverter<TreePath> getConverter() {
    return converter;
  }

  @Override
  public long getMaxValue() {
    return max;
  }

  /**
   * @return the provider that provides tree leaves.
   */
  public IProvider<TreePath> getProvider() {
    return pathProvider;
  }

  /**
   * Gets the value of the given tree path. If the given tree path is a parent
   * path of one or more child paths, then the sum of the children values will
   * be returned.
   * @param element the tree path to get the value for.
   * @return the value of the tree path, may be zero. Zero is also returned if
   *         the given element is not a tree path.
   */
  @Override
  public long getValue(@Nullable Object element) {
    if (!(element instanceof TreePath)) {
      return 0;
    }
    return getValue((TreePath) element, getProvider().get());
  }

  @Override
  public ICategory getVisualCategory() {
    return visual;
  }

  /**
   * Sets the max value using a category. If the category of a tree node is
   * equal to the given category, and the value of that path (from the root to
   * that element) is the highest of all, that the max value will be set to that
   * value.
   * @param category the category.
   */
  public void setMaxValue(ICategory category) {
    setMaxValue(category, alwaysTrue());
  }

  /**
   * Sets the max value using a category a predicate. If the category of a tree
   * node is equal to the given category, and the
   * {@link Predicate#apply(Object)} returns true on the element, and the value
   * of that path (from the root to that element) is the highest of all, that
   * the max value will be set to that value.
   * @param category the category.
   * @param predicate the predicate to select wanted elements.
   */
  public void setMaxValue(ICategory category,
      Predicate<? super Object> predicate) {
    Set<TreePath> paths = Sets.newHashSet();
    Collection<TreePath> leaves = getProvider().get();
    for (TreePath leaf : leaves) {
      for (int i = 0; i < leaf.getSegmentCount(); ++i) {

        Object segment = leaf.getSegment(i);
        ICategory another = getCategorizer().getCategory(segment);
        if (Objects.equal(category, another) && predicate.apply(segment)) {
          paths.add(TreePaths.headPath(leaf, i + 1));
          break;
        }
      }
    }

    long max = 0;
    for (TreePath path : paths) {
      long value = getValue(path, leaves);
      if (value > max) {
        max = value;
      }
    }
    setMaxValue(max);
  }

  /**
   * Sets the max value representing the highest value.
   * @param max the new value.
   */
  public void setMaxValue(long max) {
    this.max = max;
  }

  /**
   * Sets the visual category. If the category is not supported by the
   * categorizer then it will be ignored and this method will return false. If
   * {@link #getVisualCategory()} returns a different category after this change
   * then observers of this instance will get notified.
   * @param category the new category.
   * @return true if the category is accepted, false otherwise.
   */
  @Override
  public boolean setVisualCategory(ICategory category) {
    if (getCategorizer().hasCategory(category)) {
      ICategory old = visual;
      visual = category;
      if (!Objects.equal(old, visual)) {
        setChanged();
        notifyObservers();
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean shouldPaint(Object element) {
    return Objects.equal(
        getVisualCategory(),
        getCategorizer().getCategory(element));
  }

  /**
   * Gets the value of the given tree path. If the given tree path is a parent
   * path of one or more child paths, then the sum of the children values will
   * be returned.
   * @return the value of the tree path, may be zero.
   */
  private long getValue(TreePath path, Collection<TreePath> leaves) {
    long value = 0;
    for (TreePath leaf : leaves) {
      if (leaf.startsWith(path, null)) {
        value += converter.convert(leaf);
      }
    }
    return value;
  }
}