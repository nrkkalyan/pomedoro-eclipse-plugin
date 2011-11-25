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
package rabbit.ui.internal;

import rabbit.ui.Preference;
import rabbit.ui.IPage;
import rabbit.ui.internal.RabbitUI;
import rabbit.ui.internal.RabbitView;
import rabbit.ui.internal.util.PageDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * @see RabbitView
 */
public class RabbitViewTest {

  private static Shell shell;

  @BeforeClass
  public static void setUpBeforeClass() {
    Display.getDefault().syncExec(new Runnable() {
      @Override
      public void run() {
        shell = new Shell(PlatformUI.getWorkbench().getDisplay());
      }
    });
  }

  @AfterClass
  public static void tearDownAfterClass() {
    shell.dispose();
  }

  @Test
  public void testDispose() throws Exception {
    RabbitView view = new RabbitView();
    view.createPartControl(shell);
    view.dispose();

    Field toolkit = RabbitView.class.getDeclaredField("toolkit");
    toolkit.setAccessible(true);
    FormToolkit theKit = (FormToolkit) toolkit.get(view);
    Field isDisposed = FormToolkit.class.getDeclaredField("isDisposed");
    isDisposed.setAccessible(true);
    assertTrue((Boolean) isDisposed.get(theKit));
  }

  @Test
  public void testIsSameDate() throws Exception {
    Method isSameDate = RabbitView.class.getDeclaredMethod("isSameDate",
        Calendar.class, Calendar.class);
    isSameDate.setAccessible(true);

    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = (Calendar) cal1.clone();
    assertTrue((Boolean) isSameDate.invoke(null, cal1, cal2));

    cal2.add(Calendar.SECOND, 1);
    assertTrue((Boolean) isSameDate.invoke(null, cal1, cal2));

    cal2.add(Calendar.DAY_OF_MONTH, 1);
    assertFalse((Boolean) isSameDate.invoke(null, cal1, cal2));
  }

  @Test
  public void testUpdate_checkDates() throws Exception {
    RabbitView view = new RabbitView();
    view.createPartControl(shell);

    Preference pref = getPreference(view);

    Calendar fromDate = new GregorianCalendar(1999, 1, 1);
    pref.getStartDate().setTimeInMillis(fromDate.getTimeInMillis());

    Calendar toDate = new GregorianCalendar(2010, 1, 1);
    pref.getEndDate().setTimeInMillis(toDate.getTimeInMillis());

    update(view);

    assertEquals(fromDate.get(Calendar.YEAR), pref.getStartDate().get(
        Calendar.YEAR));
    assertEquals(fromDate.get(Calendar.MONTH), pref.getStartDate().get(
        Calendar.MONTH));
    assertEquals(fromDate.get(Calendar.DAY_OF_MONTH), pref.getStartDate().get(
        Calendar.DAY_OF_MONTH));

    assertEquals(toDate.get(Calendar.YEAR), pref.getEndDate()
        .get(Calendar.YEAR));
    assertEquals(toDate.get(Calendar.MONTH), pref.getEndDate().get(
        Calendar.MONTH));
    assertEquals(toDate.get(Calendar.DAY_OF_MONTH), pref.getEndDate().get(
        Calendar.DAY_OF_MONTH));
  }

  @Test
  public void testUpdate_checkPageStatus() throws Exception {
    RabbitView view = new RabbitView();
    view.createPartControl(shell);

    IPage visiblePage = null;
    for (PageDescriptor des : RabbitUI.getDefault().loadRootPages()) {
      visiblePage = des.getPage();
      display(view, des.getPage());
    }
    // All pages have been displayed before, so they should all be updated:
    Map<IPage, Boolean> status = getPageStatus(view);
    for (boolean isPageUpdated : status.values()) {
      assertTrue(isPageUpdated);
    }

    update(view);
    // Now only the current visible page is updated:
    for (Map.Entry<IPage, Boolean> entry : status.entrySet()) {
      if (entry.getKey() == visiblePage) {
        assertTrue(entry.getValue());
      } else {
        assertFalse(entry.getValue());
      }
    }

  }

  @Test
  public void testUpdateDate() {
    Calendar date = Calendar.getInstance();
    DateTime widget = new DateTime(shell, SWT.NONE);
    widget.setYear(1901);
    widget.setMonth(3);
    widget.setDay(9);
    RabbitView.updateDate(date, widget);
    assertEquals(widget.getYear(), date.get(Calendar.YEAR));
    assertEquals(widget.getMonth(), date.get(Calendar.MONTH));
    assertEquals(widget.getDay(), date.get(Calendar.DAY_OF_MONTH));
  }

  @Test
  public void testUpdateDateTime() {
    Calendar date = Calendar.getInstance();
    date.set(1999, 2, 3);
    DateTime widget = new DateTime(shell, SWT.NONE);
    RabbitView.updateDateTime(widget, date);
    assertEquals(date.get(Calendar.YEAR), widget.getYear());
    assertEquals(date.get(Calendar.MONTH), widget.getMonth());
    assertEquals(date.get(Calendar.DAY_OF_MONTH), widget.getDay());
  }

  private void display(RabbitView view, IPage page) throws Exception {
    Method display = RabbitView.class.getDeclaredMethod("display", IPage.class);
    display.setAccessible(true);
    display.invoke(view, page);
  }

  @SuppressWarnings("unchecked")
  private Map<IPage, Boolean> getPageStatus(RabbitView view) throws Exception {
    Field pageStatus = RabbitView.class.getDeclaredField("pageStatus");
    pageStatus.setAccessible(true);
    return (Map<IPage, Boolean>) pageStatus.get(view);
  }

  private Preference getPreference(RabbitView view) throws Exception {
    Field pref = RabbitView.class.getDeclaredField("preferences");
    pref.setAccessible(true);
    return (Preference) pref.get(view);
  }

  private void update(RabbitView view) throws Exception {
    Method update = RabbitView.class.getDeclaredMethod("updateView");
    update.setAccessible(true);
    update.invoke(view);
  }
}
