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

import rabbit.ui.internal.viewers.DateLabelProvider;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

/**
 * @see DateLabelProvider
 */
public class DateLabelProviderTest extends NullLabelProviderTest {

  @Test
  public void getTextShouldReturnTheLongDateFormatOfTheDate() {
    LocalDate date = new LocalDate();
    String expected = DateTimeFormat.longDate().print(date);
    String actual = provider.getText(date);
    assertThat(actual, equalTo(expected));
  }

  @Test
  public void getImageShouldReturnANonnullImageForADate() {
    assertThat(provider.getImage(new LocalDate()), notNullValue());
  }

  @Override
  protected DateLabelProvider create() {
    return new DateLabelProvider();
  }
}
