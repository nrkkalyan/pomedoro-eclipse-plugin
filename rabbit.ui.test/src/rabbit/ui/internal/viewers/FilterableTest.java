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

import rabbit.ui.internal.viewers.IFilterable;

import com.google.common.base.Predicates;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link IFilterable}.
 */
public abstract class FilterableTest {

  private IFilterable filterable;

  @Before
  public void before() {
    filterable = create();
  }

  @Test
  public void removingAFilterShouldTakeEffectImmediately() {
    filterable.addFilter(Predicates.instanceOf(Integer.class));
    filterable.addFilter(Predicates.instanceOf(Character.class));

    Object[] original = {"string", 'c', Integer.valueOf(0)};
    filterable.filter(original);

    filterable.removeFilter(Predicates.instanceOf(Character.class));
    Object[] expected = {"string", 'c'};
    Object[] returned = filterable.filter(original);
    assertThat(returned, equalTo(expected));
  }

  @Test
  public void shouldBeAbleToHandleMultipleFilters() {
    filterable.addFilter(Predicates.instanceOf(Integer.class));
    filterable.addFilter(Predicates.instanceOf(Character.class));
    Object[] original = {"string", 'c', Integer.valueOf(0)};
    Object[] expected = {"string"};
    Object[] returned = filterable.filter(original);
    assertThat(returned, equalTo(expected));
  }

  @Test
  public void shouldNotModifyTheOriginalArrayWhenFiltering() {
    filterable.addFilter(Predicates.instanceOf(Float.class));
    Object[] original = {Integer.valueOf(0), Float.valueOf(0f)};
    Object[] originalBackup = original.clone();
    filterable.filter(original);
    assertThat(original, equalTo(originalBackup));
  }

  @Test
  public void shouldNotModifyTheOriginalArrayWhenNoFiltering() {
    Object[] original = {"1", Float.valueOf(0f), 'a', Integer.valueOf(0)};
    Object[] originalBackup = original.clone();
    filterable.filter(original);
    assertThat(original, equalTo(originalBackup));
  }

  @Test
  public void shouldRespectTheOrderOfTheElementsWhenFiltering() {
    filterable.addFilter(Predicates.instanceOf(Float.class));
    Object[] original = {"string", Float.valueOf(0f), Integer.valueOf(0)};
    Object[] expected = {"string", Integer.valueOf(0)}; // Order is maintained
    Object[] returned = filterable.filter(original);
    assertThat(returned, equalTo(expected));
  }

  @Test
  public void shouldRespectTheOrderOfTheElementsWhenNoFiltering() {
    Object[] original = {"string", Integer.valueOf(0)};
    Object[] expected = {"string", Integer.valueOf(0)};
    Object[] returned = filterable.filter(original);
    assertThat(returned, equalTo(expected));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void shouldReturnANewArrayWhenFiltering() {
    filterable.addFilter(Predicates.instanceOf(Object.class));
    Object[] original = {Integer.valueOf(0)};
    Object[] returned = filterable.filter(original);
    assertThat(returned, allOf(not(nullValue()), not(sameInstance(original))));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void shouldReturnANewArrayWhenNoFiltering() {
    Object[] original = {};
    Object[] returned = filterable.filter(original);
    assertThat(returned, allOf(not(nullValue()), not(sameInstance(original))));
  }

  @Test
  public void shouldReturnTheCorrectElementsWhenFiltering() {
    filterable.addFilter(Predicates.instanceOf(Integer.class));
    Object[] original = {"string", Integer.valueOf(0)};
    Object[] expected = {"string"};
    Object[] returned = filterable.filter(original);
    assertThat(returned, equalTo(expected));
  }

  @Test
  public void shouldReturnTheCorrectElementsWhenNoFiltering() {
    Object[] original = {"string"};
    Object[] returned = filterable.filter(original);
    assertThat(returned, equalTo(original));
  }

  /**
   * @return an instance to be tested.
   */
  protected abstract IFilterable create();
}
