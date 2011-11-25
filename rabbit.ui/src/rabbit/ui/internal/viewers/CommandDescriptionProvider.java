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
package rabbit.ui.internal.viewers;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;

/**
 * Providers command descriptions.
 */
public class CommandDescriptionProvider extends ColumnLabelProvider {
  
  private final Color gray;

  public CommandDescriptionProvider() {
    gray = PlatformUI.getWorkbench().getDisplay().getSystemColor(
        SWT.COLOR_DARK_GRAY);
  }

  @Override
  public String getText(Object element) {
    if (element instanceof Command) {
      try {
        return ((Command) element).getDescription();
      } catch (NotDefinedException ignored) {
      }
    }
    return null;
  }
  
  @Override
  public Color getForeground(Object element) {
    if (element instanceof Command) {
      if (!((Command) element).isDefined()) {
        return gray;
      }
    }
    return super.getForeground(element);
  }
}
