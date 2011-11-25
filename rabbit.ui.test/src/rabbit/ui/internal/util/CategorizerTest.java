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

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

/**
 * Tests for {@link Categorizer}.
 */
public class CategorizerTest {

  @Test
  public void getCategoryShouldAcceptNull() {
    ICategory category = mock(ICategory.class);
    Categorizer categorizer = new Categorizer(ImmutableMap.of(Predicates.isNull(), category));
    assertThat(categorizer.getCategory(null), is(category));
  }

  @Test
  public void getCategoryShouldReturnNullIfTheCategoryOfTheGivenObjectIsNotDefinedByTheMap() {
    Categorizer categorizer = new Categorizer(
        ImmutableMap.of(Predicates.isNull(), mock(ICategory.class)));
    assertThat(categorizer.getCategory(new Object()), is(nullValue()));
  }

  @Test
  public void getCategoryShouldReturnTheCategoryOfTheGivenObjectDefinedByTheMap() {
    Object object = new Object();
    ICategory category = mock(ICategory.class);
    Categorizer categorizer = new Categorizer(ImmutableMap.of(Predicates.equalTo(object), category));
    assertThat(categorizer.getCategory(object), is(category));
  }

  @Test
  public void hasCategoryShouldReturnFalseIfTheMapSuppliedDoesNotContainThatCategory() {
    Categorizer categorizer = new Categorizer(
        ImmutableMap.of(Predicates.alwaysFalse(), mock(ICategory.class)));
    assertThat(categorizer.hasCategory(mock(ICategory.class)), is(false));
  }

  @Test
  public void hasCategoryShouldReturnTrueIfTheMapSuppliedContainsThatCategory() {
    ICategory category = mock(ICategory.class);
    Categorizer categorizer = new Categorizer(ImmutableMap.of(Predicates.alwaysFalse(), category));
    assertThat(categorizer.hasCategory(category), is(true));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowAnExceptionIfConstructedWithoutAMapOfCategories() {
    new Categorizer(null);
  }
}
