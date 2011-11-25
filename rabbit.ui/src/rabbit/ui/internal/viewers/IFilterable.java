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

import com.google.common.base.Predicate;

/**
 * Represents a filterer that can filter elements based on the filters
 * configured.
 */
public interface IFilterable {

  /**
   * Adds the given filter.
   * @param filter a filter to filter unwanted elements.
   */
  void addFilter(Predicate<? super Object> filter);

  /**
   * Removes the given filter.
   * @param filter the filter to be removed.
   */
  void removeFilter(Predicate<? super Object> filter);

  /**
   * Filters the given elements using the filters configured.
   * @param elements the elements to be filtered, will not be modified.
   * @return a new array containing the remaining elements after filtering.
   */
  Object[] filter(Object[] elements);
}
