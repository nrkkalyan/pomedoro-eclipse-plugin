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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nullable;

/**
 * Utility class for working with {@link IMerger}.
 */
public class Mergers {

  /**
   * Merges a collection of elements into another collection. If any of the
   * element is not merged with another element, it will be added to the
   * collection instead. If merger is null, the second collection is simply 
   * added to the first collection.
   * 
   * @param merger The merger to use.
   * @param to The collection to merge the elements to.
   * @param from The elements to be merged into the collection.
   * @return The to collection.
   * @throws NullPointerException If any of the collections are null.
   */
  public static <T> Collection<T> merge(
      @Nullable IMerger<T> merger, Collection<T> to, Collection<T> from) {

    checkNotNull(to);
    checkNotNull(from);
    
    if (merger == null) {
      to.addAll(from);
      return to;
    }

    for (T element : from) {
      merge(merger, to, element);
    }
    return to;
  }

  /**
   * Merges a element into a collection. If merger is null or the element is not
   * merged with another element, it will be added to the collection instead.
   * 
   * @param merger The merger to use.
   * @param collection The collection to merge the element to.
   * @param item The element to be merged into the collection.
   * @return The collection.
   * @throws NullPointerException If any of the arguments are null.
   */
  public static <T> Collection<T> merge(
      @Nullable IMerger<T> merger, Collection<T> collection, T item) {

    checkNotNull(collection);
    checkNotNull(item);
    
    if (merger == null) {
      collection.add(item);
      return collection;
    }

    T mergedElement = null;
    for (Iterator<T> it = collection.iterator(); it.hasNext();) {
      T element = it.next();
      if (merger.isMergeable(element, item)) {
        mergedElement = merger.merge(element, item);

        // Removes the old one, the new one will be added after the loop:
        it.remove();
        break;
      }
    }

    if (mergedElement == null) {
      collection.add(item);
    } else {
      collection.add(mergedElement);
    }
    return collection;
  }
}
