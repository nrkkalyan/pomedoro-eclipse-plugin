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

import rabbit.ui.internal.SharedImages;

import org.eclipse.jface.action.IAction;

public class ColorByAction extends DropDownAction {

  /**
   * Constructor. This constructor uses the first action in the arguments as the default, that is,
   * the default text/image will be taken from the first action.
   * @param actions the list of actions, length must be greater than 1.
   */
  public ColorByAction(IAction... actions) {
    super(actions);
    setText("Highlight " + getText());
    setImageDescriptor(SharedImages.BRUSH);
  }
}
