/* Copyright 2010 The Rabbit Eclipse Plug-in Project
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
package rabbit.data.internal.access.model;

import rabbit.data.access.model.IKey;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * A builder for building an immutable map containing keys of type {@link IKey}.
 * Instances of this class are reusable. Calling {@link #build()} multiple times
 * will build a map with the same content.
 */
public final class KeyMapBuilder {
  
  private final ImmutableMap.Builder<IKey<? extends Object>, Object> builder;
  
  /**
   * Constructs a new builder.
   */
  public KeyMapBuilder() {
    builder = ImmutableMap.builder();
  }

  /**
   * Associates a key with a value, adding the same key more than once will
   * cause the {@link #build()} method to fail.
   * @param key The key.
   * @param value The value.
   * @return The builder itself.
   */
  public <T> KeyMapBuilder put(IKey<T> key, T value) {
    builder.put(key, value);
    return this;
  }
  
  /**
   * Builds the immutable map.
   * @return The map containing the keys and values.
   * @throws IllegalArgumentException If duplicate keys exist in the map.
   */
  public Map<IKey<? extends Object>, Object> build() {
    return builder.build();
  }
}
