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

import rabbit.data.access.model.IJavaData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.ui.IProvider;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.treebuilders.JavaDataTreeBuilder.IJavaDataProvider;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.viewers.ITreePathBuilder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.TreePath;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;

public final class JavaDataTreeBuilderTest extends
    AbstractDataTreeBuilderTest<IJavaData> {

  String methodId = "=Proj/src<com.example{MyPlugin.java[MyPlugin~getDefault";

  IMethod method;
  Duration duration;
  LocalDate date;
  WorkspaceStorage ws;

  IJavaData data;

  @Before
  public void setup() {
    duration = new Duration(1024);
    date = new LocalDate().minusDays(1);
    ws = new WorkspaceStorage(new Path(".a"), new Path("/a"));
    method = (IMethod) JavaCore.create(methodId);

    data = mock(IJavaData.class);
    given(data.get(IJavaData.DATE)).willReturn(date);
    given(data.get(IJavaData.DURATION)).willReturn(duration);
    given(data.get(IJavaData.JAVA_ELEMENT)).willReturn(method);
    given(data.get(IJavaData.WORKSPACE)).willReturn(ws);
  }

  @Test
  public void shouldCorrectlyBuildAPathWithInnerClassMethod() {
    // A method inside an inner class:
    method = (IMethod) JavaCore
        .create("=J/src<main{Main.java[Main[Inner~hello");
    ICategory[] categories = {
        Category.DATE,
        Category.WORKSPACE,
        Category.PROJECT,
        Category.JAVA_PACKAGE_ROOT,
        Category.JAVA_PACKAGE,
        Category.JAVA_TYPE_ROOT,
        Category.JAVA_MEMBER
    };

    List<TreePath> expected = asList(newPath(
        date,
        ws,
        method.getJavaProject(),
        getElement(method, IJavaElement.PACKAGE_FRAGMENT_ROOT),
        getElement(method, IJavaElement.PACKAGE_FRAGMENT),
        method.getTypeRoot(), // Main.java
        method.getParent().getParent(), // Main
        method.getParent(), // Inner
        method,
        duration
        ));

    data = mock(IJavaData.class);
    given(data.get(IJavaData.DATE)).willReturn(date);
    given(data.get(IJavaData.DURATION)).willReturn(duration);
    given(data.get(IJavaData.JAVA_ELEMENT)).willReturn(method);
    given(data.get(IJavaData.WORKSPACE)).willReturn(ws);

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    IJavaDataProvider input = mock(IJavaDataProvider.class);
    given(input.get()).willReturn(asList(data));
    List<TreePath> actual = builder.build(input);

    assertThat(toString(actual, expected), actual, equalTo(expected));
  }

  @Override
  public void shouldCorrectlyBuildASinglePath() {
    ICategory[] categories = {
        Category.DATE,
        Category.WORKSPACE,
        Category.PROJECT,
        Category.JAVA_PACKAGE_ROOT,
        Category.JAVA_PACKAGE,
        Category.JAVA_TYPE_ROOT,
        Category.JAVA_MEMBER
    };

    List<TreePath> expected = asList(newPath(
        date,
        ws,
        method.getJavaProject(),
        getElement(method, IJavaElement.PACKAGE_FRAGMENT_ROOT),
        getElement(method, IJavaElement.PACKAGE_FRAGMENT),
        method.getTypeRoot(),
        method.getParent(),
        method,
        duration
        ));

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    IJavaDataProvider input = mock(IJavaDataProvider.class);
    given(input.get()).willReturn(asList(data));
    List<TreePath> actual = builder.build(input);

    assertThat(toString(actual, expected), actual, equalTo(expected));
  }

  @Override
  public void shouldCorrectlyBuildMultiplePaths() {
    WorkspaceStorage ws2 = new WorkspaceStorage(new Path(".b"), null);
    LocalDate date2 = date.minusDays(2);
    IMethod method2 = (IMethod) JavaCore
        .create("=Proj2/src2<com.example2{My.java[My~getDefault");
    Duration duration2 = duration.minus(100);

    ICategory[] categories = {Category.WORKSPACE, Category.JAVA_TYPE_ROOT};
    List<TreePath> expected = asList(
        newPath(ws, method.getTypeRoot(), duration),
        newPath(ws2, method2.getTypeRoot(), duration2));

    IJavaData data2 = mock(IJavaData.class);
    given(data2.get(IJavaData.DATE)).willReturn(date2);
    given(data2.get(IJavaData.DURATION)).willReturn(duration2);
    given(data2.get(IJavaData.JAVA_ELEMENT)).willReturn(method2);
    given(data2.get(IJavaData.WORKSPACE)).willReturn(ws2);

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    IJavaDataProvider input = mock(IJavaDataProvider.class);
    given(input.get()).willReturn(asList(data, data2));
    List<TreePath> actual = builder.build(input);

    assertThat(actual.size(), equalTo(expected.size()));
    assertThat(toString(actual, expected),
        actual, hasItems(expected.toArray(new TreePath[0])));
  }

  @Override
  public void shouldRetainIdenticalPaths() {
    ICategory[] categories = {Category.DATE, Category.WORKSPACE};
    List<TreePath> expected = asList(
        newPath(date, ws, duration),
        newPath(date, ws, duration));

    ICategoryProvider provider = mock(ICategoryProvider.class);
    given(provider.getSelected()).willReturn(asList(categories));
    ITreePathBuilder builder = create(provider);

    IJavaDataProvider input = mock(IJavaDataProvider.class);
    given(input.get()).willReturn(asList(data, data));
    List<TreePath> actual = builder.build(input);

    assertThat(toString(actual, expected), actual, equalTo(expected));
  }

  @Override
  protected ITreePathBuilder create(ICategoryProvider p) {
    return new JavaDataTreeBuilder(p);
  }

  @Override
  protected IProvider<IJavaData> input(
      final Collection<IJavaData> inputData) {
    return new IJavaDataProvider() {
      @Override
      public Collection<IJavaData> get() {
        return inputData;
      }
    };
  }

  private IJavaElement getElement(IJavaElement element, int elementType) {
    while ((element != null) && (element.getElementType() != elementType)) {
      element = element.getParent();
    }
    return element;
  }
}
