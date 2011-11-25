/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.ui.internal.actions;

import rabbit.ui.internal.util.ICategoryProvider;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.action.IAction;
import org.junit.Test;

/**
 * @see GroupByAction
 */
public class GroupByActionTest {

  @Test(expected = NullPointerException.class)
  public void shouldThrowAnExceptionIfTryToConstructWithANullCategoryProvider() {
    new GroupByAction(null, mock(IAction.class));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowAnExceptionIfTryToConstructWithNullActions() {
    new GroupByAction(mock(ICategoryProvider.class), new IAction[]{null});
  }

  @Test
  public void getTextShouldReturnTheCorrectText() {
    IAction action = mock(IAction.class);
    given(action.getText()).willReturn("Hello");

    assertThat(
        new GroupByAction(mock(ICategoryProvider.class), action).getText(),
        equalTo("Group by " + action.getText()));
  }
}
