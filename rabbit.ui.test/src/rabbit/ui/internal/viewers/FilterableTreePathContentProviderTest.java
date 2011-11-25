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

import rabbit.ui.internal.viewers.FilterableTreePathContentProvider;
import rabbit.ui.internal.viewers.ForwardingTreePathContentProvider;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests for a {@link FilterableTreePathContentProvider}.
 * @see FilterableTreePathContentProviderFilteringTest
 */
public class FilterableTreePathContentProviderTest {

  private static final Object[] EMPTY_ARRAY = {};

  @Test(expected = NullPointerException.class)
  public void constructorShouldThrowAnExceptionIfProviderIsNull() {
    create(null);
  }
  
  @SuppressWarnings("unchecked")
  @Test(expected = NullPointerException.class)
  public void constructorShouldThrowAnExceptionIfFiltersContainNull() {
    create(mock(ITreePathContentProvider.class), mock(Predicate.class), null);
  }

  @Test
  public void getChildrenShouldRetainDuplicateChildrenWhenFiltering() {
    Object[] originalChildren = {Integer.valueOf(0), Integer.valueOf(0), "a"};
    Object[] expectedChildren = {Integer.valueOf(0), Integer.valueOf(0)};

    TreePath parent = newPath();
    FilterableTreePathContentProvider p = create(parent, originalChildren);
    p.addFilter(Predicates.instanceOf(String.class));

    assertThat(p.getChildren(parent), equalTo(expectedChildren));
  }

  @Test
  public void getChildrenShouldRetainDuplicateChildrenWhenNoFiltering() {
    Object[] children = {0, 0, 0};
    TreePath parent = newPath();
    assertThat(create(parent, children).getChildren(parent), equalTo(children));
  }

  @Test
  public void getChildrenShouldRetainTheOrderOfTheChildrenWhenFiltering() {
    Object[] original = {"3", Integer.valueOf(2), "1"};
    Object[] expected = {"3", "1"};

    TreePath parent = newPath();
    FilterableTreePathContentProvider provider = create(parent, original);
    provider.addFilter(Predicates.instanceOf(Integer.class));

    assertThat(provider.getChildren(parent), equalTo(expected));
  }

  @Test
  public void getChildrenShouldRetainTheOrderOfTheChildrenWhenNoFiltering() {
    Object[] children = {9, 29, "adf", 0};
    TreePath parent = newPath();
    ITreePathContentProvider p = create(parent, children);
    assertThat(p.getChildren(parent), equalTo(children));
  }

  @Test
  public void getChildrenShouldReturnAllChildrenIfNoFiltering() {
    Object[] expectedChildren1 = {Integer.valueOf(0), "100"};
    Object[] expectedChildren2 = {"1", "2"};

    TreePath parent1 = newPath("1");
    TreePath parent2 = newPath("2");
    ITreePathContentProvider mock = mock(ITreePathContentProvider.class);
    given(mock.getChildren(parent1)).willReturn(expectedChildren1);
    given(mock.getChildren(parent2)).willReturn(expectedChildren2);
    FilterableTreePathContentProvider provider = create(mock);

    assertThat(provider.getChildren(parent1), equalTo(expectedChildren1));
    assertThat(provider.getChildren(parent2), equalTo(expectedChildren2));
  }

  @Test
  public void getChildrenShouldReturnAnEmptyArrayIfAllChildrenAreFilteredOut() {
    Object[] original = {"1", "2", "3"};
    Object[] expected = {};

    TreePath parent = newPath();
    FilterableTreePathContentProvider provider = create(parent, original);
    provider.addFilter(Predicates.alwaysTrue()); // Filters everything

    assertThat(provider.getChildren(parent), equalTo(expected));
  }

  @Test
  public void getChildrenShouldReturnAnEmptyArrayIfParentIsNull() {
    assertThat(create().getChildren(null), equalTo(EMPTY_ARRAY));
  }

  @Test
  public void getChildrenShouldReturnAnEmptyArrayParentHasNoChildren() {
    assertThat(create().getChildren(newPath()), equalTo(EMPTY_ARRAY));
  }

  @Test
  public void getChildrenShouldReturnFilteredChildrenWhenFiltering() {
    Object[] originalChildren = {Integer.valueOf(0), "string"};
    Object[] expectedChildren = {Integer.valueOf(0)};

    TreePath parent = newPath();
    FilterableTreePathContentProvider p = create(parent, originalChildren);
    p.addFilter(Predicates.instanceOf(String.class));

    assertThat(p.getChildren(parent), equalTo(expectedChildren));
  }

  @Test
  public void getElementsShouldRetainDuplicateElementsWhenFiltering() {
    Object[] originalElements = {0, "abc", 0, "def"};
    Object[] expectedElements = {0, 0};

    Object input = new Object();
    FilterableTreePathContentProvider p = create(input, originalElements);
    p.addFilter(Predicates.instanceOf(String.class));

    assertThat(p.getElements(input), equalTo(expectedElements));
  }

  @Test
  public void getElementsShouldRetainDuplicateElementsWhenNoFiltering() {
    Object input = new Object();
    Object[] elements = {"", "1", "", "1"};
    FilterableTreePathContentProvider provider = create(input, elements);
    assertThat(provider.getElements(input), equalTo(elements));
  }

  @Test
  public void getElementsShouldRetainTheCorrectElementsWhenNoFiltering() {
    Object input = "";
    Object[] elements = {"1", "2", "3"};
    FilterableTreePathContentProvider p = create(input, elements);
    assertThat(p.getElements(input), equalTo(elements));
  }

  @Test
  public void getElementsShouldRetainTheOrderOfTheElementsWhenFiltering() {
    Object[] originalElements = {"1", "2", "3"};
    Object[] expectedChildren = {"1", "3"};

    Object input = "";
    FilterableTreePathContentProvider p = create(input, originalElements);
    p.addFilter(Predicates.<Object> equalTo("2"));

    assertThat(p.getElements(input), equalTo(expectedChildren));
  }

  @Test
  public void getElementsShouldReturnAnEmptyArrayIfAllElementsAreFilteredOut() {
    Object[] originalElements = {"1", "2", "3"};
    Object[] expectedElements = {};

    Object input = "";
    FilterableTreePathContentProvider p = create(input, originalElements);
    p.addFilter(Predicates.instanceOf(String.class));

    assertThat(p.getElements(input), equalTo(expectedElements));
  }

  @Test
  public void getElementsShouldReturnAnEmptyArrayIfInputHasNoElements() {
    assertThat(create().getElements(""), equalTo(EMPTY_ARRAY));
  }

  @Test
  public void getElementsShouldReturnAnEmptyArrayIfInputIsNull() {
    assertThat(create().getElements(null), equalTo(EMPTY_ARRAY));
  }

  @Test
  public void getElementsShouldReturnFilteredElementsWhenFiltering() {
    Object[] originalElements = {Integer.valueOf(0), "2", "3"};
    Object[] expectedElements = {"2", "3"};

    Object input = "";
    FilterableTreePathContentProvider p = create(input, originalElements);
    p.addFilter(Predicates.instanceOf(Integer.class));

    assertThat(p.getElements(input), equalTo(expectedElements));
  }

  @Test
  public void hasChildrenShouldReturnFalseIfAllChildrenAreFilteredOut() {
    TreePath parent = newPath();
    Object[] children = {Integer.valueOf(0), Long.valueOf(1l), "str"};
    FilterableTreePathContentProvider p = create(parent, children);
    p.addFilter(Predicates.instanceOf(Object.class));
    assertThat(p.hasChildren(newPath()), is(false));
  }

  @Test
  public void hasChildrenShouldReturnFalseIfParentHasNoChildren() {
    assertThat(create().hasChildren(TreePath.EMPTY), is(false));
  }

  @Test
  public void hasChildrenShouldReturnFalseIfParentIsNull() {
    assertThat(create().hasChildren(null), is(false));
  }

  @Test
  public void hasChildrenShouldReturnTrueIfNotAllChildrenAreFilteredOut() {
    TreePath parent = newPath();
    Object[] children = {Integer.valueOf(0), "string"};
    FilterableTreePathContentProvider p = create(parent, children);
    p.addFilter(Predicates.instanceOf(String.class));
    assertThat(p.hasChildren(parent), is(true));
  }

  @Test
  public void hasChildrenShouldReturnTrueIfParentHasChildrenAndThereIsNoFilter() {
    TreePath parent = newPath();
    Object[] children = {Integer.valueOf(0)};
    FilterableTreePathContentProvider p = create(parent, children);
    assertThat(p.hasChildren(parent), is(true));
  }

  /**
   * @see FilterableTreePathContentProvider#FilterableTreePathContentProvider(ITreePathContentProvider,
   *      Predicate...)
   */
  protected FilterableTreePathContentProvider create(
      ITreePathContentProvider p, Predicate<? super Object>... filters) {
    return new FilterableTreePathContentProvider(p, filters);
  }

  private final ForwardingTreePathContentProvider create() {
    ITreePathContentProvider p = mock(ITreePathContentProvider.class);
    given(p.getChildren(Mockito.<TreePath> any())).willReturn(EMPTY_ARRAY);
    given(p.getElements(Mockito.any())).willReturn(EMPTY_ARRAY);
    given(p.hasChildren(Mockito.<TreePath> any())).willReturn(Boolean.FALSE);
    return create(p);
  }

  private FilterableTreePathContentProvider create(Object input, Object[] elements) {
    ITreePathContentProvider mock = mock(ITreePathContentProvider.class);
    given(mock.getElements(input)).willReturn(elements);
    return create(mock);
  }

  private FilterableTreePathContentProvider create(TreePath parent, Object[] children) {
    ITreePathContentProvider mock = mock(ITreePathContentProvider.class);
    given(mock.getChildren(parent)).willReturn(children);
    return create(mock);
  }

  /**
   * @return A {@link TreePath} containing the given segments.
   */
  private TreePath newPath(Object... segments) {
    return new TreePath(segments);
  }
}