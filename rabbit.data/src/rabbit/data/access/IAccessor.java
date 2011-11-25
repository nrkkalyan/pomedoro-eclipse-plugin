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
package rabbit.data.access;

import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Represents a data accessor to get data out of a data store.
 * 
 * @param <T> The return data type.
 */
public interface IAccessor<T> {

  /**
   * Gets the data between the dates, inclusive.
   * 
   * @param start The start date.
   * @param end The end Date.
   * @return A collection of data, or an empty collection if no data is found.
   * @throws NullPointerException If any of the arguments is null.
   */
  Collection<T> getData(LocalDate start, LocalDate end);
}
