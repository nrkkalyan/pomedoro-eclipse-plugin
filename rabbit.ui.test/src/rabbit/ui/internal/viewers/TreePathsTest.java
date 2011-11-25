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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.jface.viewers.TreePath;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests for {@link TreePaths}
 */
public class TreePathsTest {

  private static TreePath newPath() {
    return new TreePath(new Object[]{"1", "2", "3"});
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void headPathShouldReturnTheCorrectSubPath() {
    TreePath path = new TreePath(new Object[]{"a", "b", "c"});
    TreePath expected = path.getParentPath();
    TreePath actual = TreePaths.headPath(path, path.getSegmentCount() - 1);
    assertThat(actual, equalTo(expected));
  }

  @Test
  public void headPathShouldThrowAnExceptionIfIndexIsGreaterThanThePathLength() {
    TreePath path = newPath();
    thrown.expect(IllegalArgumentException.class);
    TreePaths.headPath(path, path.getSegmentCount() + 1);
  }
  
  @Test
  public void headPathShouldReturnAnEqualPathIfIndexIsEqualToThePathLength() {
    TreePath path = newPath();
    assertThat(TreePaths.headPath(path, path.getSegmentCount()), equalTo(path));
  }

  @Test
  public void headPathShouldThrowAnExceptionIfIndexIsNegative() {
    TreePath path = newPath();
    thrown.expect(IllegalArgumentException.class);
    TreePaths.headPath(path, -1);
  }

  @Test
  public void indexOfShouldReturnNegative1IfNotFound() {
    TreePath path = new TreePath(new Object[]{"a", "b", "c"});
    assertThat(TreePaths.indexOf(path, "d"), is(-1));
  }

  @Test
  public void indexOfShouldReturnTheCorrectIndex() {
    TreePath path = new TreePath(new Object[]{"a", "b", "c"});
    int index = 1; // Index of "b"
    assertThat(TreePaths.indexOf(path, "b"), is(index));
  }

  @Test
  public void indexOfShouldReturnTheFirstIndex() {
    TreePath path = new TreePath(new Object[]{"a", "a", "a"});
    assertThat(TreePaths.indexOf(path, "a"), is(0));
  }

  @Test
  public void indexOfShouldThrowAnExceptionIfPathIsNull() {
    thrown.expect(NullPointerException.class);
    TreePaths.indexOf(null, "");
  }

  @Test
  public void indexOfShouldThrowAnExceptionIfSegmentIsNull() {
    TreePath path = newPath();
    thrown.expect(NullPointerException.class);
    TreePaths.indexOf(path, null);
  }
}
