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

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @see NullLabelProvider
 */
public class NullLabelProviderTest {

  protected NullLabelProvider provider;

  @Test
  public void getBackgroundShouldReturnNullIfElementIsNotRecognized() {
    assertThat(provider.getBackground(getUnrecognizedElement()), nullValue());
  }

  @Test
  public void getStyledStringShouldReturnNullIfElementIsNotRecognized() {
    assertThat(provider.getStyledText(getUnrecognizedElement()), nullValue());
  }

  @Test
  public void getFontShouldReturnNullIfElementIsNotRecognized() {
    assertThat(provider.getFont(getUnrecognizedElement()), nullValue());
  }

  @Test
  public void getForegroundShouldReturnNullIfElementIsNotRecognized() {
    assertThat(provider.getForeground(getUnrecognizedElement()), nullValue());
  }

  @Test
  public void getImageShouldReturnNullIfElementIsNotRecognized() {
    assertThat(provider.getImage(getUnrecognizedElement()), nullValue());
  }

  @Test
  public void getTextShouldReturnNullIfElementIsNotRecognized() {
    assertThat(provider.getText(getUnrecognizedElement()), nullValue());
  }

  @Before
  public void setUp() {
    provider = create();
  }

  @After
  public void tearDown() {
    provider.dispose();
  }

  /**
   * Creates a {@link NullLabelProvider} for testing.
   * @return a object to be tested.
   */
  protected NullLabelProvider create() {
    return new NullLabelProvider() {};
  }

  /**
   * Gets an element that is not recognized by the label provider. Default
   * implementation returns {@code this}.
   * @return an element that is not recognized by the label provider.
   */
  protected Object getUnrecognizedElement() {
    return this;
  }
}
