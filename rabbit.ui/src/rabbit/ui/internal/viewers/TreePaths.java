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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayListWithCapacity;

import com.google.common.base.Objects;

import org.eclipse.jface.viewers.TreePath;

import java.util.List;

/**
 * Utility class for working with {@link TreePath}s.
 */
public class TreePaths {

  /**
   * Returns the index of the given segment in the given path.
   * @param path the tree path to search the segment.
   * @param segment the segment to find the index for.
   * @return the index, or a negative number if not found.
   */
  public static int indexOf(TreePath path, Object segment) {
    checkNotNull(path, "path");
    checkNotNull(segment, "segment");
    
    for (int i = 0; i < path.getSegmentCount(); ++i) {
      if (Objects.equal(path.getSegment(i), segment)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Gets the head path of a tree path.
   * @param path the path.
   * @param endIndex the ending index on the given path, exclusive.
   * @return the head path.
   * @throws IllegalArgumentException if {@code endIndex < 0} ||
   *         {@code endIndex > path.getSegmentCount()}.
   */
  public static TreePath headPath(TreePath path, int endIndex) {
    checkArgument(endIndex >= 0, "endIndex is negative");
    checkArgument(
        endIndex <= path.getSegmentCount(),
        "endIndex is greater than length of path");

    List<Object> segments = newArrayListWithCapacity(endIndex);
    for (int i = 0; i < endIndex; ++i) {
      segments.add(path.getSegment(i));
    }
    return new TreePath(segments.toArray());
  }

  private TreePaths() {}
}
