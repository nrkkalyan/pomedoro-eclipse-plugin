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
package rabbit.ui;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;

/**
 * Represents a page for displaying graphical information.
 */
public interface IPage {

  /**
   * Creates the content of this page.
   * @param parent The parent composite.
   */
  void createContents(Composite parent);

  /**
   * Creates the tool bar items of this page.
   * @return All tool bat items that have been created for this page, including
   *         the separators.
   */
  IContributionItem[] createToolBarItems(IToolBarManager toolBar);

  /**
   * Restores the state of this page.
   * @param memento the memento containing the state.
   */
  void onRestoreState(IMemento memento);

  /**
   * Saves the state of this page.
   * @param memento the memento for saving the state.
   */
  void onSaveState(IMemento memento);

  /**
   * Creates a job which will be ran to update the page. This page will only
   * return the job, not run it.
   * @param preference The new preferences.
   * @return A job to update the page, or null if this page does not need to be
   *         updated.
   */
  Job updateJob(Preference preference);
}
