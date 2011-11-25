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
package rabbit.ui.internal.dialogs;

import rabbit.ui.internal.util.ICategory;

import com.google.common.collect.Maps;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import java.util.Map;

/**
 * Label provider for {@link ICategory}
 */
public class CategoryLabelProvider extends LabelProvider {

  private Map<ICategory, Image> images;

  /** Constructor */
  public CategoryLabelProvider() {
    images = Maps.newHashMap();
  }

  @Override
  public void dispose() {
    super.dispose();
    for (Image img : images.values()) {
      if (img != null) {
        img.dispose();
      }
    }
  }

  @Override
  public String getText(Object element) {
    return ((ICategory) element).getText();
  }

  @Override
  public Image getImage(Object element) {
    if (images.containsKey(element)) {
      return images.get(element);
    }

    ICategory category = (ICategory) element;
    ImageDescriptor des = category.getImageDescriptor();
    Image image = (des != null) ? des.createImage() : null;
    images.put(category, image);
    return image;
  }
}
