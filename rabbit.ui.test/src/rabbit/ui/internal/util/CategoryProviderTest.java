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

import static com.google.common.collect.Lists.newArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static java.util.Arrays.asList;

import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

/**
 * Tests for a {@link CategoryProvider}.
 */
public class CategoryProviderTest {

  /**
   * Helper class to test observerables.
   */
  private static class ObserverHelper implements Observer {

    /**
     * Default to 0, increments by 1 each time
     * {@link #update(Observable, Object)} is called.
     */
    int updateCount = 0;

    @Override
    public void update(Observable arg0, Object arg1) {
      ++updateCount;
    }
  }

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test(expected = IllegalArgumentException.class)
  public void constructorShouldThrowAnExceptionIfAllSupportedDoesNoContainAllDefaultSelectedCategories() {
    create(new ICategory[0], mock(ICategory.class));
  }

  @Test
  public void shouldCorrectlySetTheSelectedCategories() {
    ICategory[] allSupported = {mock(ICategory.class), mock(ICategory.class)};
    CategoryProvider p = create(allSupported);

    ICategory[] selected = {allSupported[0]};
    p.setSelected(selected);
    assertThat(p.getSelected(), equalTo(asList(selected)));
  }

  @Test
  public void shouldFilterOutNotSupportedCategories() {
    ICategory[] allSupported = {mock(ICategory.class)};
    CategoryProvider p = create(allSupported);
    p.setSelected(mock(ICategory.class), mock(ICategory.class));
    assertThat(p.getSelected(), equalTo(Collections.<ICategory> emptyList()));
  }

  @Test
  public void shouldNotifyObserversWhenSelectedCategoriesAreSetToDifferentCategories() {
    ICategory[] all = {mock(ICategory.class), mock(ICategory.class)};
    ICategory[] selected = {all[1], all[0]};
    ObserverHelper observer = new ObserverHelper();
    CategoryProvider p = create(all, selected);
    p.addObserver(observer);
    p.setSelected(all);
    assertThat(observer.updateCount, is(1));
  }

  @Test
  public void shouldNotNotifyObserversIfSelectedCategoriesAreSetToTheSameCategories() {
    ICategory[] all = {mock(ICategory.class), mock(ICategory.class)};
    ICategory[] selected = {all[0], all[1]};
    ObserverHelper observer = new ObserverHelper();
    CategoryProvider p = create(all, selected);
    p.addObserver(observer);
    p.setSelected(selected);
    assertThat(observer.updateCount, is(0));
  }

  @Test
  public void shouldReturnAllSupportedCategoriesAsAnImmutableCollection() {
    ICategory[] all = {mock(ICategory.class), mock(ICategory.class)};
    CategoryProvider p = create(all);

    thrown.expect(UnsupportedOperationException.class);
    p.getAllSupported().add(mock(ICategory.class));
  }

  @Test
  public void shouldReturnAllSupportedCategoriesDefinedInConstructor() {
    ICategory[] all = {mock(ICategory.class), mock(ICategory.class)};
    ICategory[] selected = {all[0]};

    CategoryProvider p = create(all, selected);
    assertThat(p.getSelected(), equalTo(asList(selected)));
  }

  @Test
  public void shouldReturnTheCorrectUnselectedCategoriesWhenFirstConstructed() {
    ICategory[] all = {mock(ICategory.class), mock(ICategory.class)};
    ICategory[] selected = {all[0]};
    ICategory[] unselected = {all[1]};

    CategoryProvider p = create(all, selected);
    assertThat(
        newArrayList(p.getUnselected()),
        equalTo(newArrayList(unselected)));
  }

  @Test
  public void shouldReturnTheCorrectUnselectedCategoriesWhenNewCategoriesAreSet() {
    ICategory[] all = {mock(ICategory.class), mock(ICategory.class)};
    ICategory[] selected = {all[0]};
    ICategory[] unselected = {all[1]};

    CategoryProvider p = create(all);
    p.setSelected(selected);
    assertThat(
        newArrayList(p.getUnselected()),
        equalTo(newArrayList(unselected)));
  }

  @Test
  public void shouldReturnTheSelectedCategoriesAsAnImmutableCollection() {
    ICategory[] all = {mock(ICategory.class), mock(ICategory.class)};
    ICategory[] selected = {all[0]};
    CategoryProvider p = create(all, selected);

    thrown.expect(UnsupportedOperationException.class);
    p.getSelected().add(mock(ICategory.class));
  }

  @Test
  public void shouldReturnTheSelectedCategoriesBeenSet() {
    ICategory[] all = {mock(ICategory.class), mock(ICategory.class)};
    CategoryProvider p = create(all);

    ICategory[] selected = {all[0]};
    p.setSelected(selected);
    assertThat(p.getSelected(), equalTo(asList(selected)));
  }

  @Test
  public void shouldReturnTheSelectedCategoriesDefinedInConstructor() {
    ICategory[] all = {mock(ICategory.class), mock(ICategory.class)};
    ICategory[] selected = {all[0]};
    CategoryProvider p = create(all, selected);
    assertThat(p.getSelected(), equalTo(asList(selected)));
  }

  @Test
  public void shouldReturnTheUnselectedCategoriesAsAnImmutableCollection() {
    ICategory[] all = {mock(ICategory.class), mock(ICategory.class)};
    CategoryProvider p = create(all);

    thrown.expect(UnsupportedOperationException.class);
    p.getUnselected().add(mock(ICategory.class));
  }

  protected CategoryProvider create(ICategory[] all, ICategory... selected) {
    return new CategoryProvider(all, selected);
  }
}
