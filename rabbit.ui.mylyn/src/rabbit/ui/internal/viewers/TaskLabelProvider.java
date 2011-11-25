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

import rabbit.ui.internal.util.UnrecognizedTask;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * Label provider for tasks.
 */
public class TaskLabelProvider extends NullLabelProvider {

  private final TaskElementLabelProvider provider;
  private final Color gray;
  private final Image normalTaskImage;

  public TaskLabelProvider() {
    provider = new TaskElementLabelProvider();
    gray = PlatformUI.getWorkbench().getDisplay()
        .getSystemColor(SWT.COLOR_DARK_GRAY);
    normalTaskImage = TasksUiImages.TASK.createImage();
  }

  @Override
  public void dispose() {
    super.dispose();
    normalTaskImage.dispose();
    provider.dispose();
  }

  @Override
  public Color getBackground(Object element) {
    if (element instanceof ITask) {
      return provider.getBackground(element);
    }
    return super.getBackground(element);
  }

  @Override
  public Font getFont(Object element) {
    if (element instanceof ITask) {
      return provider.getFont(element);
    }
    return super.getFont(element);
  }

  @Override
  public Color getForeground(Object element) {
    if (element instanceof UnrecognizedTask) {
      return gray;
    }
    if (element instanceof ITask) {
      return provider.getForeground(element);
    }
    return super.getForeground(element);
  }

  @Override
  public Image getImage(Object element) {
    if (element instanceof ITask) {
      Image image = provider.getImage(element);
      if (image == null) {
        image = normalTaskImage;
      }
      return image;
    }
    return super.getImage(element);
  }

  @Override
  public String getText(Object element) {
    if (element instanceof ITask) {
      return provider.getText(element);
    }
    return super.getText(element);
  }
}
