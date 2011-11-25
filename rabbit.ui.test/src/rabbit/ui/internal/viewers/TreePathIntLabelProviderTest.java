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

import static com.google.common.base.Strings.nullToEmpty;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.lang.reflect.Constructor;

/**
 * Tests for {@link TreePathIntLabelProvider}.
 */
public final class TreePathIntLabelProviderTest {

  @Test
  public void getValueProviderShouldReturnTheValueProvider() {
    IValueProvider valueProvider = mock(IValueProvider.class);
    TreePathIntLabelProvider labelProvider = new TreePathIntLabelProvider(
        valueProvider);
    assertThat(labelProvider.getValueProvider(), is(valueProvider));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowAnExceptionIfConstructedWithoutAValueProvider() {
    new TreePathIntLabelProvider(null);
  }

  @Test
  public void updateShouldSetTheCellColorUsingTheGivenColorProvider()
      throws Exception {
    ViewerCell cell = newCell(0, new Object());

    IValueProvider valueProvider = mock(IValueProvider.class);
    given(valueProvider.shouldPaint(cell.getElement())).willReturn(TRUE);

    Display display = PlatformUI.getWorkbench().getDisplay();
    Color foreground = display.getSystemColor(SWT.COLOR_CYAN);
    Color background = display.getSystemColor(SWT.COLOR_BLUE);
    IColorProvider colors = mock(IColorProvider.class);
    given(colors.getForeground(cell.getElement())).willReturn(foreground);
    given(colors.getBackground(cell.getElement())).willReturn(background);

    TreePathIntLabelProvider labelProvider =
        new TreePathIntLabelProvider(valueProvider, colors);

    labelProvider.update(cell);
    assertThat(cell.getForeground(), is(foreground));
    assertThat(cell.getBackground(), is(background));
  }

  @Test
  public void updateShouldSetTheCellTextToBlankIfThePathShouldNotBePainted()
      throws Exception {
    ViewerCell cell = newCell(0, new Object());

    IValueProvider valueProvider = mock(IValueProvider.class);
    given(valueProvider.shouldPaint(cell.getElement())).willReturn(FALSE);
    given(valueProvider.getValue(cell.getViewerRow().getTreePath()))
        .willReturn(Long.valueOf(1024));

    TreePathIntLabelProvider labelProvider =
        new TreePathIntLabelProvider(valueProvider);

    labelProvider.update(cell);
    assertThat(nullToEmpty(cell.getText()), is(""));
  }

  @Test
  public void updateShouldSetTheCellTextToTheValueOfThePathIfThePathIsToBePainted()
      throws Exception {
    long value = 1024;
    ViewerCell cell = newCell(0, new Object());

    IValueProvider valueProvider = mock(IValueProvider.class);
    given(valueProvider.shouldPaint(cell.getElement())).willReturn(TRUE);
    given(valueProvider.getValue(cell.getViewerRow().getTreePath()))
        .willReturn(value);

    TreePathIntLabelProvider labelProvider =
        new TreePathIntLabelProvider(valueProvider);

    labelProvider.update(cell);
    assertThat(cell.getText(), is(String.valueOf(value)));
  }

  private ViewerCell newCell(int columnIndex, Object element) throws Exception {
    final String[] text = new String[1];
    final Color[] background = new Color[1];
    final Color[] foreground = new Color[1];

    ViewerRow row = mock(ViewerRow.class);
    given(row.getBackground(columnIndex)).willAnswer(new Answer<Color>() {
      @Override
      public Color answer(InvocationOnMock invocation) throws Throwable {
        return background[0];
      }
    });
    given(row.getForeground(columnIndex)).willAnswer(new Answer<Color>() {
      @Override
      public Color answer(InvocationOnMock invocation) throws Throwable {
        return foreground[0];
      }
    });
    given(row.getText(columnIndex)).willAnswer(new Answer<String>() {
      @Override
      public String answer(InvocationOnMock invocation) throws Throwable {
        return text[0];
      }
    });
    given(row.getTreePath()).willReturn(new TreePath(new Object[]{element}));

    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        background[0] = (Color) invocation.getArguments()[1];
        return null;
      }
    }).when(row).setBackground(eq(columnIndex), Mockito.<Color> any());

    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        foreground[0] = (Color) invocation.getArguments()[1];
        return null;
      }
    }).when(row).setForeground(eq(columnIndex), Mockito.<Color> any());

    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        text[0] = (String) invocation.getArguments()[1];
        return null;
      }
    }).when(row).setText(eq(columnIndex), Mockito.anyString());

    Constructor<ViewerCell> constructor = ViewerCell.class
        .getDeclaredConstructor(ViewerRow.class, int.class, Object.class);
    constructor.setAccessible(true);
    return constructor.newInstance(row, columnIndex, element);
  }
}
