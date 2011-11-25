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

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import rabbit.ui.internal.util.PageDescriptor;

/**
 * Label provider for {@link PageDescriptor}.
 */
public class PageDescriptorLabelProvider extends StyledCellLabelProvider 
    implements ILabelProvider {
  
  private final ImageRegistry images;
  
  /** Constructs a new label provider. */
  public PageDescriptorLabelProvider() {
    images = new ImageRegistry();
  }

  @Override
  public void dispose() {
    super.dispose();
    images.dispose();
  }
  
  @Override
  public Image getImage(Object element) {
    if (!(element instanceof PageDescriptor)) {
      return null;
    }
    
    PageDescriptor page = (PageDescriptor) element;
    if (page.getImage() == null) {
      return null;
    }
    Image image = images.get(page.getName());
    if (image == null) {
      image = page.getImage().createImage();
      images.put(page.getName(), image);
    }
    return image;
  }
  
  @Override
  public String getText(Object element) {
    if (!(element instanceof PageDescriptor)) {
      return element.toString();
    }
    return ((PageDescriptor) element).getName();
  }

  @Override
  public String getToolTipText(Object element) {
    if (!(element instanceof PageDescriptor)) {
      return super.getToolTipText(element);
    }
    return ((PageDescriptor) element).getDescription();
  }

  @Override
  public void update(ViewerCell cell) {
    super.update(cell);
    cell.setText(getText(cell.getElement()));
    cell.setImage(getImage(cell.getElement()));
  }

  @Override
  public boolean useNativeToolTip(Object object) {
    return true;
  }
}
