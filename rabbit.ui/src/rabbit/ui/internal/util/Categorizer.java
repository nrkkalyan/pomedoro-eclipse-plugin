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
package rabbit.ui.internal.util;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Default implementation of {@link ICategorizer}.
 * <p>
 * An instance of this class takes a map of data and produces results from that.
 * </p>
 */
public final class Categorizer implements ICategorizer {

  private final Map<Predicate<? super Object>, ICategory> map;

  /**
   * Constructor.
   * @param map The map of data specifies how to categorize elements.
   */
  public Categorizer(Map<? extends Predicate<? super Object>, ? extends ICategory> map) {
    this.map = ImmutableMap.copyOf(map);
  }

  @Override
  public ICategory getCategory(@Nullable Object element) {
    for (Map.Entry<Predicate<? super Object>, ICategory> entry : map.entrySet()) {
      if (entry.getKey().apply(element)) {
        return entry.getValue();
      }
    }
    return null;
  }

  @Override
  public boolean hasCategory(ICategory category) {
    return map.values().contains(category);
  }

}
