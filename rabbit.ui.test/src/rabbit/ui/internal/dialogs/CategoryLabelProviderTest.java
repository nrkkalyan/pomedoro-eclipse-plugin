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

import rabbit.ui.internal.dialogs.CategoryLabelProvider;
import rabbit.ui.internal.util.ICategory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @see CategoryLabelProvider
 */
public class CategoryLabelProviderTest {

  private static CategoryLabelProvider labelProvider;

  private ICategory category = new ICategory() {
    @Override
    public ImageDescriptor getImageDescriptor() {
      return PlatformUI.getWorkbench().getSharedImages()
          .getImageDescriptor(ISharedImages.IMG_DEF_VIEW);
    }

    @Override
    public String getText() {
      return "SampleCategory";
    }
  };

  @AfterClass
  public static void afterClass() {
    labelProvider.dispose();
  }

  @BeforeClass
  public static void beforeClass() {
    labelProvider = new CategoryLabelProvider();
  }

  @Test
  public void testGetText() {
    assertEquals(category.getText(), labelProvider.getText(category));
  }

  @Test
  public void testGetImage() {
    assertNotNull(labelProvider.getImage(category));
  }
}
