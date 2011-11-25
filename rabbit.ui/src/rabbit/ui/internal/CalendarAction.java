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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A subclass of {@link Action} that is designed for {@link IToolBarManager}.
 * When an action of this kind is clicked, a pop up dialog containing a
 * {@link DateTime} widget will show up below the clicked tool item.
 */
public class CalendarAction extends Action {

  /** The string format used to format dates. */
  public static final String DATE_FORMAT = "yyyy-MM-dd";

  /**
   * Creates and adds a new action on the tool bar.
   * 
   * @param toolBar The tool bar.
   * @param shell The parent shell.
   * @param calendar The calendar to bind with this action. The changes made on
   *          this action's DateTime widget will be reflected on this calendar.
   * @return The created action.
   */
  public static CalendarAction create(IToolBarManager toolBar, Shell shell,
      Calendar calendar) {
    return create(toolBar, shell, calendar, "  ", "  ");
  }

  /**
   * Creates and adds a new action on the tool bar.
   * 
   * @param toolBar The tool bar.
   * @param shell The parent shell.
   * @param calendar The calendar to bind with this action. The changes made on
   *          this action's DateTime widget will be reflected on this calendar.
   * @param prefix The prefix of the action's text.
   * @param suffix The suffix of the action's text.
   * @return The created action.
   */
  public static CalendarAction create(IToolBarManager toolBar, Shell shell,
      Calendar calendar, String prefix, String suffix) {
    CalendarAction action = new CalendarAction(shell, calendar, prefix, suffix);
    toolBar.add(action);
    return action;
  }

  private final Calendar calendar;
  private final Format format;
  private final String prefix;
  private final String suffix;

  private DateTime dateTime;
  private Shell shell;

  /**
   * Constructor.
   * 
   * @param parentShell The parent shell.
   * @param calendar The calendar to bind with this action.
   * @param prefix The prefix of this action's text.
   * @param suffix The suffix of this action's text.
   */
  private CalendarAction(final Shell parentShell, Calendar calendar,
      String prefix, String suffix) {
    super("", IAction.AS_CHECK_BOX);
    this.calendar = calendar;
    this.prefix = (prefix == null) ? "" : prefix;
    this.suffix = (suffix == null) ? "" : suffix;
    this.format = new SimpleDateFormat(DATE_FORMAT);

    setText(getFormattedText());
    parentShell.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        createCalendar(parentShell);
      }
    });
  }

  /**
   * Gets the calendar binded to this action.
   * 
   * @return The calendar.
   */
  public Calendar getCalendar() {
    return calendar;
  }

  /**
   * Gets the date time widget.
   * 
   * @return The widget.
   */
  public DateTime getDateTime() {
    return dateTime;
  }

  public Shell getShell() {
    return shell;
  }

  @Override
  public void runWithEvent(Event event) {
    if (!shell.isVisible()) {
      open(event);
    } else {
      close();
    }
  }

  /**
   * Closes the pop up calendar.
   */
  private void close() {
    shell.setVisible(false);
    setChecked(false);
  }

  /**
   * Creates the pop up calendar.
   * 
   * @param parentShell The parent shell.
   */
  private void createCalendar(Shell parentShell) {
    GridLayout layout = new GridLayout();
    layout.marginTop = -6; // better looking
    layout.marginWidth = -1;
    shell = new Shell(parentShell, SWT.TOOL);
    shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    shell.setLayout(layout);
    shell.addListener(SWT.Deactivate, new Listener() {
      @Override
      public void handleEvent(Event event) {
        close();
      }
    });

    Calendar cal = getCalendar();
    dateTime = new DateTime(shell, SWT.CALENDAR);
    dateTime.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal
        .get(Calendar.DAY_OF_MONTH));
    dateTime.addListener(SWT.MouseDoubleClick, new Listener() {
      @Override
      public void handleEvent(Event event) {
        ok();
      }
    });
    dateTime.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
        getCalendar().set(Calendar.YEAR, dateTime.getYear());
        getCalendar().set(Calendar.MONTH, dateTime.getMonth());
        getCalendar().set(Calendar.DAY_OF_MONTH, dateTime.getDay());
        setText(getFormattedText());
      }
    });

    Link link = new Link(shell, SWT.NONE);
    link.setText("<A>Today</A>");
    link.setBackground(shell.getBackground());
    link.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
    link.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event event) {
        RabbitView.updateDateTime(dateTime, Calendar.getInstance());
        ok();
      }
    });

    shell.pack();
  }

  /**
   * Formats the default calendar to a string.
   * 
   * @return A formatted string.
   */
  private String getFormattedText() {
    return getFormattedText(calendar);
  }

  /**
   * Formats the given calendar to a string.
   * 
   * @param calendar The calendar.
   * @return A formatted string.
   */
  private String getFormattedText(Calendar calendar) {
    return prefix + format.format(calendar.getTime()) + suffix;
  }

  /**
   * Applies the changes and closes the pop up calendar.
   */
  private void ok() {
    shell.setVisible(false);
    calendar.set(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
    setText(getFormattedText());
    setChecked(false);
  }

  /**
   * Opens the pop up calendar.
   * 
   * @param e The event.
   */
  private void open(Event e) {
    if (!(e.widget instanceof ToolItem)) {
      return;
    }
    RabbitView.updateDateTime(dateTime, calendar);
    ToolItem item = ((ToolItem) e.widget);
    Rectangle bounds = item.getBounds();
    Point location = item.getParent().toDisplay(bounds.x,
        bounds.y + bounds.height);
    shell.setLocation(location);
    shell.setVisible(true);
    shell.setActive();
  }
}