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
package rabbit.data.store;

import java.util.Collection;

/**
 * Represents a storer that stores a particular type of objects.
 * 
 * @param <T> The object type.
 */
public interface IStorer<T> {

  /**
   * Stores the data.
   */
  void commit();

  /**
   * Insert a collection of objects to be stored.
   * @param collection The collection of objects.
   */
  void insert(Collection<? extends T> collection);

  /**
   * Inserts an object to be stored.
   * @param element The object.
   */
  void insert(T element);

}
