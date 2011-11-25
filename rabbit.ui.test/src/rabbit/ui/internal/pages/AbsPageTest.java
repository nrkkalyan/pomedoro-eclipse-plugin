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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static java.util.Arrays.asList;

/**
 * @see AbsPage
 */
public abstract class AbsPageTest {

  private static Shell shell;

  @BeforeClass
  public static void setupBeforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
  }

  @AfterClass
  public static void tearDownAfterClass() {
    shell.dispose();
  }

  private AbsPage page;
  private Category[] supportedCategories;

  @Before
  public void setup() {
    page = create();
    page.createContents(shell);
    supportedCategories = getSupportedCategories();
  }

  @Test
  public void shouldSaveAndRestoreTheColumnWidths() {
    Tree tree = page.getFilteredTree().getViewer().getTree();

    int width1 = 123;
    int width2 = 321;
    TreeColumn column1 = new TreeColumn(tree, SWT.NONE);
    TreeColumn column2 = new TreeColumn(tree, SWT.NONE);
    column1.setWidth(width1);
    column2.setWidth(width2);

    IMemento memento = XMLMemento.createWriteRoot("Testing");
    page.onSaveState(memento);

    column1.setWidth(column1.getWidth() * 2);
    page.onRestoreState(memento);

    assertThat(column1.getWidth(), equalTo(width1));
    assertThat(column2.getWidth(), equalTo(width2));
  }

  @Test
  public void shouldSaveAndRestoreTheVisualCategory() {
    page.setVisualCategory(supportedCategories[0]);

    IMemento memento = XMLMemento.createWriteRoot("Testing");
    page.onSaveState(memento);
    page.setVisualCategory(supportedCategories[1]);
    page.onRestoreState(memento);

    assertThat(page.getVisualCategory(), equalTo(supportedCategories[0]));
  }

  @Test
  public void shouldSaveAndRetoreTheSelectedCategories() {
    page.setSelectedCategories(asList(supportedCategories[0]));

    IMemento memento = XMLMemento.createWriteRoot("Testing");
    page.onSaveState(memento);
    page.setSelectedCategories(asList(supportedCategories[1]));
    page.onRestoreState(memento);

    assertThat(page.getSelectedCategories(),
        equalTo(new Category[]{supportedCategories[0]}));
  }

  protected abstract AbsPage create();

  protected abstract Category[] getSupportedCategories();
}
