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

import rabbit.ui.internal.util.DurationFormat;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerCell;

import javax.annotation.Nullable;

/**
 * A {@link CellLabelProvider} that treats the value of a {@link TreePath} as a
 * duration in milliseconds, then formats that duration into human readable
 * string and use that as the label of the cell representing the path. The value
 * of the path is supplied by a {@link IValueProvider}, if
 * {@link IValueProvider#shouldPaint(Object)} returns <code>false</code> on a
 * object of a cell, then the text of the cell representing the path will be
 * blank.
 */
public final class TreePathDurationLabelProvider extends CellLabelProvider {

  private final IValueProvider valueProvider;
  private final IColorProvider colorProvider;

  /**
   * @param valueProvider the value provider to provide value for the tree
   *        paths.
   */
  public TreePathDurationLabelProvider(IValueProvider valueProvider) {
    this(valueProvider, null);
  }

  /**
   * @param valueProvider the value provider to provide value for the tree
   *        paths.
   * @param colorProvider the optional {@link IColorProvider} to color the
   *        labels.
   */
  public TreePathDurationLabelProvider(
      IValueProvider valueProvider, @Nullable IColorProvider colorProvider) {
    this.valueProvider = checkNotNull(valueProvider, "valueProvider");
    this.colorProvider = colorProvider;
  }

  /**
   * @return the value provider that provides the tree path values.
   */
  public IValueProvider getValueProvider() {
    return valueProvider;
  }

  @Override
  public void update(ViewerCell cell) {
    TreePath path = cell.getViewerRow().getTreePath();
    String text = null;
    if (getValueProvider().shouldPaint(cell.getElement())) {
      text = DurationFormat.format(getValueProvider().getValue(path));
    }
    cell.setText(text);

    if (colorProvider != null) { // TOOD test
      cell.setBackground(colorProvider.getBackground(cell.getElement()));
      cell.setForeground(colorProvider.getForeground(cell.getElement()));
    }
  }
}
