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
package rabbit.data.access.model;

/**
 * Represents and contains method to create a basic key.
 * An instance of this class equals only to itself.
 */
public class Key<T> implements IKey<T> {

  /**
   * Creates a new key.
   * @param <T> The type of the value represents by this key.
   * @return A key.
   */
  public static <T> IKey<T> create() {
    return new Key<T>();
  }
  
  /** Private constructor */
  private Key() {}
}
