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
import rabbit.ui.IProvider;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.viewers.ITreePathBuilder;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.newArrayListWithCapacity;

import com.google.common.base.Joiner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.viewers.TreePath;
import org.junit.Before;
import org.junit.Test;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * An abstract test case defined common tests for the internal tree builders.
 * These tree builders all take an {@link ICategoryProvider} as constructor
 * argument, and all accept input as some kind of {@link IProvider}.
 * 
 * @see AbstractDataTreeBuilder
 */
public abstract class AbstractDataTreeBuilderTest<T> {

  private ITreePathBuilder builder;

  @Before
  public void before() {
    Collection<ICategory> noCategories = emptySet();
    ICategoryProvider p = mock(ICategoryProvider.class);
    given(p.getAllSupported()).willReturn(noCategories);
    builder = create(p);
  }

  @Test(expected = NullPointerException.class)
  public void constructorShouldThrowAnExceptionIfTheCategoryProviderIsNull() {
    // Testing the abstract class only:
    Map<ICategory, IKey<Object>> keys = emptyMap();
    new AbstractDataTreeBuilder<IData>(null, keys) {
      @Override
      protected Collection<IData> getData(Object input) {
        return emptySet();
      }
    };
  }

  @Test(expected = NullPointerException.class)
  public void constructorShouldThrowAnExceptionIfTheKeyMapIsNull() {
    // Testing the abstract class only:
    ICategoryProvider p = mock(ICategoryProvider.class);
    given(p.getAllSupported()).willReturn(Collections.<ICategory> emptySet());
    given(p.getSelected()).willReturn(Collections.<ICategory> emptyList());
    given(p.getUnselected()).willReturn(Collections.<ICategory> emptySet());
    new AbstractDataTreeBuilder<IData>(p, null) {
      @Override
      protected Collection<IData> getData(Object input) {
        return emptySet();
      }
    };
  }

  @Test
  public abstract void shouldCorrectlyBuildASinglePath();

  @Test
  public abstract void shouldCorrectlyBuildMultiplePaths();

  @Test
  public void shouldIgnoreTheInputIfTheInputReturnsANullCollection() {
    // Given an input that will return null:
    IProvider<T> dataProvider = input(null);

    // When asking to build from such an input:
    List<TreePath> pathsBuilt = builder.build(dataProvider);

    // Then an empty collection is returned without failing:
    assertThat(pathsBuilt, is(Collections.<TreePath> emptyList()));
  }

  @Test
  public abstract void shouldRetainIdenticalPaths();

  @Test
  public void shouldReturnAnEmptyCollectionIfInputIsNotRecognized() {
    assertThat(builder.build(this), is(Collections.<TreePath> emptyList()));
  }

  @Test
  public void shouldReturnAnEmptyCollectionIsInputIsNull() {
    assertThat(builder.build(null), is(Collections.<TreePath> emptyList()));
  }

  /**
   * Creates a builder to be tested.
   * @param p the {@link ICategoryProvider} for the constructor.
   * @return a builder.
   */
  protected abstract ITreePathBuilder create(ICategoryProvider p);

  /**
   * Creates a provider as an input to the builder.
   * @param inputData the data to be returned by the provider.
   */
  protected abstract IProvider<T> input(@Nullable Collection<T> inputData);

  protected TreePath newPath(Object... segments) {
    return new TreePath(segments);
  }

  /**
   * @return an error message to use with assert methods.
   */
  protected String toString(List<TreePath> actual, List<TreePath> expected) {
    List<String> actualString = newArrayList();
    for (TreePath p : actual) {
      actualString.add(toString(p));
    }
    List<String> expectedString = newArrayList();
    for (TreePath p : expected) {
      expectedString.add(toString(p));
    }
    return "\n[" + Joiner.on(", ").join(expectedString) + "] is expected"
        + "\n[" + Joiner.on(", ").join(actualString) + "] is actual";
  }

  /**
   * @return a string representation of the given path.
   */
  protected String toString(TreePath path) {
    List<String> segments = newArrayListWithCapacity(path.getSegmentCount());
    for (int i = 0; i < path.getSegmentCount(); ++i) {
      segments.add(path.getSegment(i).toString());
    }
    return path.getClass().getSimpleName() + "["
        + Joiner.on(", ").join(segments) + "]";
  }
}
