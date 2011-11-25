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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newLinkedList;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TreeItem;

import java.util.List;

/**
 * A label provider for a tree viewer column that paints horizontal bars in the
 * cells.
 */
public class TreeViewerCellPainter extends StyledCellLabelProvider {

  private Color customBackground;
  private Color systemForeground;
  private final IValueProvider valueProvider;
  private final boolean isLinux;

  /**
   * @param valueProvider the provider for getting the values of each tree path.
   * @throws NullPointerException if argument is null.
   */
  public TreeViewerCellPainter(IValueProvider valueProvider) {
    this.valueProvider = checkNotNull(valueProvider, "valueProvider");
    this.isLinux = Platform.getOS().equals(Platform.OS_LINUX);
  }

  @Override
  public void dispose() {
    super.dispose();
    customBackground.dispose();
  }

  /**
   * Gets the value provider of this painter.
   * 
   * @return The value provider.
   */
  public IValueProvider getValueProvider() {
    return valueProvider;
  }

  @Override
  public void initialize(ColumnViewer viewer, ViewerColumn column) {
    super.initialize(viewer, column);
    Display display = viewer.getControl().getDisplay();
    systemForeground = display.getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    customBackground = createColor(display);
  }

  @Override
  public void paint(Event event, Object element) {
    if (!valueProvider.shouldPaint(element)) {
      return;
    }

    TreePath path = getTreePath((TreeItem) event.item);
    int columnWidth = event.gc.getClipping().width;
    int width = getWidth(columnWidth, path);
    if (width == 0) {
      return;
    }
    int x = event.x;
    int y = event.y;
    int height = event.height - 1;

    GC gc = event.gc;
    Color oldBackground = gc.getBackground();
    Color oldForeground = gc.getForeground();
    int oldAnti = gc.getAntialias();
    int oldAlpha = gc.getAlpha();

    /*
     * On Linux, enabling GC's antialias or changing the alpha will sometimes
     * cause the color bar to be half drawn.
     */
    if (!isLinux) {
      int alpha = (int) (width / (float) columnWidth * 255);
      if (alpha < 100) {
        alpha = 100;
      }
      gc.setAlpha(alpha);
      gc.setAntialias(SWT.ON);
    }
    gc.setBackground(customBackground);
    gc.fillRectangle(x, y, 2, height);
    gc.fillRoundRectangle(x, y, width, height, 4, 4);

    gc.setAlpha(oldAlpha);
    gc.setAntialias(oldAnti);

    gc.setForeground(systemForeground);
    gc.drawLine(x, y, x, y + height - 1);
    gc.drawLine(x + width, y, x + width, y + height - 1);

    gc.setBackground(oldBackground);
    gc.setForeground(oldForeground);
  }

  /**
   * Creates the desired color for painting the cells. Callers of this method
   * must dispose the returned color themselves.
   * 
   * @param display the display to create the color for.
   * @return a new color.
   */
  protected Color createColor(Display display) {
    return new Color(display, 84, 141, 212);
  }

  private TreePath getTreePath(TreeItem item) {
    List<Object> segments = newLinkedList();
    while (item != null) {
      segments.add(0, item.getData());
      item = item.getParentItem();
    }
    return new TreePath(segments.toArray());
  }

  /**
   * Gets the width in pixels for the paint.
   */
  private int getWidth(int columnWidth, TreePath path) {
    long maxValue = valueProvider.getMaxValue();
    if (maxValue == 0) {
      return 0;
    }

    long value = valueProvider.getValue(path);
    int width = (int) (value * columnWidth / (double) valueProvider
        .getMaxValue());
    width = ((value != 0) && (width == 0)) ? 2 : width;

    if (value != 0 && width < 2) {
      width = 2;
    }
    return width;
  }
}
