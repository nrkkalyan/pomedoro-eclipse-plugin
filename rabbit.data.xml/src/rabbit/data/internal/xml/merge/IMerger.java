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
package rabbit.data.internal.xml.merge;

/**
 * An interface contains utility methods for merging data objects. These objects
 * usually consist of two "parts", one identify part and one value part, if the
 * identity parts are equal for two objects, then they are considered as
 * mergeable.
 */
public interface IMerger<T> {

  /**
   * Checks whether the two objects are mergeable.
   * 
   * @param t1 The first object.
   * @param t2 The second object.
   * @return True if the objects are mergeable, false otherwise.
   * @throws NullPointerException If any of the arguments is null.
   */
  boolean isMergeable(T t1, T t2);

  /**
   * Merges the two objects into one.
   * 
   * @param t1 The first object.
   * @param t2 The second object.
   * @return An object that is the result of merging the two parameter objects.
   * @throws IllegalArgumentException If {@link #isMergeable(Object, Object)}
   *           returns false on the two object.
   */
  T merge(T t1, T t2);
}
