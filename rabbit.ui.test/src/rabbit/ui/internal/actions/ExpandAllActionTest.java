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
package rabbit.ui.internal.actions;

import rabbit.ui.internal.AbstractTreeContentProvider;
import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.actions.ExpandAllAction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @see ExpandAllAction
 */
public class ExpandAllActionTest {

  private static final Shell shell;
  
  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }
  
  private final TreeViewer viewer;

  private final IAction action;

  static {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
  }

  public ExpandAllActionTest() {
    viewer = new TreeViewer(shell);
    action = new ExpandAllAction(viewer);
  }

  @Before
  public void before() {
    viewer.getTree().removeAll();
  }

  @Test
  public void testImageDescriptor() {
    assertSame(SharedImages.EXPAND_ALL, action.getImageDescriptor());
  }

  @Test
  public void testRun() {
    final String[] input = new String[] { "1", "2", "3" };
    
    viewer.setContentProvider(new AbstractTreeContentProvider() {

      private int counter = 0;

      @Override
      public Object[] getChildren(Object parentElement) {
        if (counter++ < input.length)
          return new Object[] { "a" };
        else
          return new Object[0];
      }

      @Override
      public Object[] getElements(Object inputElement) {
        return (Object[]) inputElement;
      }

      @Override
      public boolean hasChildren(Object element) {
        return true;
      }
    });
    viewer.setInput(input);
    viewer.collapseAll();
    assertEquals(0, viewer.getExpandedElements().length);

    action.run();
    assertEquals(input.length, viewer.getExpandedElements().length);
  }

  @Test
  public void testStyle() {
    assertSame(IAction.AS_PUSH_BUTTON, action.getStyle());
  }

  @Test
  public void testText() {
    assertEquals("Expand All", action.getText());
  }
}
