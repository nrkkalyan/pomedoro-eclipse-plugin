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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Set;

/**
 * Provides default implementation for {@link ICategoryProvider}.
 */
public final class CategoryProvider extends Observable
    implements ICategoryProvider {

  /**
   * Immutable collection of all supported categories.
   */
  private final Set<ICategory> allSupported;

  /**
   * Immutable list of currently selected categories.
   */
  private List<ICategory> selected;

  /**
   * Constructor.
   * @param supported all the supported to be supported.
   * @param defaultSelected the default selected categories.
   * @throws IllegalArgumentException if the supported categories does not
   *         contain all of the default selected categories.
   */
  public CategoryProvider(ICategory[] supported, ICategory... defaultSelected) {
    this.allSupported = ImmutableSet.copyOf(supported);
    this.selected = ImmutableList.copyOf(defaultSelected);

    checkArgument(allSupported.containsAll(selected));
  }

  @Override
  public Collection<ICategory> getUnselected() {
    return Collections2.filter(getAllSupported(), not(in(getSelected())));
  }

  @Override
  public List<ICategory> getSelected() {
    return selected;
  }

  @Override
  public void setSelected(ICategory... categories) {
    List<ICategory> newCategories = Lists.newArrayList(categories);
    newCategories.retainAll(getAllSupported());
    if (!selected.equals(newCategories)) {
      selected = ImmutableList.copyOf(newCategories);
      setChanged();
      notifyObservers();
    }
  }

  @Override
  public Collection<ICategory> getAllSupported() {
    return allSupported;
  }

}
