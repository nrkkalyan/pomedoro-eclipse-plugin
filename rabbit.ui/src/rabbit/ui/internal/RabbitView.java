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

import rabbit.tracking.internal.TrackingPlugin;
import rabbit.ui.IPage;
import rabbit.ui.Preference;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A view to show metrics.
 */
public class RabbitView extends ViewPart {

  /** Preference constant for saving/restoring the view state. */
  private static final String PREF_RABBIT_VIEW = "rabbitView";

  /** Preference constant for saving/restoring the view state. */
  private static final String PREF_METRICS_WIDTH = "metricsPanelWidth";

  /**
   * Checks whether the two calendars has the same year, month, and day of
   * month.
   * 
   * @param date1 The calendar.
   * @param date2 The other calendar.
   * @return True if the dates has the same year, month, and day of month, false
   *         otherwise.
   */
  public static boolean isSameDate(Calendar date1, Calendar date2) {
    return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
        && date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
        && date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH);
  }

  /**
   * Updates the date with the data from the widget.
   * 
   * @param date The date to be updated.
   * @param widget The widget to get the data from.
   */
  public static void updateDate(Calendar date, DateTime widget) {
    date.set(Calendar.YEAR, widget.getYear());
    date.set(Calendar.MONTH, widget.getMonth());
    date.set(Calendar.DAY_OF_MONTH, widget.getDay());
  }

  /**
   * Updates the widget with the data from the date.
   * 
   * @param widget The widget to be updated.
   * @param date The date to get the data from.
   */
  public static void updateDateTime(DateTime widget, Calendar date) {
    widget.setYear(date.get(Calendar.YEAR));
    widget.setMonth(date.get(Calendar.MONTH));
    widget.setDay(date.get(Calendar.DAY_OF_MONTH));
  }
  
  /**
   * Gets the version of Eclipse. Not completely reliable.
   * 
   * @return The version String, such as 3.5..., or an empty String.
   */
  private static String getProductVersion() {
    try {
      IProduct product = Platform.getProduct();
      String aboutText = product.getProperty("aboutText");
      String pattern = "Version: (.*)\n";
      Pattern p = Pattern.compile(pattern);
      Matcher m = p.matcher(aboutText);
      return (m.find()) ? m.group(1) : "";
    } catch (Exception e) {
      return "";
    }
  }
  
  /**
   * A map containing page status (updated or not), if a page is not updated
   * (value return false), then it will be updated before it's displayed (when a
   * user clicks on a tree node).
   */
  private Map<IPage, Boolean> pageStatus;

  /** A map containing pages and the root composite of the page. */
  private Map<IPage, Composite> pages;

  /** A map containing pages and their tool bar items. */
  private Map<IPage, IContributionItem[]> pageToolItems;
  
  /** A tool bar for pages to create their tool bar items. */
  private IToolBarManager extensionToolBar;
  
  private FormToolkit toolkit;

  /**
   * The layout of {@link #displayPanel}, used to show/hide pages on user 
   * selection.
   */
  private StackLayout stackLayout;

  /** The composite to show the page that is selected by the user. */
  private Composite displayPanel;
  
  /** The preferences for the pages. */
  private final Preference preferences;
  
  /** True if this OS is Windows, false otherwise. */
  private final boolean isWindowsOS = Platform.getOS().equals(Platform.OS_WIN32);

  /** True if this OS is linux, false otherwise. */
  private final boolean isLinux = Platform.getOS().equals(Platform.OS_LINUX);
  
  /** File to save/restore the view state, may be null. */
  private IMemento memento;
  
  /** The form data of the sash dividing the two panels. */
  private FormData sashFormData;
  
  /**
   * Constructs a new view.
   */
  public RabbitView() {
    pages = new HashMap<IPage, Composite>();
    pageStatus = new HashMap<IPage, Boolean>();
    pageToolItems = new HashMap<IPage, IContributionItem[]>();

    toolkit = new FormToolkit(PlatformUI.getWorkbench().getDisplay());
    stackLayout = new StackLayout();
    preferences = new Preference();
  }
  
  @Override
  public void createPartControl(Composite parent) {
    Form form = toolkit.createForm(parent);
    form.getBody().setLayout(new FormLayout());

    sashFormData = new FormData();
    sashFormData.width = 1;
    sashFormData.top = new FormAttachment(0, 0);
    sashFormData.left = new FormAttachment(0, 200);
    sashFormData.bottom = new FormAttachment(100, 0);
    final Sash sash = new Sash(form.getBody(), SWT.VERTICAL);
    sash.setBackground(toolkit.getColors().getBorderColor());
    sash.setLayoutData(sashFormData);
    sash.addListener(SWT.Selection, new Listener() {
      @Override
      public void handleEvent(Event e) {
        ((FormData) sash.getLayoutData()).left = new FormAttachment(0, e.x);
        sash.getParent().layout();
      }
    });

    // Extension list:
    FormData leftData = new FormData();
    leftData.top = new FormAttachment(0, 0);
    leftData.left = new FormAttachment(0, 0);
    leftData.right = new FormAttachment(sash, 0);
    leftData.bottom = new FormAttachment(100, 0);
    Form left = toolkit.createForm(form.getBody());
    left.setText("Metrics");
    left.setLayoutData(leftData);
    left.getBody().setLayout(new FillLayout());
    MetricsPanel list = new MetricsPanel(this);
    list.createContents(left.getBody());

    // Displaying area:
    FormData rightData = new FormData();
    rightData.top = new FormAttachment(0, 0);
    rightData.left = new FormAttachment(sash, 0);
    rightData.right = new FormAttachment(100, 0);
    rightData.bottom = new FormAttachment(100, 0);

    Composite right = toolkit.createComposite(form.getBody());
    right.setLayoutData(rightData);
    GridLayoutFactory.fillDefaults().spacing(0, 0).applyTo(right);

    // Header:
    Composite header = toolkit.createComposite(right);
    GridLayout headerLayout = new GridLayout(2, false);
    if (isLinux) { // Make GTK widgets have less spaces:
      headerLayout.marginHeight = 0;
      headerLayout.marginWidth = 0;
      headerLayout.horizontalSpacing = 0;
      headerLayout.verticalSpacing = 0;
    }
    header.setLayout(headerLayout);
    GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER)
        .grab(true, false).applyTo(header);
    {
      int toolbarStyle = (!isLinux) ? SWT.FLAT : SWT.NONE;
      
      ToolBar bar = new ToolBar(header, toolbarStyle);
      bar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      toolkit.adapt(bar, false, false);
      extensionToolBar = new ToolBarManager(bar);

      
      bar = new ToolBar(header, toolbarStyle);
      toolkit.adapt(bar, false, false);
      createToolBarItems(new ToolBarManager(bar));
    }
    displayPanel = toolkit.createComposite(right);
    displayPanel.setLayout(stackLayout);
    GridDataFactory.fillDefaults().grab(true, true).span(3, 1).applyTo(
        displayPanel);

    // Greeting message:
    Composite cmp = toolkit.createComposite(displayPanel);
    cmp.setLayout(new GridLayout());
    {
      Label imgLabel = toolkit.createLabel(cmp, "", SWT.CENTER);
      imgLabel.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true));
      imgLabel.setImage(getTitleImage());

      Label helloLabel = toolkit.createLabel(cmp, "Welcome!", SWT.CENTER);
      helloLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
    }
    stackLayout.topControl = cmp;
    displayPanel.layout();
    
    if (memento != null) {
      restoreState(memento);
    }
  }
  
  /**
   * Displays the given page.
   * 
   * @param page The page to display.
   */
  public void display(IPage page) {
    // Removes the extension tool bar items:
    for (IContributionItem item : extensionToolBar.getItems()) {
      item.setVisible(false);
    }

    Composite cmp = null;
    if (page != null) {

      // Updates the page:
      cmp = pages.get(page);
      if (cmp == null) {
        cmp = toolkit.createComposite(displayPanel);
        cmp.setLayout(new FillLayout());
        page.createContents(cmp);
        pages.put(page, cmp);
        
        // Restores the state:
        if (memento != null) {
          page.onRestoreState(memento);
        }
      }

      // Updates the extension tool bar items:
      IContributionItem[] items = pageToolItems.get(page);
      if (items == null) {
        items = page.createToolBarItems(extensionToolBar);
        pageToolItems.put(page, items);
      } else {
        for (IContributionItem item : items) {
          item.setVisible(true);
        }
      }

      // Updates the current visible page, mark others as not updated:
      Boolean updated = pageStatus.get(page);
      if (updated == null || !updated) {
        pageStatus.put(page, Boolean.TRUE);
        updatePage(page, preferences);
      }
    }

    extensionToolBar.update(true);
    stackLayout.topControl = cmp;
    displayPanel.layout();
  }
  
  @Override
  public void dispose() {
    toolkit.dispose();
    super.dispose();
  }

  @Override
  public void init(IViewSite site, IMemento m) throws PartInitException {
    super.init(site, m);
    if (m != null) {
      this.memento = m.getChild(PREF_RABBIT_VIEW);
    }
  }

  @Override
  public void saveState(IMemento memento) {
    memento = memento.createChild(PREF_RABBIT_VIEW);
    memento.putInteger(PREF_METRICS_WIDTH, sashFormData.left.offset);
    for (IPage page : pages.keySet()) {
      page.onSaveState(memento);
    }
  }

  @Override
  public void setFocus() {
  }

  private void createSpace(IToolBarManager toolBar) {
    createString(toolBar, "  ");
  }

  private void createString(IToolBarManager toolBar, final String str) {
    toolBar.add(new ControlContribution(null) {
      @Override
      protected Control createControl(Composite parent) {
        return toolkit.createLabel(parent, str);
      }
    });
  }

  /**
   * Creates tool bar items for non windows operating systems.
   * 
   * @param toolBar The tool bar.
   */
  private void createToolBarForNonWindowsOS(IToolBarManager toolBar) {
    CalendarAction.create(toolBar, getSite().getShell(), preferences
        .getStartDate(), " From: ", " ");
    CalendarAction.create(toolBar, getSite().getShell(), preferences
        .getEndDate(), " To: ", " ");
  }

  /**
   * Creates tool bar items for Windows operating system.
   * 
   * @param toolBar The tool bar.
   */
  private void createToolBarForWindowsOS(IToolBarManager toolBar) {
    toolBar.add(new ControlContribution("rabbit.ui.fromDateTime") {
      @Override
      protected Control createControl(Composite parent) {
        final Calendar dateToBind = preferences.getStartDate();
        final DateTime fromDateTime = new DateTime(parent, SWT.DROP_DOWN
            | SWT.BORDER);
        fromDateTime
            .setToolTipText("Select the start date for the data to be displayed");
        updateDateTime(fromDateTime, dateToBind);
        fromDateTime.addListener(SWT.Selection, new Listener() {
          @Override
          public void handleEvent(Event event) {
            updateDate(dateToBind, fromDateTime);
          }
        });
        return fromDateTime;
      }
    });
    createSpace(toolBar);
    toolBar.add(new ControlContribution("rabbit.ui.toDateTime") {
      @Override
      protected Control createControl(Composite parent) {
        final Calendar dateToBind = preferences.getEndDate();
        final DateTime toDateTime = new DateTime(parent, SWT.DROP_DOWN
            | SWT.BORDER);
        toDateTime
            .setToolTipText("Select the end date for the data to be displayed");
        updateDateTime(toDateTime, dateToBind);
        toDateTime.addListener(SWT.Selection, new Listener() {
          @Override
          public void handleEvent(Event event) {
            updateDate(dateToBind, toDateTime);
          }
        });
        return toDateTime;
      }
    });
  }
  
  /**
   * Creates the tool bar items.
   * 
   * @param toolBar The tool bar.
   */
  private void createToolBarItems(IToolBarManager toolBar) {
    // Only Windows && Eclipse 3.5 has SWT.DROP_DOWN for DateTime.
    // We don't support 3.3 and before anyway:
    boolean isDropDownDateTimeSupported = !getProductVersion()
        .startsWith("3.4");

    if (isWindowsOS && isDropDownDateTimeSupported) {
      createToolBarForWindowsOS(toolBar);
    } else {
      createToolBarForNonWindowsOS(toolBar);
    }

    if (isWindowsOS) { // Looks better:
      createSpace(toolBar);
    }
    
    IAction refresh = new Action("Refresh") {
      @Override
      public void run() {
        updateView();
      }
    };

    /*
     * Mainly for Eclipse 3.4 (no SWT.DROP_DOWN DateTime) on Windows. Things
     * look ugly on Windows if some tool bar actions have text and some have
     * icons, so in this case, no icons at all.
     */
    if (!isWindowsOS || isDropDownDateTimeSupported) {
      refresh.setImageDescriptor(SharedImages.REFRESH);
    }

    //
    toolBar.add(refresh);
    toolBar.update(true);
  }

  /**
   * Restores the view state.
   * @param memento The settings.
   */
  private void restoreState(IMemento memento) {
    memento = memento.getChild(PREF_RABBIT_VIEW);
    if (memento == null) {
      return;
    }
    Integer width = memento.getInteger(PREF_METRICS_WIDTH);
    if (width != null && width > 0) {
      sashFormData.left.offset = width;
    }
  }

  private void updatePage(final IPage page, final Preference preference) {
    Job job = page.updateJob(preference);
    if (job == null)
      return;
    
    IWorkbenchSiteProgressService service = (IWorkbenchSiteProgressService) 
        getSite().getService(IWorkbenchSiteProgressService.class);
    
    service.schedule(job);
  }
  
  /**
   * Updates the pages to current preference.
   */
  private void updateView() {
    // Sync with today's data:
    Calendar today = Calendar.getInstance();
    if (isSameDate(today, preferences.getEndDate())
        || today.before(preferences.getEndDate())) {
      TrackingPlugin.getDefault().saveCurrentData();
    }

    // Mark all invisible pages as "not yet updated":
    for (Map.Entry<IPage, Composite> entry : pages.entrySet()) {
      boolean isVisible = stackLayout.topControl == entry.getValue();
      if (isVisible) {
        // update current visible page.
        updatePage(entry.getKey(), preferences);
      }
      pageStatus.put(entry.getKey(), Boolean.valueOf(isVisible));
    }
  }
}
