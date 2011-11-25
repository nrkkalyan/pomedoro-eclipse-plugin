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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.junit.Before;
import org.junit.Test;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * @see TreePathPatternFilter
 */
public final class TreePathPatternFilterTest {

  TreePathPatternFilter filter;

  @Before
  public void create() {
    filter = new TreePathPatternFilter(new LabelProvider());
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowAnExceptionIfTryToConstructWithoutALabelProvider() {
    new TreePathPatternFilter(null);
  }

  @Test
  public void shouldHideTheElementIfTheLabelOfTheElementDoesNotMatchTheFilterText() {
    TreePath path = new TreePath(new Object[]{"parent", "child"});

    ITreePathContentProvider contentProvider = mock(ITreePathContentProvider.class);
    given(contentProvider.getChildren(path.getParentPath()))
        .willReturn(new Object[]{path.getLastSegment()});
    given(contentProvider.getChildren(path))
        .willReturn(new Object[0]);
    given(contentProvider.getParents(path.getLastSegment()))
        .willReturn(new TreePath[]{path.getParentPath()});

    TreeViewer v = mock(TreeViewer.class);
    given(v.getContentProvider()).willReturn(contentProvider);

    filter.setPattern("not a match");
    assertThat(filter.isElementVisible(v, path.getLastSegment()), is(FALSE));
  }

  @Test
  public void shouldShowTheElementIfTheLabelOfTheElementMatchesTheFilterText() {
    TreePath path = new TreePath(new Object[]{"parent", "child"});

    ITreePathContentProvider contentProvider = mock(ITreePathContentProvider.class);
    given(contentProvider.getChildren(path.getParentPath()))
        .willReturn(new Object[]{path.getLastSegment()});
    given(contentProvider.getParents(path.getLastSegment()))
        .willReturn(new TreePath[]{path.getParentPath()});

    TreeViewer v = mock(TreeViewer.class);
    given(v.getContentProvider()).willReturn(contentProvider);

    filter.setPattern("child");
    assertThat(filter.isElementVisible(v, path.getLastSegment()), is(TRUE));
  }
}
