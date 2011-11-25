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

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * An action that has a drop down menu.
 */
public class DropDownAction extends Action implements IMenuCreator {

  protected Menu menu;
  private final IAction defaultAction;
  private final IAction[] menuItems;

  /**
   * Constructor.
   * 
   * @param text The text of this action.
   * @param image The image of this action.
   * @param defaultAction The default action to run.
   * @param menuItems The list of actions to be displayed on the menu.
   * @throws NullPointerException If default action is null.
   */
  public DropDownAction(String text, ImageDescriptor image,
      IAction defaultAction, IAction... menuItems) {
    super(text, IAction.AS_DROP_DOWN_MENU);
    checkNotNull(defaultAction);

    setMenuCreator(this);
    setImageDescriptor(image);
    this.defaultAction = defaultAction;
    this.menuItems = menuItems;
  }

  /**
   * Constructor. This constructor uses the first action in the arguments as the default, that is,
   * the default text/image/action will be taken from the first action.
   * @param menuItems the list of actions, length must be greater than 1.
   */
  public DropDownAction(IAction... menuItems) {
    this(menuItems[0].getText(), menuItems[0].getImageDescriptor(), menuItems[0], menuItems);
  }

  @Override
  public void dispose() {
    if (menu != null && !menu.isDisposed()) {
      menu.dispose();
      menu = null;
    }
  }

  @Override
  public Menu getMenu(Control parent) {
    if (menu == null || menu.isDisposed()) {
      menu = new Menu(parent);

      for (IAction action : menuItems) {
        new ActionContributionItem(action).fill(menu, -1);
      }
    }
    return menu;
  }

  @Override
  public Menu getMenu(Menu parent) {
    return null;
  }

  @Override
  public void run() {
    defaultAction.run();
  }

  /**
   * Gets the default action.
   * 
   * @return The default action.
   */
  public IAction getDefaultAction() {
    return defaultAction;
  }

  /**
   * Gets the actions to be displayed on the menu.
   * 
   * @return The actions to be displayed on the menu.
   */
  public IAction[] getMenuItemActions() {
    return menuItems;
  }
}
