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

/**
 * A value provider that provides values for elements.
 */
public interface IValueProvider {

  /**
   * Gets the maximum value of all the elements.
   * @return The maximum value.
   */
  long getMaxValue();

  /**
   * Gets the value of the given element.
   * @param element The element.
   * @return The value.
   */
  long getValue(Object element);

  /**
   * Checks whether a cell should be painted.
   * @param element The element in the cell.
   * @return True to paint, false otherwise.
   */
  boolean shouldPaint(Object element);

}