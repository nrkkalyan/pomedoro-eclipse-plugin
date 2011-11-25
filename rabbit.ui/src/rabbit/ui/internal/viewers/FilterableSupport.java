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
package rabbit.ui.internal.viewers;

import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Provides a default implementation of {@link IFilterable}.
 */
public final class FilterableSupport implements IFilterable {

  private final Set<Predicate<? super Object>> filters;

  /**
   * Constructor.
   */
  public FilterableSupport() {
    filters = Sets.newHashSet();
  }

  @Override
  public void addFilter(Predicate<? super Object> filter) {
    filters.add(filter);
  }

  @Override
  public void removeFilter(Predicate<? super Object> filter) {
    filters.remove(filter);
  }

  @Override
  public Object[] filter(Object[] elements) {
    List<Object> children = Arrays.asList(elements);
    return Collections2.filter(children, not(or(filters))).toArray();
  }

}
