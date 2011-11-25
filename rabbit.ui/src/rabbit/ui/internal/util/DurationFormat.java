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

import java.text.DecimalFormat;
import java.text.Format;

/**
 * Utility class for formating durations.
 */
public class DurationFormat {
  
  private static int SECOND = 1000;
  private static int MINUTE = SECOND * 60;
  private static int HOUR = MINUTE * 60;

  private static final Format formatter = new DecimalFormat("#00");

  /**
   * Formats the given duration to a human readable string. Examples of values 
   * passed in and strings returned:
   * <pre>
   *    1000:              1 s
   *   60000:       1 min 00 s
   * 3600000: 1 hr 00 min 00 s
   * </pre>
   * This method is synchronized.
   * 
   * @param durationInMillis The duration in milliseconds.
   * @return A formatted string.
   */
  public static synchronized String format(long durationInMillis) {
    int hours = (int) (durationInMillis / HOUR);
    durationInMillis = durationInMillis % HOUR;

    int minutes = (int) (durationInMillis / MINUTE);
    durationInMillis = durationInMillis % MINUTE;

    int seconds = (int) (durationInMillis / SECOND);

    StringBuilder result = new StringBuilder();
    if (hours > 0) {
      result.append(hours);
      result.append(" hr ");
    }

    if (minutes > 0) {
      if (hours > 0) {
        result.append(formatter.format(minutes));
      } else {
        result.append(minutes);
      }
      result.append(" min ");
      result.append(formatter.format(seconds));
    } else {
      if (hours > 0) {
        result.append(formatter.format(minutes));
        result.append(" min ");
        result.append(formatter.format(seconds));
      } else {
        result.append(seconds);
      }
    }
    result.append(" s");

    return result.toString();
  }
}
