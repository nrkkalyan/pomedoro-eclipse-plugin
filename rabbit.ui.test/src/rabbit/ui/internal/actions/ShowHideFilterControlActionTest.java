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

import rabbit.ui.internal.actions.ShowHideFilterControlAction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @see ShowHideFilterControlAction
 */
public class ShowHideFilterControlActionTest {

  private static Shell shell;
  private static FilteredTree tree;

  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    shell.setLayout(new FillLayout());
    tree = new FilteredTree(shell, SWT.NONE, new PatternFilter(), false);
  }

  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }

  @Test
  public void testRun() {
    shell.open();
    
    ShowHideFilterControlAction action = new ShowHideFilterControlAction(tree);
    assertTrue(tree.getFilterControl().getParent().isVisible());
    assertEquals(tree.getFilterControl().getParent().isVisible(), action
        .isChecked());

    action.run();
    assertFalse(tree.getFilterControl().getParent().isVisible());
    assertEquals(tree.getFilterControl().getParent().isVisible(), action
        .isChecked());
    
    action.run();
    assertTrue(tree.getFilterControl().getParent().isVisible());
    assertTrue(tree.getFilterControl().isFocusControl());
    assertEquals(tree.getFilterControl().getParent().isVisible(), action
        .isChecked());
  }
}
