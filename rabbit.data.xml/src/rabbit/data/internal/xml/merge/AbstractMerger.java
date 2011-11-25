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
package rabbit.data.internal.xml.merge;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Abstract class containing default implementations for an {@link IMerger}.
 * 
 * @param <T> The object type to merge.
 */
public abstract class AbstractMerger<T> implements IMerger<T> {
  
  protected AbstractMerger() {
  }

  @Override
  public final boolean isMergeable(T t1, T t2) {
    checkNotNull(t1);
    checkNotNull(t2);

    return doIsMergeable(t1, t2);
  }

  @Override
  public final T merge(T t1, T t2) {
    if (!isMergeable(t1, t2))
      throw new IllegalArgumentException("Objects are not mergeable");

    return doMerge(t1, t2);
  }

  /**
   * This method is called from {@link #isMergeable(Object, Object)} by the
   * super class after checking both arguments are not null. Subclasses can
   * safely compare the objects without further checking for null.
   * <p>
   * This method should not be invoked by subclasses.
   * </p>
   * 
   * @param t1 The first object.
   * @param t2 The second object.
   * @return True if the objects are mergeable, false otherwise.
   * 
   */
  protected abstract boolean doIsMergeable(T t1, T t2);

  /**
   * This method will be called from {@link #merge(Object, Object)} by the super
   * class after checking {@link #isMergeable(Object, Object)} return true on
   * the two objects, subclasses can safely merge the two objects without
   * further checking.
   * <p>
   * This method should not be invoked by subclasses.
   * </p>
   * 
   * @param t1 The first object.
   * @param t2 The second object.
   * @return A new object as a result of the two objects being merged.
   */
  protected abstract T doMerge(T t1, T t2);
}
