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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Abstract {@link IConverter} class containing default implementations.
 * 
 * @param <F> The element to convert from.
 * @param <T> The element to convert to.
 */
public abstract class AbstractConverter<F, T> implements IConverter<F, T> {

  protected AbstractConverter() {
  }

  @Override
  public final T convert(F element) {
    checkNotNull(element, "Argument cannot be null");
    return doConvert(element);
  }

  /**
   * This method is called by {@link #convert(Object)} from the super class
   * after checking for null, subclasses can safely convert the element without
   * further checking.
   * 
   * @param element The element to convert from.
   * @return A converted element.
   */
  protected abstract T doConvert(F element);

}
