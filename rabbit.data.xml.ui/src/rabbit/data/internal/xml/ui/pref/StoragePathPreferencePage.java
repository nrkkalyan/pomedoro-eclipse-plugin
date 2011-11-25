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
package rabbit.data.internal.xml.ui.pref;

import rabbit.data.internal.xml.XmlPlugin;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class StoragePathPreferencePage extends PreferencePage implements
    IWorkbenchPreferencePage {

  private Text storageText;

  public StoragePathPreferencePage() {
  }

  public StoragePathPreferencePage(String title) {
    super(title);
  }

  public StoragePathPreferencePage(String title, ImageDescriptor image) {
    super(title, image);
  }

  @Override
  public void init(IWorkbench workbench) {
  }

  @Override
  public boolean performOk() {

    final File oldRoot = XmlPlugin.getDefault().getStoragePathRoot().toFile();
    final File newRoot = new File(storageText.getText());
    
    // Nothing to do if directory is unchanged:
    if (oldRoot.equals(newRoot)) {
      return true;
    }
    
    // Create the new directory and check read/write permissions:
    boolean dirCreated = newRoot.exists();
    if (!dirCreated) {
      dirCreated = newRoot.mkdirs();
    }
    if (!dirCreated || !newRoot.canRead() || !newRoot.canWrite()) {
      MessageDialog.openError(getShell(), "Error", "Error occurred while " +
      		"accessing the new directory, please select another directory.");
      return false;
    }

    String title = "Copy Exsiting Data?";
    String message = "Would you like to copy the existing data "
        + "over to the new storage location for Rabbit?";
    if (MessageDialog.openQuestion(getShell(), title, message)) {
      try {
        
        /*
         * This filter ensures that we don't enter an endless recursion (happens
         * when the source directory or the destination directory is the parent
         * or child of the other) while copying the data. Basically is to copy
         * everything except copying the source and destination folders
         * themselves.
         */
        FileFilter filter = new FileFilter() {
          @Override public boolean accept(File pathname) {
            if (pathname.equals(newRoot) || pathname.equals(oldRoot)) {
              return false;
            } else {
              return true;
            }
          }
        };
        FileUtils.copyDirectory(oldRoot, newRoot, filter);
        
      } catch (IOException e) {
        MessageDialog.openError(getShell(), "Error", 
            "Error occurred while copying data, please select another directory.");
        return false;
      }
    }

    XmlPlugin.getDefault().setStoragePathRoot(newRoot);
    setMessage("Storage location have been successfully changed.");

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

    // Contains settings for storage location:
    Group pathGroup = new Group(cmp, SWT.NONE);
    pathGroup.setText("Location to Store Data");
    pathGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
    pathGroup.setLayout(new GridLayout(3, false));
    {
      Label description = new Label(pathGroup, SWT.WRAP);
      description.setText("Please use a dedicated folder to prevent Rabbit" +
      		" from messing up your files.\nIt's a rabbit after all!");
      GridDataFactory.fillDefaults().span(3, 1).applyTo(description);

      new Label(pathGroup, SWT.NONE).setText("Location:");

      storageText = new Text(pathGroup, SWT.BORDER);
      storageText.setText(XmlPlugin.getDefault().getStoragePathRoot().toOSString());
      storageText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      storageText.addListener(SWT.KeyUp, new Listener() {
        @Override public void handleEvent(Event event) {
          setErrorMessage(null);
        }
      });

      Button browse = new Button(pathGroup, SWT.PUSH);
      browse.setText("    Browse...    ");
      browse.addListener(SWT.Selection, new Listener() {
        @Override public void handleEvent(Event event) {
          DirectoryDialog dialog = new DirectoryDialog(getShell());
          dialog.setMessage("Select a folder for storing data collected by Rabbit.");

          String path = dialog.open();
          if (path != null) {
            storageText.setText(path);
          }
          setErrorMessage(null);
        }
      });
    }
    return cmp;
  }

  @Override
  protected void performDefaults() {
    storageText.setText(XmlPlugin.getDefault().getStoragePathRoot().toOSString());
    super.performDefaults();
  }
}
