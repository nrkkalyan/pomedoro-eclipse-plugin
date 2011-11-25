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

import rabbit.ui.internal.CalendarAction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotDateTime;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @see CalendarAction
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class CalendarActionTest {

  private static Shell shell;
  @AfterClass
  public static void afterClass() {
    shell.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        shell.dispose();
      }
    });
  }

  @BeforeClass
  public static void beforeClass() {
    final Display dis = PlatformUI.getWorkbench().getDisplay();
    dis.syncExec(new Runnable() {
      @Override
      public void run() {
        shell = new Shell(dis);
      }
    });
  }

  private ToolBarManager manager;

  @Before
  public void before() {
    manager = new ToolBarManager(SWT.NONE);
    shell.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        manager.createControl(shell);
      }
    });
  }

  @Test
  public void testDateTimeWidget() {
    Format format = new SimpleDateFormat(CalendarAction.DATE_FORMAT);
    Calendar date = new GregorianCalendar(2100, Calendar.JANUARY, 1);
    CalendarAction action = CalendarAction.create(manager, shell, date, null,
        null);

    SWTBotDateTime bot = new SWTBotDateTime(action.getDateTime());
    assertEquals(format.format(date.getTime()), format.format(bot.getDate()));

    date = new GregorianCalendar(2012, Calendar.DECEMBER, 21);
    bot.setDate(date.getTime());
    assertEquals(format.format(date.getTime()), action.getText());

    Calendar cal = action.getCalendar();
    assertEquals(format.format(date.getTime()), format.format(cal.getTime()));
  }

  @Test
  public void testFormat() {
    Calendar date = Calendar.getInstance();
    Format format = new SimpleDateFormat(CalendarAction.DATE_FORMAT);
    String text = format.format(date.getTime());
    IAction action = CalendarAction.create(manager, shell, date, null, null);
    assertEquals(text, action.getText());
  }

  @Test
  public void testPrefixSuffix() {
    String prefix = "abc";
    String suffix = "xyz";
    IAction action = CalendarAction.create(manager, shell, Calendar
        .getInstance(), prefix, suffix);
    assertTrue(action.getText().startsWith(prefix));
    assertTrue(action.getText().endsWith(suffix));
  }

  @Test
  public void testTodayLink() {
    Calendar today = Calendar.getInstance();
    Calendar notToday = (Calendar) today.clone();
    notToday.add(Calendar.MONTH, 1);
    final CalendarAction action = CalendarAction.create(manager, shell,
        notToday, null, null);

    shell.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        action.getShell().open();
      }
    });

    SWTBotShell bot = new SWTBotShell(action.getShell());
    bot.bot().link().click();

    Format format = new SimpleDateFormat(CalendarAction.DATE_FORMAT);
    assertEquals(format.format(today.getTime()), action.getText());

    Calendar cal = action.getCalendar();
    assertEquals(format.format(today.getTime()), format.format(cal.getTime()));
  }
}
