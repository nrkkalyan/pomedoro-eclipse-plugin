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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @see CompositeCellLabelProvider
 */
public class CompositeCellLabelProviderTest {

  private static Display display;

  @BeforeClass
  public static void setupBeforeClass() {
    display = new Display();
  }

  @AfterClass
  public static void tearDownAfterClass() {
    display.dispose();
  }

  @Test
  public void getFontShouldReturnTheFontFromTheInternalLabelProvider() {
    Font font = new Font(display, "Sans", 10, SWT.BOLD);
    try {
      ColumnLabelProvider p1 = mock(ColumnLabelProvider.class);
      ColumnLabelProvider p2 = mock(ColumnLabelProvider.class);
      given(p1.getFont(any())).willReturn(null);
      given(p2.getFont(any())).willReturn(font);

      CompositeCellLabelProvider provider = create(p1, p2);
      assertThat(provider.getFont(""), equalTo(font));
    } finally {
      font.dispose();
    }
  }

  @Test
  public void getForegroundShouldReturnTheForegroundFromTheInternalLabelProvider() {
    Color color = display.getSystemColor(SWT.COLOR_BLACK);

    ColumnLabelProvider p1 = mock(ColumnLabelProvider.class);
    ColumnLabelProvider p2 = mock(ColumnLabelProvider.class);
    given(p1.getForeground(any())).willReturn(null);
    given(p2.getForeground(any())).willReturn(color);

    CompositeCellLabelProvider provider = create(p1, p2);
    assertThat(provider.getForeground(""), equalTo(color));
  }

  @Test
  public void getBackgroundShouldReturnTheBackgroundFromTheInternalLabelProvider() {
    Color color = display.getSystemColor(SWT.COLOR_BLACK);

    ColumnLabelProvider p1 = mock(ColumnLabelProvider.class);
    ColumnLabelProvider p2 = mock(ColumnLabelProvider.class);
    given(p1.getBackground(any())).willReturn(null);
    given(p2.getBackground(any())).willReturn(color);

    CompositeCellLabelProvider provider = create(p1, p2);
    assertThat(provider.getBackground(""), equalTo(color));
  }

  @Test
  public void disposeShouldDisposeAllTheInternalLabelProviders() {
    final int[] disposeCount = {0};
    Answer<Void> answer = new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        disposeCount[0]++;
        return null;
      }
    };
    ILabelProvider p1 = mock(ILabelProvider.class);
    ILabelProvider p2 = mock(ILabelProvider.class);
    doAnswer(answer).when(p1).dispose();
    doAnswer(answer).when(p2).dispose();

    CompositeCellLabelProvider provider = create(p1, p2);
    provider.dispose();
    assertThat(disposeCount[0], equalTo(2));
  }

  @Test
  public void getImageShouldReturnTheImageFromTheInternalLabelProvider()
      throws Exception {
    Image image = new Image(display, 1, 1);

    ILabelProvider p1 = mock(ILabelProvider.class);
    ILabelProvider p2 = mock(ILabelProvider.class);
    given(p1.getImage(any())).willReturn(null);
    given(p2.getImage(any())).willReturn(image);

    CompositeCellLabelProvider provider = create(p1, p2);
    assertThat(provider.getImage(""), equalTo(image));
  }

  @Test
  public void getStyledTextShouldNeverReturnNull() {
    // Returning null can cause problems with the JFace framework.

    Object element = new Object();
    CompositeCellLabelProvider provider = create();

    String expectedString = new StyledString(element.toString()).getString();
    StyledString actual = provider.getStyledText(element);
    assertThat(actual, notNullValue());
    assertThat(actual.getString(), equalTo(expectedString));
  }

  @Test
  public void getStyledTextShouldReturnThePlainTextOfTheElementIfThereIsNoStyledTextLabelProvider() {
    String text = "Hello";
    Object element = new Object();
    ILabelProvider p = mock(ILabelProvider.class);
    given(p.getText(element)).willReturn(text);

    CompositeCellLabelProvider provider = create(p);
    assertThat(provider.getStyledText(element).getString(),
        equalTo(new StyledString(text).getString()));
  }

  @Test
  public void getStyledTextShouldReturnTheStyledTextFromTheInternalStyledTextLabelProvider() {
    StyledString expectedText = new StyledString("Hello");

    Object element = new Object();
    IStyledLabelProvider p = mock(IStyledLabelProvider.class);
    given(p.getStyledText(element)).willReturn(expectedText);

    CompositeCellLabelProvider provider = create(p);
    assertThat(provider.getStyledText(element), equalTo(expectedText));
  }

  @Test
  public void getTextShouldReturnTheTextFromTheInternalLabelProvider()
      throws Exception {
    String text = "Hello";

    ILabelProvider p1 = mock(ILabelProvider.class);
    ILabelProvider p2 = mock(ILabelProvider.class);
    given(p1.getText(any())).willReturn(null);
    given(p2.getText(any())).willReturn(text);

    CompositeCellLabelProvider provider = create(p1, p2);
    assertThat(provider.getText(""), equalTo(text));
  }

  @Test
  public void shouldCloneTheArrayOfLabelProviders() throws Exception {
    String text = "Hello";
    ILabelProvider theProvider = mock(ILabelProvider.class);
    given(theProvider.getText(this)).willReturn(text);

    ILabelProvider[] providerArray = {theProvider};
    CompositeCellLabelProvider provider = create(providerArray);
    providerArray[0] = null;

    assertThat(provider.getText(this), equalTo(text));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowAnExceptionIfConstructedWithANullLabelProvider()
      throws Exception {
    create(new ILabelProvider[]{null});
  }

  /**
   * @see CompositeCellLabelProvider#CompositeCellLabelProvider(IBaseLabelProvider...)
   */
  private CompositeCellLabelProvider create(IBaseLabelProvider... providers) {
    return new CompositeCellLabelProvider(providers);
  }
}
