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
package rabbit.ui.internal.util;

/**
 * Represents a provider that holds a {@link ICategory} that represents a
 * visual.
 */
public interface IVisualProvider {

  /**
   * @return the visual category.
   */
  ICategory getVisualCategory();

  /**
   * @param category the new visual category.
   * @return true if the category is supported and accepted, false otherwise.
   */
  boolean setVisualCategory(ICategory category);
}
