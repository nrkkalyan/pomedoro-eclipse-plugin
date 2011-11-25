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
import rabbit.ui.internal.dialogs.GroupByDialog;
import rabbit.ui.internal.util.ICategoryProvider;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

/**
 * Action to open a {@link GroupByDialog}
 */
public class ShowGroupByDialogAction extends Action {

  private ICategoryProvider provider;

  /**
   * Constructs a new action using the default text and style.
   * 
   * @param provider The category provider.
   * @throws NullPointerException If the provider is null.
   */
  public ShowGroupByDialogAction(ICategoryProvider provider) {
    this(provider, "Advanced...", IAction.AS_PUSH_BUTTON);
  }

  /**
   * Constructor.
   * 
   * @param text The text of the action.
   * @param style The style of the action.
   * @param provider The category provider.
   * @throws NullPointerException If the provider is null.
   */
  public ShowGroupByDialogAction(ICategoryProvider provider, String text,
      int style) {
    super(text, style);
    checkNotNull(provider);

    this.provider = provider;
    setImageDescriptor(SharedImages.HIERARCHY);
  }

  @Override
  public void runWithEvent(Event event) {
    Shell parentShell = event.display.getActiveShell();
    GroupByDialog dialog = new GroupByDialog(parentShell);
    dialog.create();
    dialog.getShell().setSize(400, 400);

    dialog.setSelectedCategories(provider.getSelected());
    dialog.setUnSelectedCategories(provider.getUnselected());

    if (dialog.open() == GroupByDialog.OK) {
      provider.setSelected(dialog.getSelectedCategories());
    }
  }
}
