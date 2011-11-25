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
package rabbit.ui.internal.pref;

import rabbit.ui.internal.RabbitUI;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class RabbitPreferencePage extends PreferencePage implements
    IWorkbenchPreferencePage {

  private Spinner daySpinner;

  public RabbitPreferencePage() {
  }

  public RabbitPreferencePage(String title) {
    super(title);
  }

  public RabbitPreferencePage(String title, ImageDescriptor image) {
    super(title, image);
  }

  @Override
  public void init(IWorkbench workbench) {
  }

  @Override
  public boolean performOk() {
    if (RabbitUI.getDefault().getDefaultDisplayDatePeriod() != daySpinner
        .getSelection()) {
      RabbitUI.getDefault().setDefaultDisplayDatePeriod(
          daySpinner.getSelection());
    }

    return true;
  }

  @Override
  protected Control createContents(Composite parent) {
    GridLayout layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    Composite cmp = new Composite(parent, SWT.NONE);
    cmp.setLayout(layout);
    cmp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    // Contains setting for Rabbit View:
    Group viewGroup = new Group(cmp, SWT.NONE);
    viewGroup.setText("Rabbit View");
    viewGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    viewGroup.setLayout(new GridLayout(3, false));
    {
      new Label(viewGroup, SWT.HORIZONTAL)
          .setText("By default, display data for the last ");
      daySpinner = new Spinner(viewGroup, SWT.BORDER);
      daySpinner.setMinimum(0);
      daySpinner.setMaximum(9999);
      daySpinner.setSelection(RabbitUI.getDefault()
          .getDefaultDisplayDatePeriod());
      daySpinner.setToolTipText("0 to display today's data only");
      new Label(viewGroup, SWT.HORIZONTAL).setText(" days.");
    }

    return cmp;
  }

  @Override
  protected void performDefaults() {
    daySpinner.setSelection(7);
    super.performDefaults();
  }
}
