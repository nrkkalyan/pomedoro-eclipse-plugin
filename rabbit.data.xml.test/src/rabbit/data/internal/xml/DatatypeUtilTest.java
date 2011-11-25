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

import static rabbit.data.internal.xml.DatatypeUtil.isSameDate;
import static rabbit.data.internal.xml.DatatypeUtil.isSameMonthInYear;
import static rabbit.data.internal.xml.DatatypeUtil.toLocalDate;
import static rabbit.data.internal.xml.DatatypeUtil.toXmlDate;
import static rabbit.data.internal.xml.DatatypeUtil.toXmlDateTime;

import rabbit.data.internal.xml.DatatypeUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Test for {@link DatatypeUtil}
 */
public class DatatypeUtilTest {

  @Test
  public void testIsSameDate() throws Exception {
    DateTime cal = new DateTime();

    XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance()
        .newXMLGregorianCalendarDate(1, 1, 1, 1);
    assertFalse(isSameDate(cal, xmlCal));

    cal = new DateTime(1, 1, 1, 0, 0, 0, 0);
    assertTrue(isSameDate(cal, xmlCal));
  }

  @Test
  public void testIsSameMonthInYear_DateTimeAndDateTime() {

    DateTime cal1 = new DateTime();
    DateTime cal2 = new DateTime();
    assertTrue(isSameMonthInYear(cal1, cal2));

    cal2 = cal2.plusMonths(1);
    assertFalse(isSameMonthInYear(cal1, cal2));
  }

  @Test
  public void testIsSameMonthInYear_DateTimeAndLocalDate() {
    DateTime dateTime = new DateTime();
    assertTrue(isSameMonthInYear(dateTime, dateTime.toLocalDate()));
    assertFalse(isSameMonthInYear(dateTime, dateTime.toLocalDate()
        .plusMonths(1)));
    assertFalse(isSameMonthInYear(dateTime, dateTime.toLocalDate().plusYears(1)));
  }

  @Test
  public void testToLocalDate() {
    XMLGregorianCalendar cal = toXmlDate(new DateTime());
    LocalDate date = toLocalDate(cal);
    assertEquals(cal.getYear(), date.getYear());
    assertEquals(cal.getMonth(), date.getMonthOfYear());
    assertEquals(cal.getDay(), date.getDayOfMonth());
  }

  @Test
  public void testToXmlDate_fromDateTime() {

    DateTime cal = new DateTime();
    XMLGregorianCalendar xmlCal = toXmlDate(cal);

    assertEquals(cal.getYear(), xmlCal.getYear());
    // Calendar.MONTH is zero based, xmlCal is one based.
    assertEquals(cal.getMonthOfYear(), xmlCal.getMonth());
    assertEquals(cal.getDayOfMonth(), xmlCal.getDay());
  }

  @Test
  public void testToXmlDate_fromLocalDate() {

    LocalDate cal = new LocalDate();
    XMLGregorianCalendar xmlCal = toXmlDate(cal);

    assertEquals(cal.getYear(), xmlCal.getYear());
    // Calendar.MONTH is zero based, xmlCal is one based.
    assertEquals(cal.getMonthOfYear(), xmlCal.getMonth());
    assertEquals(cal.getDayOfMonth(), xmlCal.getDay());
  }

  @Test
  public void testToXmlDateTime() {
    GregorianCalendar cal = new GregorianCalendar();
    XMLGregorianCalendar xmlCal = toXmlDateTime(cal);
    assertEquals(cal.get(Calendar.YEAR), xmlCal.getYear());
    // Calendar.MONTH is zero based, xmlCal is one based.
    assertEquals(cal.get(Calendar.MONTH) + 1, xmlCal.getMonth());
    assertEquals(cal.get(Calendar.DAY_OF_MONTH), xmlCal.getDay());
    assertEquals(cal.get(Calendar.HOUR_OF_DAY), xmlCal.getHour());
    assertEquals(cal.get(Calendar.MINUTE), xmlCal.getMinute());
    assertEquals(cal.get(Calendar.SECOND), xmlCal.getSecond());
    assertEquals(cal.getTime(), xmlCal.toGregorianCalendar().getTime());
  }
}
