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

import org.eclipse.jface.viewers.TreePath;

import java.util.List;

import javax.annotation.Nullable;

/**
 * Builder to build tree paths from a given input element.
 */
public interface ITreePathBuilder {

  /**
   * Builds a collection of tree paths representing the leaves elements of the
   * tree built from the given input.
   * 
   * @param input the input element to build from.
   * @return a collection of tree paths, the collection may be empty but never
   *         null. If unable to build from the given input, an empty collection
   *         will be returned.
   */
  List<TreePath> build(@Nullable Object input);
}
