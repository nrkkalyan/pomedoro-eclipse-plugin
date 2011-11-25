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

import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.actions.DropDownAction;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.junit.Test;

/**
 * @see DropDownAction
 */
public class DropDownActionTest {

  @Test
  public void testConstructor_defaultAction() {
    IAction action = new Action() {
    };
    assertSame(action, new DropDownAction("text", SharedImages.BRUSH, action)
        .getDefaultAction());
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_defaultAction_null() {
    new DropDownAction("text", SharedImages.BRUSH, null);
  }

  @Test
  public void testConstructor_image() {
    ImageDescriptor image = SharedImages.BRUSH;
    assertEquals(image, new DropDownAction(null, image, new Action() {
    }).getImageDescriptor());
  }

  @Test
  public void testConstructor_menuItems() {
    IAction[] actions = new IAction[] { new Action() {
    }, new Action() {
    } };
    assertArrayEquals(actions, new DropDownAction(null, null, new Action() {
    }, actions).getMenuItemActions());
  }

  @Test
  public void testConstructor_text() {
    String text = "adfolij";
    assertEquals(text, new DropDownAction(text, null, new Action() {
    }).getText());
  }
}
