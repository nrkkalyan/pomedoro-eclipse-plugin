/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.ui.internal.viewers;

import rabbit.ui.IPage;
import rabbit.ui.Preference;
import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.util.PageDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @see PageDescriptorLabelProvider
 */
public class PageDescriptorLabelProviderTest {

  private static PageDescriptorLabelProvider labelProvider;
  
  private static final IPage page = new IPage() {
    @Override public void createContents(Composite parent) {}
    @Override public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
      return new IContributionItem[0];
    }
    @Override public Job updateJob(Preference preference) { return null; }
    @Override public void onRestoreState(IMemento memento) {}
    @Override public void onSaveState(IMemento memento) {}
  };
  
  @AfterClass
  public static void afterClass() {
    labelProvider.dispose();
  }
  
  @BeforeClass
  public static void beforeClass() {
    labelProvider = new PageDescriptorLabelProvider();
  }
  
  @Test
  public void testGetImage() throws Exception {
    PageDescriptor des = new PageDescriptor("", "", page, "", null, null);
    assertNull(labelProvider.getImage(des));
    
    // A page with image:
    des = new PageDescriptor("", "", page, "", SharedImages.CALENDAR, null);
    assertNotNull(labelProvider.getImage(des));
  }
  
  @Test
  public void testGetText() throws Exception {
    PageDescriptor des = new PageDescriptor("", "name", page, "", null, null);
    assertEquals(des.getName(), labelProvider.getText(des));
  }
  
  @Test
  public void testGetToolTipText() throws Exception {
    PageDescriptor des = new PageDescriptor("", "", page, "toolTipText", null, null);
    assertEquals(des.getDescription(), labelProvider.getToolTipText(des));
  }
}
