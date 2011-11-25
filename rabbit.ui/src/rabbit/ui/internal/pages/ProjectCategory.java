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
package rabbit.ui.internal.pages;

import rabbit.ui.IPage;
import rabbit.ui.Preference;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;

public class ProjectCategory implements IPage {

  public ProjectCategory() {}

  @Override
  public void createContents(Composite parent) {
    parent.setLayout(new GridLayout());
    Label label = new Label(parent, SWT.NONE);
    label.setBackground(parent.getBackground());
    label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
    label.setText("This category contains pages that display information" +
        " relating to projects.");
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    return new IContributionItem[0];
  }

  @Override
  public Job updateJob(Preference preference) {
    return null;
  }

  @Override
  public void onRestoreState(IMemento memento) {}

  @Override
  public void onSaveState(IMemento memento) {}

}
