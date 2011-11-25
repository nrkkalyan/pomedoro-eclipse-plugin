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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import com.google.common.collect.Lists;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * Tests for a {@link TreePathContentProvider}.
 */
public class TreePathContentProviderTest {

  private static final Object[] EMPTY_ARRAY = {};

  private TreePathContentProvider content;

  @Before
  public void before() {
    TreePath path1 = new TreePath(new Object[]{"1", "2", "3"});
    TreePath path2 = new TreePath(new Object[]{"a", "b"});
    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    when(builder.build(any())).thenReturn(Arrays.asList(path1, path2));
    content = create(builder);

    // Call once to initialize:
    content.inputChanged(null, null, null);
  }

  @Test
  public void getChildrenShouldReturnAnEmptyArrayIfParentHasNoChildren() {
    // Given a non-null parent has no children:
    TreePath parent = new TreePath(new Object[]{this});

    // When asking for the children of the parent:
    Object[] children = content.getChildren(parent);

    // The an empty array is returned:
    assertThat(children, is(EMPTY_ARRAY));
  }

  @Test
  public void getChildrenShouldReturnAnEmtpyArrayIfParentIsNull() {
    // Given a parent path is null:
    TreePath parent = null;

    // When asking for the children of the null object:
    Object[] children = content.getChildren(parent);

    // The an empty array should be returned as the children:
    assertThat(children, is(EMPTY_ARRAY));
  }

  @Test
  public void getChildrenShouldReturnTheDistinctChildrenOfAParent() {
    // Given a tree path has 3 children, 2 of which are equal:
    TreePath branch = new TreePath(new Object[]{0, 1});
    TreePath leaf1 = branch.createChildPath(2);
    TreePath leaf2 = branch.createChildPath(3);
    TreePath leaf3 = branch.createChildPath(3);
    Object input = new Object();

    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    given(builder.build(input)).willReturn(asList(leaf1, leaf2, leaf3));
    content = create(builder);

    // When asking for the children of the parent path:
    content.inputChanged(null, null, input); // Sets the input.
    List<Object> children = newArrayList(content.getChildren(branch));

    // Then the distinct children should be returned:
    List<Object> expected = newArrayList(newHashSet( // Get unique children
        leaf1.getLastSegment(),
        leaf2.getLastSegment(),
        leaf3.getLastSegment()));
    assertThat(children, is(equalTo(expected)));
  }

  @Test
  public void getElementsShouldReturnAnEmptyArrayIfAllLeavesAreEmptyPaths() {
    // Given all tree paths are empty paths:
    Object input = new Object();
    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    given(builder.build(input)).willReturn(
        asList(TreePath.EMPTY, new TreePath(new Object[0])));
    content = create(builder);

    // When set and ask for elements:
    content.inputChanged(null, null, input);
    Object[] elements = content.getElements(input);

    // Then an empty array is returned:
    assertThat(elements, is(EMPTY_ARRAY));
  }

  @Test
  public void getElementsShouldReturnAnEmptyArrayIfInputHasNoElements() {
    // Given an non-null input element that has no child elements:
    Object input = this;
    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    given(builder.build(any())).willReturn(Collections.<TreePath> emptyList());
    content = create(builder);
    content.inputChanged(null, null, null);

    // When asking for the child elements of the input:
    Object[] elements = content.getElements(input);

    // Then an empty array is returned:
    assertThat(elements, is(EMPTY_ARRAY));
  }

  @Test
  public void getElementsShouldReturnTheDistinctElementsOfAllLeaves() {
    // Given we have 3 leaves, the 0th segments of 2 of them are equal::
    TreePath leaf1 = new TreePath(new Object[]{0, 1});
    TreePath leaf2 = new TreePath(new Object[]{2, 3});
    TreePath leaf3 = new TreePath(new Object[]{9, 8});
    Object input = new Object();

    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    given(builder.build(input)).willReturn(asList(leaf1, leaf2, leaf3));
    content = create(builder);

    // When asking for elements of the new input:
    content.inputChanged(null, null, input);
    List<Object> actual = newArrayList(content.getElements(input));

    // Then the distinct first elements of each leave is returned:
    List<Object> expected = newArrayList(newHashSet( // Get distinct elements
        leaf1.getFirstSegment(),
        leaf2.getFirstSegment(),
        leaf3.getFirstSegment()));
    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void getParentsShouldReturnAnEmptyArrayIsChildHasNoParents() {
    // Given a non-null child that has no possible parents:
    Object child = this;

    // When asking for the possible parents of the child:
    TreePath[] parents = content.getParents(child);

    // Then an empty array is returned as the parents:
    assertThat(parents, is(EMPTY_ARRAY));
  }

  @Test
  public void getParentsShouldReturnAnEmptyArrayIsChildIsNull() {
    // Given a child is null:
    Object child = null;

    // When asking for possible parents of the child:
    TreePath[] parents = content.getParents(child);

    // Then an empty array is returned:
    assertThat(parents, is(EMPTY_ARRAY));
  }

  @Test
  public void getParentsShouldReturnTheParentsOfAChild() {
    // Given we have two leaves that have the same last segments:
    Object child = "Child";
    TreePath leaf1 = new TreePath(new Object[]{0, child});
    TreePath leaf2 = new TreePath(new Object[]{2, child});

    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    given(builder.build("input")).willReturn(asList(leaf1, leaf2));
    content = create(builder);

    // When getting the parents of the child:
    content.inputChanged(null, null, "input");
    Set<TreePath> actual = newHashSet(content.getParents(child));

    // Then the parent path of the two leaves should be returned:e
    Set<TreePath> expected = newHashSet(
        leaf1.getParentPath(),
        leaf2.getParentPath());
    assertThat(actual, is(expected));
  }

  @Test
  public void getShouldReturnAnEmptyCollectionWhenFirstConstructed() {
    assertThat(create(mock(ITreePathBuilder.class)).get().isEmpty(), is(true));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void getShouldReturnAnImmutableCollection() {
    content.get().add(new TreePath(new Object[]{}));
  }

  @Test
  public void getShouldReturnTheTreePathsCurrentlyInUse() {
    List<TreePath> paths = Lists.newArrayList();
    paths.add(new TreePath(new Object[]{}));
    paths.add(new TreePath(new Object[]{"a", "b"}));

    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    given(builder.build(any())).willReturn(paths);
    content = create(builder);
    content.inputChanged(null, null, null);

    assertEquals(paths, content.get());
  }

  @Test
  public void hasChildrenShouldReturnFalseIfTheGivenPathHasNoChildren() {
    // Given a non-null parent has no children:
    TreePath parent = new TreePath(new Object[]{this});

    // When asking whether the parent has children or not:
    boolean hasChildren = content.hasChildren(parent);

    // Then false is returned:
    assertThat(hasChildren, is(false));
  }

  @Test
  public void hasChildrenShouldReturnFalseIfTheGivenPathIsNull() {
    // Given a parent is null:
    TreePath parent = null;

    // When asking whether the parent has children:
    boolean hasChildren = content.hasChildren(parent);

    // Then false is returned:
    assertThat(hasChildren, is(not(true)));
  }

  @Test
  public void hasChildrenShouldReturnTrueIfTheGivenPathHasChildren() {
    // Given a parent has children:
    TreePath parent = new TreePath(new Object[]{0, 1, 2});
    TreePath leaf = parent.createChildPath(100);

    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    given(builder.build(any())).willReturn(Arrays.asList(leaf));

    content = create(builder);

    // When asking whether the parent has children:
    content.inputChanged(null, null, "");
    boolean hasChildren = content.hasChildren(parent);

    // Then true is returned:
    assertThat(hasChildren, is(true));
  }

  @Test
  public void inputChangedShouldAcceptNullableNewInput() {
    content.inputChanged(mock(Viewer.class), "", null); // No exception
  }

  @Test
  public void inputChangedShouldAcceptNullableOldInput() {
    content.inputChanged(mock(Viewer.class), null, ""); // No exception
  }

  @Test
  public void inputChangedShouldAcceptNullableViewer() {
    content.inputChanged(null, "", ""); // No exception
  }

  @Test
  public void shouldAlwaysReturnTheDataOfTheLatestInput() {
    // Given that an input has already been set:
    List<TreePath> input1 = asList(new TreePath(new Object[]{"a", "b"}));
    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    given(builder.build(input1)).willReturn(input1);
    content = create(builder);
    content.inputChanged(null, null, input1);

    // When new input is set, gets the new root elements:
    TreePath input2 = new TreePath(new Object[]{0, 1});
    given(builder.build(input2)).willReturn(asList(input2));
    content.inputChanged(null, input1, input2);
    Object[] elements = content.getElements(input2);

    // Then data from new input should be returned:
    Object[] expected = new Object[]{input2.getFirstSegment()};
    assertThat(elements, is(expected));
  }

  @Test
  public void shouldBuildTheTreePathsOnInputChange() {
    // Given a content provider is built with a tree path builder:
    Object input = new Object();
    ITreePathBuilder builder = mock(ITreePathBuilder.class);
    content = create(builder);

    // When input is set:
    content.inputChanged(null, null, input);

    // Then the content provider should have called the tree path builder:
    verify(builder).build(input);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowAnExceptionIfConstructedWithoutATreePathBulder() {
    content = create(null);
  }

  @Test
  public void shouldNotifyObserversOnInputChange() {
    final int[] updateCount = {0};
    Observer observer = new Observer() {
      @Override
      public void update(Observable arg0, Object arg1) {
        updateCount[0]++;
      }
    };
    TreePathContentProvider provider = create(mock(ITreePathBuilder.class));
    provider.addObserver(observer);
    
    provider.inputChanged(null, null, null);
    assertThat(updateCount[0], is(1));
  }
  
  /**
   * Creates a content provider for testing.
   */
  private TreePathContentProvider create(ITreePathBuilder builder) {
    return new TreePathContentProvider(builder);
  }

}
