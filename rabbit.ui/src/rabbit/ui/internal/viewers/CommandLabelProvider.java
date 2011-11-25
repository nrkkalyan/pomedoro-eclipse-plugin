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
package rabbit.ui.internal.viewers;

import com.google.common.collect.Maps;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandImageService;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Label provider for {@link Command}.
 */
public final class CommandLabelProvider extends NullLabelProvider {

  private final ICommandImageService service;

  /**
   * Maps from command ID to image, the values are not unique, need to check
   * {@link Image#isDisposed()} on the image before calling
   * {@link Image#dispose()}.
   */
  private final Map<String, Image> images;

  private final Color gray;

  /**
   * Constructor.
   */
  public CommandLabelProvider() {
    images = Maps.newLinkedHashMap();
    service = (ICommandImageService) PlatformUI.getWorkbench()
        .getService(ICommandImageService.class);
    gray = PlatformUI.getWorkbench().getDisplay()
        .getSystemColor(SWT.COLOR_DARK_GRAY);
  }
  
  @Override
  public void dispose() {
    super.dispose();
    for (Image img : images.values()) {
      if (img != null && !img.isDisposed())
        img.dispose();
    }
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

  @Override
  public Image getImage(@Nullable Object element) {
    if (element instanceof Command) {
      String commandId = ((Command) element).getId();
      Image image = images.get(commandId);
      if (image == null) {
        ImageDescriptor des = service.getImageDescriptor(commandId);

        if (des != null) {
          image = des.createImage();
        }
        images.put(commandId, image);
      }
      return image;
    }
    return null;
  }

  @Override
  public String getText(@Nullable Object element) {
    if (element instanceof Command) {
      try {
        return ((Command) element).getName();
      } catch (NotDefinedException e) {
        return ((Command) element).getId();
      }
    }
    return null;
  }
}
