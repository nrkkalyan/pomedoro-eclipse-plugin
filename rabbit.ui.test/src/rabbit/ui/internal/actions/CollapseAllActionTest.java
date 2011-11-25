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
import rabbit.ui.internal.actions.CollapseAllAction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

/**
 * @see CollapseAllAction
 */
public class CollapseAllActionTest {

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

  public CollapseAllActionTest() {
    viewer = new TreeViewer(shell);
    action = new CollapseAllAction(viewer);
  }

  @Before
  public void before() {
    viewer.getTree().removeAll();
  }

  @Test
  public void testImageDescriptor() {
    assertNotNull(action.getImageDescriptor());
  }

  @Test
  public void testRun() {
    viewer.setContentProvider(new AbstractTreeContentProvider() {

      private int counter = 0;

      @Override
      public Object[] getChildren(Object parentElement) {
        if (counter++ == 0)
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

    viewer.setInput(new String[] { "1", "2", "3" });
    viewer.expandAll();
    assertFalse(viewer.getExpandedElements().length == 0);

    action.run();
    assertTrue(viewer.getExpandedElements().length == 0);
  }

  @Test
  public void testStyle() {
    assertSame(IAction.AS_PUSH_BUTTON, action.getStyle());
  }

  @Test
  public void testText() {
    assertEquals("Collapse All", action.getText());
  }
}
