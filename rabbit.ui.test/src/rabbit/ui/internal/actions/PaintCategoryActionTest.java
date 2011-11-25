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
import rabbit.ui.internal.util.IVisualProvider;

import static org.hamcrest.CoreMatchers.equalTo;
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
 * @see PaintCategoryAction
 */
public class PaintCategoryActionTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void runShouldSetTheProvidersPaintCategory() {
    @SuppressWarnings("serial")
    class MyException extends RuntimeException {}
    ICategory category = mock(ICategory.class);
    IVisualProvider provider = mock(IVisualProvider.class);
    doThrow(new MyException()).when(provider).setVisualCategory(category);

    IAction action = create(category, provider);
    thrown.expect(MyException.class);
    action.run();
  }

  @Test
  public void shouldSetTheImageOfTheActionToBeTheSameAsTheImageOfTheCategory() {
    ICategory category = mock(ICategory.class);
    ImageDescriptor image = mock(ImageDescriptor.class);
    given(category.getImageDescriptor()).willReturn(image);

    IAction action = create(category);
    assertThat(action.getImageDescriptor(),
        equalTo(category.getImageDescriptor()));
  }

  @Test
  public void shouldSetTheTextOfTheActionToBeTheSameAsTheTextOfTheCategory() {
    ICategory category = mock(ICategory.class);
    given(category.getText()).willReturn("Hello");

    IAction action = create(category);
    assertThat(action.getText(), equalTo(category.getText()));
  }

  @Test
  public void shouldThrowAnExceptionIfTryToConstructWithoutACategory() {
    thrown.expect(NullPointerException.class);
    create(null, mock(IVisualProvider.class));
  }

  @Test
  public void shouldThrowAnExceptionIfTryToConstructWithANullProvider() {
    thrown.expect(NullPointerException.class);
    create(mock(ICategory.class), new IVisualProvider[]{null});
  }

  /**
   * @see PaintCategoryAction#PaintCategoryAction(ICategory, IVisualProvider...)
   */
  protected PaintCategoryAction create(ICategory paintCategory,
      IVisualProvider... providers) {
    return new PaintCategoryAction(paintCategory, providers);
  }
}
