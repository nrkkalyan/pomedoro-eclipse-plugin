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
package rabbit.ui.internal.actions;

import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @see CategoryAction
 */
public class CategoryActionTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void runShouldSetTheCategories() {
    @SuppressWarnings("serial")
    class MyException extends RuntimeException {}

    ICategory[] categories = {mock(ICategory.class), mock(ICategory.class)};
    ICategoryProvider provider = mock(ICategoryProvider.class);
    doThrow(new MyException()).when(provider).setSelected(categories);

    IAction action = create(provider, categories);
    thrown.expect(MyException.class);
    action.run();
  }

  @Test
  public void shouldSetTheImageOfTheActionToBeTheSameAsTheImageOfTheFirstCategory() {
    ICategory first = mock(ICategory.class);
    ImageDescriptor firstImage = mock(ImageDescriptor.class);
    given(first.getImageDescriptor()).willReturn(firstImage);

    ICategory second = mock(ICategory.class);
    ImageDescriptor secondImage = mock(ImageDescriptor.class);
    given(second.getImageDescriptor()).willReturn(secondImage);

    ICategory[] categories = {first, second};
    IAction action = create(mock(ICategoryProvider.class), categories);
    assertThat(action.getImageDescriptor(), sameInstance(first.getImageDescriptor()));
  }

  @Test
  public void shouldSetTheTextOfTheActionToBeTheSameAsTheTextOfTheFirstCategory() {
    ICategory first = mock(ICategory.class);
    given(first.getText()).willReturn("First");

    ICategory second = mock(ICategory.class);
    given(second.getText()).willReturn("Second");

    ICategory[] categories = {first, second};
    IAction action = create(mock(ICategoryProvider.class), categories);
    assertThat(action.getText(), equalTo(first.getText()));
  }

  @Test
  public void shouldThrowAnExceptionIfTryToConstructWithNullCategoriesInArray() {
    ICategory[] categories = {mock(ICategory.class), null};
    thrown.expect(NullPointerException.class);
    create(mock(ICategoryProvider.class), categories);
  }

  @Test
  public void shouldThrowAnExceptionIfTryToConstructWithoutAnyCategories() {
    thrown.expect(IllegalArgumentException.class);
    create(mock(ICategoryProvider.class));
  }

  @Test
  public void shouldThrowAnExceptionIfTryToConstructWithoutAProvider() {
    thrown.expect(NullPointerException.class);
    new CategoryAction(null, mock(ICategory.class));
  }

  /**
   * @see CategoryAction#CategoryAction(ICategoryProvider, ICategory...)
   */
  protected CategoryAction create(ICategoryProvider provider, ICategory... categories) {
    return new CategoryAction(provider, categories);
  }
}
