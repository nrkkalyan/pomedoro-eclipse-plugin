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
package rabbit.data.internal.xml;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * A utility class.
 */
public class DatatypeUtil {

  /**
   * An data type factory for creating XML data types.
   */
  public static DatatypeFactory datatypeFactory;

  static {
    try {
      datatypeFactory = DatatypeFactory.newInstance();
    } catch (DatatypeConfigurationException e) {

      // Try again?
      try {
        datatypeFactory = DatatypeFactory.newInstance();
      } catch (DatatypeConfigurationException ex) {
        e.printStackTrace(); // OK, I give up...
      }
    }
  }

  /**
   * Checks whether the two dates are representing the same year, month, and day
   * of month.
   * 
   * @param date1 The first date.
   * @param date2 The second date.
   * @return true if the two dates are representing the same date in time, false
   *         otherwise.
   */
  public static boolean isSameDate(DateTime date1, XMLGregorianCalendar date2) {

    return (date2.getYear() == date1.getYear())
        && (date2.getMonth() == date1.getMonthOfYear())
        && (date2.getDay() == date1.getDayOfMonth());
  }

  /**
   * Checks whether the two calendars are representing the same month in time.
   * 
   * @param cal1 The first calendar.
   * @param cal2 The second calendar.
   * @return true if the two calendars are representing the same month in time,
   *         false otherwise.
   */
  public static boolean isSameMonthInYear(DateTime cal1, DateTime cal2) {
    return (cal1.getYear() == cal2.getYear())
        && (cal1.getMonthOfYear() == cal2.getMonthOfYear());
  }

  /**
   * Checks whether the two dates are representing the same year and same month.
   * 
   * @param cal1 The first date.
   * @param cal2 The second date.
   * @return True if both dates are representing the same year and same month.
   */
  public static boolean isSameMonthInYear(DateTime cal1, LocalDate cal2) {
    return (cal1.getYear() == cal2.getYear())
        && (cal1.getMonthOfYear() == cal2.getMonthOfYear());
  }

  /**
   * Converts the given calendar to a {@link LocalDate}.
   * 
   * @param cal The calendar to convert.
   * @return A converted {@linkplain LocalDate}.
   */
  public static LocalDate toLocalDate(XMLGregorianCalendar cal) {
    return new LocalDate(cal.getYear(), cal.getMonth(), cal.getDay());
  }

  /**
   * Converts a {@link DateTime} to {@link XMLGregorianCalendar}. The converted
   * calendar is a date, which means only the year, month and day of month
   * fields are set.
   * 
   * @param cal The calendar to convert from.
   * @return The converted calendar.
   */
  public static XMLGregorianCalendar toXmlDate(DateTime cal) {
    return datatypeFactory.newXMLGregorianCalendarDate(cal.getYear(), cal
        .getMonthOfYear(), cal.getDayOfMonth(),
        DatatypeConstants.FIELD_UNDEFINED);
  }

  /**
   * Converts a {@link LocalDate} to {@link XMLGregorianCalendar}. The
   * converted calendar is a date, which means only the year, month and day of
   * month fields are set.
   * 
   * @param cal The calendar to convert from.
   * @return The converted calendar.
   */
  public static XMLGregorianCalendar toXmlDate(LocalDate cal) {
    return datatypeFactory.newXMLGregorianCalendarDate(cal.getYear(), cal
        .getMonthOfYear(), cal.getDayOfMonth(),
        DatatypeConstants.FIELD_UNDEFINED);
  }

  /**
   * Converts a {@code GregorianCalendar} to a {@code XMLGregorianCalendar} date
   * time.
   * 
   * @param cal The calendar to convert from.
   * @return A converted calendar.
   */
  public static XMLGregorianCalendar toXmlDateTime(GregorianCalendar cal) {
    return datatypeFactory.newXMLGregorianCalendar(cal);
  }
}
