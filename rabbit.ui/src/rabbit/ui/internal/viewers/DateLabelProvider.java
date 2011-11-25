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

import rabbit.ui.internal.SharedImages;

import org.eclipse.swt.graphics.Image;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.annotation.Nullable;

/**
 * Label provider for {@link LocalDate} elements.
 */
public class DateLabelProvider extends NullLabelProvider {

  private final Image dateImage;
  private final DateTimeFormatter formatter;

  /**
   * Constructor.
   */
  public DateLabelProvider() {
    formatter = DateTimeFormat.longDate();
    dateImage = SharedImages.CALENDAR.createImage();
  }

  @Override
  public void dispose() {
    super.dispose();
    dateImage.dispose();
  }

  @Override
  public Image getImage(@Nullable Object element) {
    if (element instanceof LocalDate) {
      return dateImage;
    }
    return super.getImage(element);
  }

  @Override
  public String getText(@Nullable Object element) {
    if (element instanceof LocalDate) {
      return formatter.print((LocalDate) element);
    }
    return super.getText(element);
  }
}
