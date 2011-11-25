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

import org.eclipse.jface.viewers.TreePath;
import org.joda.time.Duration;

/**
 * Converts a {@link TreePath} by checking its last segment, if the segment is a {@link Duration}
 * then the duration in milliseconds is returned, otherwise 0 is returned.
 */
public final class TreePathDurationConverter implements IConverter<TreePath> {

  @Override
  public long convert(TreePath element) {
    Object obj = element.getLastSegment();
    if (obj instanceof Duration) {
      return ((Duration) obj).getMillis();
    }
    return 0;
  }
}
