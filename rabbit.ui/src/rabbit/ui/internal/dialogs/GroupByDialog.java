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
package rabbit.ui.internal.dialogs;

import rabbit.ui.internal.util.ICategory;

import com.google.common.collect.Lists;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

// TODO test
/**
 * Dialog to allow user to structure how they want the data to be structured and
 * displayed.
 */
public class GroupByDialog extends FormDialog {

  /** Table viewer containing list of available categories. */
  private TableViewer availableCategoriesViewer;

  /** Tree viewer containing list of selected categories. */
  private TreeViewer selectedCategoriesViewer;
  
  private Button removeButton;
  private Button addButton;
  private Button downButton;
  private Button upButton;
  
  /** List of available categories. */
  private List<ICategory> availableCategories;

  /** List of selected categories. */
  private List<ICategory> selectedCategories;

  /**
   * Content provider for {@link #availableCategoriesViewer}, always return the
   * elements of {@link #availableCategories}.
   */
  private IStructuredContentProvider availableCatContentProvider = new IStructuredContentProvider() {
    @Override
    public void dispose() {
    }

    @Override
    public Object[] getElements(Object inputElement) {
      return availableCategories.toArray();
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
  };

  /**
   * Content provider for {@link #selectedCategoriesViewer}, always return the
   * elements of {@link #selectedCategories}.
   */
  private ITreeContentProvider selectedCatContentProvider = new ITreeContentProvider() {
    @Override
    public void dispose() {
    }

    @Override
    public Object[] getChildren(Object element) {
      int index = selectedCategories.indexOf(element);
      if (index < 0 || index >= selectedCategories.size() - 1) {
        return new Object[0];
      }
      return new Object[] { selectedCategories.get(index + 1) };
    }

    @Override
    public Object[] getElements(Object inputElement) {
      return (selectedCategories.isEmpty()) ? new Object[0]
          : new Object[] { selectedCategories.get(0) };
    }

    @Override
    public Object getParent(Object element) {
      return null;
    }

    @Override
    public boolean hasChildren(Object element) {
      int index = selectedCategories.indexOf(element);
      return (index > -1) && (index < selectedCategories.size() - 1);
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
  };
  
  @Override
  public int open() {
    refreshViewers();
    return super.open();
  }

  /**
   * Constructor.
   * 
   * @param shell The parent shell.
   */
  public GroupByDialog(Shell shell) {
    super(shell);
    availableCategories = Lists.newLinkedList();
    selectedCategories = Lists.newLinkedList();
  }

  /**
   * Gets the user selected categories. This method should be call after this
   * dialog is closed.
   * 
   * @return The user selected categories.
   */
  public ICategory[] getSelectedCategories() {
    return selectedCategories.toArray(new ICategory[selectedCategories.size()]);
  }

  /**
   * Sets the elements to be displayed as available (not currently enabled).
   * This method should be called before this dialog is made visible.
   * 
   * @param categories The elements.
   */
  public void setUnSelectedCategories(Collection<ICategory> categories) {
    availableCategories.clear();
    availableCategories.addAll(categories);
  }

  /**
   * Sets the elements to be displayed as selected (currently enabled). This
   * method should be called before this dialog is made visible.
   * 
   * @param categories The elements.
   */
  public void setSelectedCategories(List<ICategory> categories) {
    selectedCategories.clear();
    selectedCategories.addAll(categories);
  }

  @Override
  protected void createFormContent(IManagedForm mform) {
    setHelpAvailable(false);
    getShell().setText("Grouping");

    FormToolkit toolkit = mform.getToolkit();
    Form form = mform.getForm().getForm();
    form.setMessage("Specify how the elements should be structured",
        IMessageProvider.INFORMATION);
    toolkit.decorateFormHeading(form);

    form.getBody().setLayout(new GridLayout());
    Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
    section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    section.setText("Groups");

    Composite parent = toolkit.createComposite(section);
    parent.setLayout(new GridLayout(3, false));
    section.setClient(parent);

    // The labels at the top:
    toolkit.createLabel(parent, "Available Groups:");
    toolkit.createLabel(parent, "");
    toolkit.createLabel(parent, "Selected Groups:");

    createAvailableCategoriesViewer(parent);

    Composite buttonsComposite = toolkit.createComposite(parent);
    createButtons(buttonsComposite, toolkit);

    createSelectedCategoriesViewer(parent);
  }

  /**
   * Creates the table viewer showing the list of available categories.
   */
  private void createAvailableCategoriesViewer(Composite parent) {
    availableCategoriesViewer = new TableViewer(parent, SWT.BORDER | SWT.MULTI);
    availableCategoriesViewer.setContentProvider(availableCatContentProvider);
    availableCategoriesViewer.setLabelProvider(new CategoryLabelProvider());
    availableCategoriesViewer.getTable().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    // Enable/disable buttons when selection changes:
    availableCategoriesViewer
        .addSelectionChangedListener(new ISelectionChangedListener() {

          @Override
          public void selectionChanged(SelectionChangedEvent event) {
            addButton.setEnabled(!event.getSelection().isEmpty());
          }
        });

    // Adds element to selected viewer when double clicked:
    availableCategoriesViewer
        .addDoubleClickListener(new IDoubleClickListener() {
          @Override
          public void doubleClick(DoubleClickEvent event) {
            if (!event.getSelection().isEmpty()) {
              handleAddEvent();
            }
          }
        });

    // Sets the input only once, call refresh when changes made to the list:
    availableCategoriesViewer.setInput(availableCategories);
  }

  /**
   * Creates the buttons for selecting/moving elements between the viewers.
   */
  private void createButtons(Composite parent, FormToolkit toolkit) {
    RowLayout layout = new RowLayout(SWT.VERTICAL);
    layout.fill = true;
    parent.setLayout(layout);

    SelectionListener listener = new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        Object source = e.getSource();
        if (source == addButton)
          handleAddEvent();
        else if (source == removeButton)
          handleRemoveEvent();
        else if (source == upButton)
          handleMoveUpEvent();
        else if (source == downButton)
          handleMoveDownEvent();
      }
    };

    addButton = toolkit.createButton(parent, "-->", SWT.PUSH);
    addButton.addSelectionListener(listener);
    addButton.setEnabled(false);

    removeButton = toolkit.createButton(parent, "<--", SWT.PUSH);
    removeButton.addSelectionListener(listener);
    removeButton.setEnabled(false);

    upButton = toolkit.createButton(parent, "Up", SWT.PUSH);
    upButton.addSelectionListener(listener);
    upButton.setEnabled(false);

    downButton = toolkit.createButton(parent, "Down", SWT.PUSH);
    downButton.addSelectionListener(listener);
    downButton.setEnabled(false);
  }

  /**
   * Creates the tree viewer for show the selection categories.
   */
  private void createSelectedCategoriesViewer(Composite parent) {
    selectedCategoriesViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI);
    selectedCategoriesViewer.setContentProvider(selectedCatContentProvider);
    selectedCategoriesViewer.setLabelProvider(new CategoryLabelProvider());
    selectedCategoriesViewer.getTree().setLayoutData(
        new GridData(SWT.FILL, SWT.FILL, true, true));

    // Enable/disable buttons when selection changes:
    selectedCategoriesViewer
        .addSelectionChangedListener(new ISelectionChangedListener() {

          @Override
          public void selectionChanged(SelectionChangedEvent event) {
            ISelection selection = event.getSelection();
            removeButton.setEnabled(!selection.isEmpty());
            if (selection.isEmpty()) {
              upButton.setEnabled(false);
              downButton.setEnabled(false);
            } else {

              // Disables move up button if selection index == 0:
              IStructuredSelection structured = (IStructuredSelection) selection;
              Object element = structured.getFirstElement();
              upButton.setEnabled(selectedCategories.indexOf(element) > 0);

              // Disables move down button last element is selection:
              Object[] array = structured.toArray();
              element = array[array.length - 1];
              int index = selectedCategories.indexOf(element);
              if (index == selectedCategories.size() - 1) {
                downButton.setEnabled(false);
              } else {
                downButton.setEnabled(true);
              }
            }
          }
        });

    // Double click to remove elements:
    selectedCategoriesViewer.addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(DoubleClickEvent event) {
        if (!event.getSelection().isEmpty()) {
          handleRemoveEvent();
        }
      }
    });

    // Sets the input only once, call refresh when changes made to the list:
    selectedCategoriesViewer.setInput(selectedCategories);
    selectedCategoriesViewer.expandAll();
  }

  /**
   * Adds the selected elements from {@link #availableCategoriesViewer} to
   * {@link #selectedCategoriesViewer}, if any.
   */
  private void handleAddEvent() {
    ISelection selection = availableCategoriesViewer.getSelection();
    if (selection.isEmpty()) {
      return;
    }

    for (Object element : ((IStructuredSelection) selection).toArray()) {
      availableCategories.remove(element);
      selectedCategories.add((ICategory) element);
    }
    refreshViewers();
  }

  /**
   * Moves the selected elements in {@link #selectedCategoriesViewer} down a
   * level, if possible.
   */
  private void handleMoveDownEvent() {
    ISelection selection = selectedCategoriesViewer.getSelection();
    if (selection.isEmpty()) {
      return;
    }

    IStructuredSelection structure = (IStructuredSelection) selection;
    for (Object element : structure.toArray()) {
      int index = selectedCategories.indexOf(element);
      if (index <= selectedCategories.size() - 2) {
        Collections.swap(selectedCategories, index, index + 1);
      }
    }
    refreshViewers();
  }

  /**
   * Moves the selected elements in {@link #selectedCategoriesViewer} up a
   * level, if possible.
   */
  private void handleMoveUpEvent() {
    ISelection selection = selectedCategoriesViewer.getSelection();
    if (selection.isEmpty()) {
      return;
    }

    IStructuredSelection structure = (IStructuredSelection) selection;
    if (selectedCategories.indexOf(structure.getFirstElement()) <= 0) {
      return;
    }

    for (Object element : structure.toArray()) {
      int index = selectedCategories.indexOf(element);
      Collections.swap(selectedCategories, index, index - 1);
    }
    refreshViewers();
  }

  /**
   * Removes the selected elements from {@link #selectedCategoriesViewer} to
   * {@link #availableCategoriesViewer}, if any.
   */
  private void handleRemoveEvent() {
    ISelection selection = selectedCategoriesViewer.getSelection();
    if (selection.isEmpty()) {
      return;
    }

    for (Object element : ((IStructuredSelection) selection).toArray()) {
      selectedCategories.remove(element);
      availableCategories.add((ICategory) element);
    }
    refreshViewers();
  }

  /**
   * Refreshes the viewers when the underlying models change.
   */
  private void refreshViewers() {
    ISelection selection = availableCategoriesViewer.getSelection();
    availableCategoriesViewer.refresh();
    availableCategoriesViewer.setSelection(selection, true);

    selection = selectedCategoriesViewer.getSelection();
    selectedCategoriesViewer.refresh();
    selectedCategoriesViewer.expandAll();
    selectedCategoriesViewer.setSelection(selection, true);
  }
}
