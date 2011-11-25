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
package rabbit.ui.internal.util;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Represents a category which text and image. These categories are used to
 * categorize the data to be displayed to the user.
 */
public interface ICategory {

  /**
   * Gets the text of this category.
   * 
   * @return The text.
   */
  String getText();

  /**
   * Gets the image descriptor of this category.
   * 
   * @return The image descriptor.
   */
  ImageDescriptor getImageDescriptor();
}
