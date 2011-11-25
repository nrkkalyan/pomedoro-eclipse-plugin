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
package rabbit.data.internal.xml.convert;

/**
 * A one way converter for converting from one object type to another.
 * 
 * @param <F> The object type to convert from.
 * @param <T> The object type to convert to.
 */
public interface IConverter<F, T> {

  /**
   * Converts the given element.
   * 
   * @param element The element to be converted.
   * @return A converted element.
   * @throws NullPointerException If element is null.
   */
  T convert(F element);

}
