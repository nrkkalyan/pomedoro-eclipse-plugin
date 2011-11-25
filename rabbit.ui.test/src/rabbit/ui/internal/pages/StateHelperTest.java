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

import static rabbit.ui.internal.pages.Category.DATE;
import static rabbit.ui.internal.pages.Category.FILE;

import rabbit.ui.IProvider;
import rabbit.ui.internal.util.CategoryProvider;
import rabbit.ui.internal.util.ICategorizer;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.util.IConverter;
import rabbit.ui.internal.util.TreePathValueProvider;

import com.google.common.collect.Lists;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static java.util.Arrays.asList;

import java.util.List;

/**
 * Tests for {@link StateHelper}.
 */
public class StateHelperTest {

  private StateHelper helper;
  private Shell shell;
  private Tree tree;

  @Before
  public void create() throws Exception {
    helper = StateHelper.of(XMLMemento.createWriteRoot("Testing"), "MyId");
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    tree = new Tree(shell, SWT.NONE);
  }

  @After
  public void dispose() {
    shell.dispose();
  }

  @Test
  public void restoreCategoriesShouldDoNothingIfNoCategoriesHaveBeenSaved() {
    ICategory[] categories = {DATE, FILE};
    ICategoryProvider provider = new CategoryProvider(categories, categories);
    helper.restoreCategories(provider);
    assertThat(provider.getSelected(), equalTo(asList(categories)));
  }

  @Test(expected = NullPointerException.class)
  public void restoreCategoriesShouldThrowAnExceptionIfProviderIsNull() {
    helper.restoreCategories(null);
  }

  @Test
  public void restoreColumnWidthsShouldDoNothingIfNoWidthsHaveBeenSaved() {
    int width = 123;
    TreeColumn column = new TreeColumn(tree, SWT.NONE);
    column.setWidth(width);

    helper.restoreColumnWidths(new TreeColumn[]{column});
    assertThat(column.getWidth(), equalTo(width));
  }

  @Test(expected = NullPointerException.class)
  public void restoreColumnWidthsShouldThrowAnExceptionIfArgumentIsNull() {
    helper.restoreColumnWidths(null);
  }

  @Test(expected = NullPointerException.class)
  public void restoreColumnWidthsShouldThrowAnExceptionIfColumnsContainNull() {
    helper.restoreColumnWidths(new TreeColumn[]{null});
  }

  @Test
  public void restoreVisualCategoryShouldDoNothingIsNoCategoryHasBeenSaved() {
    ICategorizer categorizer = mock(ICategorizer.class);
    given(categorizer.hasCategory(Mockito.<ICategory> any())).willReturn(true);
    @SuppressWarnings("unchecked")
    TreePathValueProvider provider = new TreePathValueProvider(categorizer,
        mock(IProvider.class), mock(IConverter.class));

    provider.setVisualCategory(FILE);
    helper.restoreVisualCategory(provider);

    assertThat(provider.getVisualCategory(), equalTo((ICategory) FILE));
  }

  @Test(expected = NullPointerException.class)
  public void restoreVisualCategoryShouldThrowAnExceptionIfArgumentIsNull() {
    helper.restoreVisualCategory(null);
  }

  @Test
  public void retrieveSavedCategoriesShouldReturnTheSavedCategories() {
    Category[] array = {DATE, FILE};
    helper.saveCategories(array);
    assertThat(helper.retrieveSavedCategories(), equalTo(asList(array)));
  }

  @Test
  public void retrieveSavedCategoryShouldReturnNullIfNoneHasBeenSave() {
    assertThat(helper.retrieveSavedCategories(), nullValue());
  }

  @Test
  public void retrieveSavedVisualCategoryShouldReturnNullIfNoneHasBeenSaved() {
    assertThat(helper.retrieveSavedVisualCategory(), nullValue());
  }

  @Test
  public void retrieveSavedVisualCategoryShouldReturnTheSavedCategory() {
    helper.saveVisualCategory(DATE);
    assertThat(helper.retrieveSavedVisualCategory(), equalTo(DATE));
  }

  @Test(expected = NullPointerException.class)
  public void saveCategoriesShouldThrowAnExceptionIfCategoriesContainNull() {
    helper.saveCategories(new Category[]{null});
  }

  @Test(expected = NullPointerException.class)
  public void saveColumnWidthsShouldThrowAnExceptionIfArgumentIsNull() {
    helper.saveColumnWidths(null);
  }

  @Test(expected = NullPointerException.class)
  public void saveColumnWidthsShouldThrowAnExceptionIfColumnsContainNull() {
    helper.saveColumnWidths(new TreeColumn[]{null});
  }

  @Test(expected = NullPointerException.class)
  public void saveVisualCategoryShouldThrowAnExceptionIfArgumentIsNull() {
    helper.saveVisualCategory(null);
  }

  @Test
  public void shouldBeAbleToPersistAndRestoreMultipleStates() {
    // For saving the visual category:
    Category visualCategory = FILE;
    ICategorizer categorizer = mock(ICategorizer.class);
    given(categorizer.hasCategory(Mockito.<ICategory> any())).willReturn(true);
    @SuppressWarnings("unchecked")
    TreePathValueProvider valueProvider = new TreePathValueProvider(
        categorizer, mock(IProvider.class), mock(IConverter.class),
        visualCategory);

    // For saving the column widths:
    int width = 101;
    TreeColumn column = new TreeColumn(tree, SWT.NONE);
    column.setWidth(width);

    // For saving the selected categories:
    final List<Category> expectedCategories = asList(DATE, FILE);
    final List<Category> actualCategories = Lists.newArrayList();
    ICategoryProvider categoryProvider = mock(ICategoryProvider.class);
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        for (Object obj : invocation.getArguments()) {
          actualCategories.add((Category) obj);
        }
        return null;
      }
    }).when(categoryProvider)
        .setSelected(expectedCategories.toArray(new Category[0]));

    // Save the states:
    helper
        .saveCategories(expectedCategories.toArray(new Category[0]))
        .saveVisualCategory(visualCategory)
        .saveColumnWidths(new TreeColumn[]{column});

    // Change the states:
    categoryProvider.setSelected(FILE);
    valueProvider.setVisualCategory(DATE);
    column.setWidth(1);

    // Restore:
    helper
        .restoreCategories(categoryProvider)
        .restoreVisualCategory(valueProvider)
        .restoreColumnWidths(new TreeColumn[]{column});

    assertThat(actualCategories, equalTo(expectedCategories));
    assertThat(column.getWidth(), equalTo(width));
    assertThat(valueProvider.getVisualCategory(),
        equalTo((ICategory) visualCategory));
  }

  @Test
  public void shouldPersistTheCategories() {
    final List<Category> expected = asList(DATE, FILE);
    final List<Category> actual = Lists.newArrayList();

    ICategoryProvider provider = mock(ICategoryProvider.class);
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        for (Object obj : invocation.getArguments()) {
          actual.add((Category) obj);
        }
        return null;
      }
    }).when(provider).setSelected(expected.toArray(new Category[0]));

    helper.saveCategories(expected.toArray(new Category[0]));
    helper.restoreCategories(provider);

    assertThat(actual, equalTo(expected));
  }

  @Test
  public void shouldPersistTheColumnWidths() {
    int width1 = 101;
    int width2 = 20;

    TreeColumn column1 = new TreeColumn(tree, SWT.NONE);
    column1.setWidth(width1);
    TreeColumn column2 = new TreeColumn(tree, SWT.NONE);
    column2.setWidth(width2);

    helper.saveColumnWidths(new TreeColumn[]{column1, column2});
    column1.setWidth(1);
    column2.setWidth(2);
    helper.restoreColumnWidths(new TreeColumn[]{column1, column2});

    assertThat(column1.getWidth(), equalTo(width1));
    assertThat(column2.getWidth(), equalTo(width2));
  }

  @Test
  public void shouldPersistTheVisualCategory() {
    ICategorizer categorizer = mock(ICategorizer.class);
    given(categorizer.hasCategory(Mockito.<ICategory> any())).willReturn(true);
    @SuppressWarnings("unchecked")
    TreePathValueProvider provider = new TreePathValueProvider(categorizer,
        mock(IProvider.class), mock(IConverter.class), FILE);

    helper.saveVisualCategory(DATE);
    helper.restoreVisualCategory(provider);

    assertThat(provider.getVisualCategory(), equalTo((ICategory) DATE));
  }
}
