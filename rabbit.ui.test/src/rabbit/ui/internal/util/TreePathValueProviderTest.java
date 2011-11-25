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

import com.google.common.base.Predicates;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jface.viewers.TreePath;
import org.junit.Test;
import org.mockito.Mockito;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.Arrays;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

/**
 * Tests for a {@link TreePathValueProvider}.
 */
public class TreePathValueProviderTest {

  @Test(expected = NullPointerException.class)
  public void constructorShouldThrowAnExceptionIfCategorizerIsNull() {
    create(null, newProvider(), newConverter());
  }

  @Test(expected = NullPointerException.class)
  public void constructorShouldThrowAnExceptionIfConverterisNull() {
    create(mock(ICategorizer.class), null, newConverter());
  }

  @Test(expected = NullPointerException.class)
  public void constructorShouldThrowAnExceptionIfProviderIsNull() {
    create(mock(ICategorizer.class), newProvider(), null);
  }

  @Test
  public void getCategorizerShouldReturnTheCategorizer() {
    ICategorizer categorizer = mock(ICategorizer.class);
    TreePathValueProvider v = create(categorizer, newProvider(), newConverter());
    assertThat(v.getCategorizer(), is(categorizer));
  }

  @Test
  public void getConverterShouldReturnTheConverter() {
    IConverter<TreePath> converter = newConverter();
    TreePathValueProvider v = create(
        mock(ICategorizer.class),
        newProvider(),
        converter);
    assertThat(v.getConverter(), is(converter));
  }

  @Test
  public void getMaxValueShouldReturnTheValueThatHasBeenSet() {
    long max = 123;
    TreePathValueProvider v = create();
    v.setMaxValue(max);
    assertThat(v.getMaxValue(), is(max));
  }

  @Test
  public void getProviderShouldReturnTheProvider() {
    IProvider<TreePath> provider = newProvider();
    TreePathValueProvider v = create(
        mock(ICategorizer.class),
        provider,
        newConverter());
    assertThat(v.getProvider(), is(provider));
  }

  @Test
  public void getValueShouldReturn0ForNullArguments() {
    assertThat(create().getValue(null), is(Long.valueOf(0)));
  }

  @Test
  public void getValueShouldReturnTheSumOfTheChildrenValueForAParentPath() {
    TreePath leaf1 = new TreePath(new Object[]{"1", "2", "3"});
    TreePath leaf2 = new TreePath(new Object[]{"1", "2", "4"});
    TreePath leaf3 = new TreePath(new Object[]{"1", "5", "6"});
    TreePath parentOf1n2 = new TreePath(new Object[]{"1", "2"});
    TreePath parentOfAll = new TreePath(new Object[]{"1"});

    @SuppressWarnings("unchecked")
    IConverter<TreePath> converter = mock(IConverter.class);
    given(converter.convert(leaf1)).willReturn(Long.valueOf(1));
    given(converter.convert(leaf2)).willReturn(Long.valueOf(2));
    given(converter.convert(leaf3)).willReturn(Long.valueOf(3));

    @SuppressWarnings("unchecked")
    IProvider<TreePath> provider = mock(IProvider.class);
    given(provider.get()).willReturn(Arrays.asList(leaf1, leaf2, leaf3));

    TreePathValueProvider values = create(
        mock(ICategorizer.class),
        provider,
        converter);

    long value = converter.convert(leaf1) + converter.convert(leaf2);
    assertThat(values.getValue(parentOf1n2), equalTo(value));

    value += converter.convert(leaf3);
    assertThat(values.getValue(parentOfAll), equalTo(value));
  }

  @Test
  public void getValueShouldReturnTheValueOfALeafPathSuppliedByTheConverter() {
    TreePath leaf = new TreePath(new Object[]{""});
    long value = 123;

    @SuppressWarnings("unchecked")
    IConverter<TreePath> c = mock(IConverter.class);
    given(c.convert(leaf)).willReturn(Long.valueOf(value));

    @SuppressWarnings("unchecked")
    IProvider<TreePath> provider = mock(IProvider.class);
    given(provider.get()).willReturn(Arrays.asList(leaf));

    TreePathValueProvider p = create(mock(ICategorizer.class), provider, c);
    assertThat(p.getValue(leaf), is(value));
  }

  @Test
  public void getVirsualCategoryShouldReturnTheCategoryThatHasBeenSet() {
    ICategory category = mock(ICategory.class);
    ICategorizer categorizer = mock(ICategorizer.class);
    given(categorizer.hasCategory(category)).willReturn(TRUE);

    TreePathValueProvider v = create(categorizer, newProvider(), newConverter());
    v.setVisualCategory(category);

    assertThat(v.getVisualCategory(), is(category));
  }

  @Test
  public void setVisualCategoryShouldNotifyObserversWhenTheCategoryIsChanged() {
    // Given we have an observer observing on the provider:
    final int[] updateCount = {0};
    Observer observer = new Observer() {
      @Override
      public void update(Observable ob, Object arg) {
        // Updates the counter when notified:
        updateCount[0]++;
      }
    };
    ICategory category = mock(ICategory.class);
    ICategorizer categorizer = mock(ICategorizer.class);
    given(categorizer.hasCategory(category)).willReturn(TRUE);
    TreePathValueProvider provider = create(categorizer, newProvider(),
        newConverter());
    provider.addObserver(observer);

    // When the provider's category is changed to a different category:
    assertThat(provider.setVisualCategory(category), is(TRUE));

    // Then the observer should have been notified:
    assertThat(updateCount[0], equalTo(1));
  }

  @Test
  public void setVisualCategoryShouldNotNotifyObserversWhenTheCategoryIsUnchanged() {
    // Given we have a provider who's category has been set:
    ICategory category = mock(ICategory.class);
    ICategorizer categorizer = mock(ICategorizer.class);
    given(categorizer.hasCategory(category)).willReturn(TRUE);
    TreePathValueProvider provider = create(categorizer, newProvider(),
        newConverter());
    provider.setVisualCategory(category);

    // And we have an observer observing on the provider:
    final int[] updateCount = {0};
    Observer observer = new Observer() {
      @Override
      public void update(Observable ob, Object arg) {
        // Updates the counter when notified:
        updateCount[0]++;
      }
    };
    provider.addObserver(observer);

    // When we call the change category method with the same category:
    assertThat(provider.setVisualCategory(provider.getVisualCategory()),
        is(TRUE));

    // Then the observer should not be notified because the actual category is
    // not changed:
    assertThat(updateCount[0], equalTo(0));
  }

  @Test
  public void setVisualCategoryShouldAcceptKnownCategories() {
    // Given that a categorizer has a given category:
    ICategory category = mock(ICategory.class);
    ICategorizer categorizer = mock(ICategorizer.class);
    given(categorizer.hasCategory(category)).willReturn(true);

    // When changing the provider's category to this category, should be OK:
    TreePathValueProvider p = create(categorizer, newProvider(), newConverter());
    assertThat(p.setVisualCategory(category), is(true));
    assertThat(p.getVisualCategory(), is(category));
  }

  @Test
  public void setVisualCategoryShouldRejectUnknownCategories() {
    ICategorizer c = mock(ICategorizer.class);
    given(c.hasCategory(Mockito.<ICategory> any())).willReturn(FALSE);
    TreePathValueProvider provider = create(c, newProvider(), newConverter());

    ICategory category = mock(ICategory.class);
    assertThat(provider.setVisualCategory(category), is(FALSE));
    assertThat(provider.getVisualCategory(), is(not(category)));
  }

  @Test
  public void setMaxValueWithCategoryAndPredicateShouldCalculateTheCorrectValue() {
    // @formatter:off
    // Given we have a tree that looks like the following:
    // + - 1 - 2 - 3  // Value of this path = 1
    //  \        \
    //   \         4  // Value of this path = 10
    //    \
    //     2 - 2 - 2  // Value of this path = 2
    // @formatter:on
    TreePath leaf1 = new TreePath(new Object[]{"1", "2", "3"});
    TreePath leaf2 = new TreePath(new Object[]{"1", "2", "4"});
    TreePath leaf3 = new TreePath(new Object[]{"2", "2", "2"});

    // And elements belong to the same category:
    ICategory category = mock(ICategory.class);
    ICategorizer categorizer = mock(ICategorizer.class);
    given(categorizer.getCategory("2")).willReturn(category);
    given(categorizer.getCategory("1")).willReturn(category);
    given(categorizer.getCategory("3")).willReturn(category);
    given(categorizer.getCategory("4")).willReturn(category);

    // And each path has a different value:
    @SuppressWarnings("unchecked")
    IConverter<TreePath> converter = mock(IConverter.class);
    given(converter.convert(leaf1)).willReturn(Long.valueOf(1));
    given(converter.convert(leaf2)).willReturn(Long.valueOf(10));
    given(converter.convert(leaf3)).willReturn(Long.valueOf(2));

    @SuppressWarnings("unchecked")
    IProvider<TreePath> provider = mock(IProvider.class);
    given(provider.get()).willReturn(Arrays.asList(leaf1, leaf2, leaf3));

    // When we call setMaxValue with predicate to say we only want element "3":
    TreePathValueProvider v = create(categorizer, provider, converter);
    v.setMaxValue(category, Predicates.<Object> equalTo("3"));

    // @formatter:off
    // Then the maximum value should have been set to the value of the element "3" that has the
    // highest value, which is the one in bracket, with value of 1:
    // + - 1 - 2 -(3)  // Value of this path = 1
    //  \        \
    //   \         4  // Value of this path = 10
    //    \
    //     2 - 2 - 2  // Value of this path = 2
    // @formatter:on
    assertThat(v.getMaxValue(), equalTo(1L));
  }

  @Test
  public void setMaxValueWithCategoryShouldCalculateTheCorrectValue_1() {
    // @formatter:off
    // Given we have a tree that looks like the following:
    // + - 1 - 2 - 3  // Value of this path = 1
    //  \        \
    //   \         4  // Value of this path = 10
    //    \
    //     2 - 2 - 2  // Value of this path = 2
    // @formatter:on
    TreePath leaf1 = new TreePath(new Object[]{"1", "2", "3"});
    TreePath leaf2 = new TreePath(new Object[]{"1", "2", "4"});
    TreePath leaf3 = new TreePath(new Object[]{"2", "2", "2"});

    // And element "2" belongs to a different category:
    ICategory theCategory = mock(ICategory.class);
    ICategory tmpCategory = mock(ICategory.class);
    ICategorizer categorizer = mock(ICategorizer.class);
    given(categorizer.getCategory("2")).willReturn(theCategory);
    given(categorizer.getCategory("1")).willReturn(tmpCategory);
    given(categorizer.getCategory("3")).willReturn(tmpCategory);
    given(categorizer.getCategory("4")).willReturn(tmpCategory);

    // And each path has a different value:
    @SuppressWarnings("unchecked")
    IConverter<TreePath> converter = mock(IConverter.class);
    given(converter.convert(leaf1)).willReturn(Long.valueOf(1));
    given(converter.convert(leaf2)).willReturn(Long.valueOf(10));
    given(converter.convert(leaf3)).willReturn(Long.valueOf(2));

    @SuppressWarnings("unchecked")
    IProvider<TreePath> provider = mock(IProvider.class);
    given(provider.get()).willReturn(Arrays.asList(leaf1, leaf2, leaf3));

    // When we call setMaxValue with element "2"'s category:
    TreePathValueProvider v = create(categorizer, provider, converter);
    v.setMaxValue(theCategory);

    // @formatter:off
    // Then the maximum value should have been set to the value of the element "2" that has the
    // highest value, which is the one in bracket, with value of 11 (1 + 10):
    // + - 1 -(2)- 3  // Value of this path = 1
    //  \        \
    //   \         4  // Value of this path = 10
    //    \
    //     2 - 2 - 2  // Value of this path = 2
    // @formatter:on
    assertThat(v.getMaxValue(),
        equalTo(converter.convert(leaf1) + converter.convert(leaf2)));
  }

  @Test
  public void setMaxValueWithCategoryShouldCalculateTheCorrectValue_2() {
    // Given our tree has only one path:
    TreePath leaf1 = new TreePath(new Object[]{"1", "2", "3"});

    // And the value of this path is 100:
    @SuppressWarnings("unchecked")
    IConverter<TreePath> converter = mock(IConverter.class);
    given(converter.convert(leaf1)).willReturn(Long.valueOf(100));

    // And each element in the path corresponds to a category:
    ICategory theCategory = mock(ICategory.class);
    ICategory tmpCategory = mock(ICategory.class);
    ICategorizer categorizer = mock(ICategorizer.class);
    given(categorizer.getCategory("2")).willReturn(theCategory);
    given(categorizer.getCategory("1")).willReturn(tmpCategory);
    given(categorizer.getCategory("3")).willReturn(tmpCategory);

    @SuppressWarnings("unchecked")
    IProvider<TreePath> provider = mock(IProvider.class);
    given(provider.get()).willReturn(Arrays.asList(leaf1));

    // When we call setMaxValue with the category belonging to one of the
    // elements of the path:
    TreePathValueProvider v = create(categorizer, provider, converter);
    v.setMaxValue(theCategory);

    // Then the maximum value is set to the value of the tree path:
    assertThat(v.getMaxValue(), equalTo(converter.convert(leaf1)));
  }

  @Test
  public void setMaxValueWithCategoryShouldCalculateTheCorrectValue_3() {
    // @formatter:off
    // If we have a tree like the following, where the element "2" appears at multiple places at
    // different levels:
    // 
    // + - 1 - 2 - 3  // Value of this path is 1
    //   \
    //     2 - 2 - 4  // Value of this path is 10
    //       \
    //         3 - 2  // Value of this path is 2
    // 
    // @formatter:on
    TreePath leaf1 = new TreePath(new Object[]{"1", "2", "3"});
    TreePath leaf2 = new TreePath(new Object[]{"2", "2", "4"});
    TreePath leaf3 = new TreePath(new Object[]{"2", "3", "2"});

    // And element "2" belongs to a different category:
    ICategory theCategory = mock(ICategory.class);
    ICategory tmpCategory = mock(ICategory.class);
    ICategorizer categorizer = mock(ICategorizer.class);
    given(categorizer.getCategory("2")).willReturn(theCategory);
    given(categorizer.getCategory("1")).willReturn(tmpCategory);
    given(categorizer.getCategory("3")).willReturn(tmpCategory);
    given(categorizer.getCategory("4")).willReturn(tmpCategory);

    // And each path has a different:
    @SuppressWarnings("unchecked")
    IConverter<TreePath> converter = mock(IConverter.class);
    given(converter.convert(leaf1)).willReturn(Long.valueOf(1));
    given(converter.convert(leaf2)).willReturn(Long.valueOf(10));
    given(converter.convert(leaf3)).willReturn(Long.valueOf(2));

    @SuppressWarnings("unchecked")
    IProvider<TreePath> provider = mock(IProvider.class);
    given(provider.get()).willReturn(Arrays.asList(leaf1, leaf2, leaf3));

    // When we call setMaxValue(ICategory) using the category belonging to the
    // element "2"
    TreePathValueProvider v = create(categorizer, provider, converter);
    v.setMaxValue(theCategory);

    // @formatter:off
    // Then then the maximum value should be set to 12 in this example, which is the value of the
    // highest element "2" in the tree (the one in bracket below):
    // 
    // + - 1 - 2 - 3  // Value of this path is 1
    //   \
    //    (2)- 2 - 4  // Value of this path is 10
    //       \
    //         3 - 2  // Value of this path is 2
    //
    // @formatter:on
    assertThat(v.getMaxValue(),
        equalTo(converter.convert(leaf2) + converter.convert(leaf3)));
  }

  @Test
  public void shouldNotPaintIfTheCategoryOfTheObjectIsNotTheVisualCategory() {
    // Given a category is set to a provider:
    ICategory category = mock(ICategory.class);
    ICategorizer categorizer = mock(ICategorizer.class);
    given(categorizer.hasCategory(category)).willReturn(TRUE);

    TreePathValueProvider v = create(categorizer, newProvider(), newConverter());
    v.setVisualCategory(category);

    // When an object belongs to a different category:
    Object element = Integer.valueOf(0);
    category = mock(ICategory.class);
    given(categorizer.getCategory(element)).willReturn(category);

    // Then it should not be painted:
    assertThat(v.shouldPaint(""), is(FALSE));
  }

  @Test
  public void shouldPaintIfTheCategoryOfTheObjectIsTheVisualCategory() {
    ICategory category = mock(ICategory.class);
    ICategorizer categorizer = mock(ICategorizer.class);
    given(categorizer.getCategory(any())).willReturn(category);
    given(categorizer.hasCategory(category)).willReturn(TRUE);

    TreePathValueProvider v = create(categorizer, newProvider(), newConverter());
    v.setVisualCategory(category);

    assertThat(v.shouldPaint(""), is(TRUE));
  }

  private TreePathValueProvider create() {
    return create(mock(ICategorizer.class), newProvider(), newConverter());
  }

  private TreePathValueProvider create(
      ICategorizer categorizer,
      IProvider<TreePath> treePathProvider,
      IConverter<TreePath> treePathValueProvider) {

    return new TreePathValueProvider(
        categorizer,
        treePathProvider,
        treePathValueProvider);
  }

  private IConverter<TreePath> newConverter() {
    @SuppressWarnings("unchecked")
    IConverter<TreePath> mock = mock(IConverter.class);
    when(mock.convert(Mockito.<TreePath> any())).thenReturn(Long.valueOf(0));
    return mock;
  }

  private IProvider<TreePath> newProvider() {
    @SuppressWarnings("unchecked")
    IProvider<TreePath> mock = mock(IProvider.class);
    when(mock.get()).thenReturn(Collections.<TreePath> emptyList());
    return mock;
  }
}
