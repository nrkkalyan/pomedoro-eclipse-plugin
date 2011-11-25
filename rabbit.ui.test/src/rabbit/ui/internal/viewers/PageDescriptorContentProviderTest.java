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
package rabbit.ui.internal.viewers;

import rabbit.ui.IPage;
import rabbit.ui.Preference;
import rabbit.ui.internal.util.PageDescriptor;
import rabbit.ui.internal.viewers.PageDescriptorContentProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

/**
 * @see PageDescriptorContentProvider
 */
public class PageDescriptorContentProviderTest {
  
  private static PageDescriptorContentProvider contentProvider;
  
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
    contentProvider.dispose();
  }
  
  @BeforeClass
  public static void beforeClass() {
    contentProvider = new PageDescriptorContentProvider();
  }

  @Test
  public void testHashChildren() throws Exception {
    PageDescriptor des = new PageDescriptor("", "", page, "", null, null);
    assertFalse(contentProvider.hasChildren(des));
    
    des.getChildren().add(des);
    assertTrue(contentProvider.hasChildren(des));
    
    assertFalse(contentProvider.hasChildren(new Object()));
  }
  
  @Test
  public void testGetElements() throws Exception {
    PageDescriptor des = new PageDescriptor("", "", page, "", null, null);
    Object[] elements = contentProvider.getElements(Arrays.asList(des));
    assertEquals(1, elements.length);
    assertEquals(des, elements[0]);
  }
  
  @Test
  public void testGetChildren() throws Exception {
    PageDescriptor des = new PageDescriptor("", "", page, "", null, null);
    assertNotNull(contentProvider.getChildren(des));
    assertEquals(0, contentProvider.getChildren(des).length);
    
    des.getChildren().add(des);
    assertEquals(1, contentProvider.getChildren(des).length);
    assertEquals(des, contentProvider.getChildren(des)[0]);
  }
}
