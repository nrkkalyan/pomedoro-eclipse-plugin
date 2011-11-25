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
package rabbit.tracking.internal.trackers;

import rabbit.data.store.model.JavaEvent;
import rabbit.tracking.internal.IdleDetector;
import rabbit.tracking.internal.TrackingPlugin;
import rabbit.tracking.internal.trackers.AbstractTracker;
import rabbit.tracking.internal.trackers.JavaTracker;

import static org.eclipse.jdt.internal.ui.actions.SelectionConverter.getElementAtOffset;
import static org.eclipse.jdt.internal.ui.actions.SelectionConverter.getInput;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.actions.OpenJavaPerspectiveAction;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.IViewDescriptor;
import org.joda.time.Interval;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static java.lang.String.format;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Observable;
import java.util.Random;

/**
 * @see JavaTracker
 */
@SuppressWarnings("restriction")
public class JavaTrackerTest extends AbstractTrackerTest<JavaEvent> {

  private static IJavaProject project;
  private static IPackageFragment pkg;
  private static ICompilationUnit unit;

  @AfterClass
  public static void afterClass() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(
        false);
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    new OpenJavaPerspectiveAction().run();

    // Creates the project:
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IProject proj = root.getProject(System.currentTimeMillis() + "");
    JavaCapabilityConfigurationPage.createProject(proj, (URI) null, null);
    project = JavaCore.create(proj);
    JavaCapabilityConfigurationPage page = new JavaCapabilityConfigurationPage();
    page.init(project, null, null, false);
    page.configureJavaProject(null);

    // Creates the package:
    IPackageFragmentRoot src = project.getAllPackageFragmentRoots()[0];
    pkg = src.createPackageFragment("pkg", true, null);

    // Creates the class:
    String className = "Program";
    StringBuilder builder = new StringBuilder();
    builder.append(format("package %s;%n", pkg.getElementName()));
    builder.append(format("public class %s {%n", className));
    builder.append(format("}%n"));
    String content = builder.toString();
    unit = pkg.createCompilationUnit(className + ".java", content, true, null);
    unit.open(null);

    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(
        false);
  }

  /**
   * Tests when the editor is no longer the active part:
   */
  @Test
  public void testEditorDeactivated() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();

    // Set the editor to select the package declaration:
    int offset = getDocument(editor).get().indexOf(pkg.getElementName());
    int len = pkg.getElementName().length();
    editor.getSelectionProvider().setSelection(new TextSelection(offset, len));

    // Run the tracker to capture the event:
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true); // Start
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    // Sets another view to be active to cause the editor to lose focus:
    editor.getSite().getPage().showView(getRandomView().getId());
    long postEnd = System.currentTimeMillis();

    // One data should be in the collection (the selected package declaration):
    assertEquals(1, tracker.getData().size());
    JavaEvent event = tracker.getData().iterator().next();

    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
    assertEquals(getElementAtOffset(editor), event.getElement());
  }

  /**
   * Tests that events are recorded properly with the different states of the
   * editor.
   */
  @Test
  public void testEditorDeactivatedThenActivated() throws Exception {
    JavaEditor editor = closeAndOpenEditor();

    // Set the editor to select the package declaration:
    int offset = getDocument(editor).get().indexOf(pkg.getElementName());
    int len = pkg.getElementName().length();
    editor.getSelectionProvider().setSelection(new TextSelection(offset, len));

    // Now run the tracker to capture the event:
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    editor.getSite().getPage().showView(getRandomView().getId());
    long postEnd = System.currentTimeMillis();

    // One data should be in the collection (the selected package declaration):
    assertEquals(1, tracker.getData().size());
    JavaEvent event = tracker.getData().iterator().next();

    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
    assertEquals(getElementAtOffset(editor), event.getElement());

    // Now we activate the editor again to see if the tracker will start to
    // track events again:
    tracker.flushData();

    preStart = System.currentTimeMillis();
    editor.getSite().getPage().activate(editor);
    postStart = System.currentTimeMillis();

    Thread.sleep(20);

    preEnd = System.currentTimeMillis();
    editor.getSite().getPage().showView(getRandomView().getId());
    postEnd = System.currentTimeMillis();

    // One data should be in the collection (the selected package declaration):
    assertEquals(1, tracker.getData().size());
    event = tracker.getData().iterator().next();
    start = event.getInterval().getStartMillis();
    end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
    assertEquals(getElementAtOffset(editor), event.getElement());
  }

  /**
   * Test when the user changes from working on a Java element to another.
   */
  @Test
  public void testElementChanged() throws Exception {
    JavaEditor editor = closeAndOpenEditor();

    // Set the editor to select the package declaration:
    IDocument document = getDocument(editor);
    int offset = document.get().indexOf(pkg.getElementName());
    int len = pkg.getElementName().length();
    editor.getSelectionProvider().setSelection(new TextSelection(offset, len));

    // Keeps the reference of the package declaration for testing latter:
    final IJavaElement element = getElementAtOffset(editor);

    // Run the tracker to capture the event:
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    // Change the element the user is working on:
    offset = document.get().indexOf(unit.getTypes()[0].getElementName());
    int line = document.getLineOfOffset(offset);
    StyledText text = editor.getViewer().getTextWidget();
    text.setCaretOffset(document.getLineOffset(line));
    text.insert(" ");

    long preEnd = System.currentTimeMillis();
    text.notifyListeners(SWT.KeyDown, new Event());
    long postEnd = System.currentTimeMillis();

    // One data should be in the collection (the selected package declaration):
    assertEquals(1, tracker.getData().size());

    JavaEvent event = tracker.getData().iterator().next();
    assertEquals(element, event.getElement());
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  /**
   * When the tracker is set to enable, but if there is no active workbench
   * window, no data will be collected.
   */
  @Test
  public void testEnable_noActiveWorkbenchWindow() throws Exception {
    JavaEditor editor = closeAndOpenEditor();

    // Set the editor to select the package declaration:
    int offset = getDocument(editor).get().indexOf(unit.getElementName());
    int len = unit.getElementName().length();
    editor.getSelectionProvider().setSelection(new TextSelection(offset, len));

    // Open a new shell to cause the workbench window to lose focus:
    Shell shell = null;
    try {
      shell = new Shell(Display.getCurrent());
      shell.open();
      shell.forceActive();

      tracker.setEnabled(true);
      Thread.sleep(30);
      tracker.setEnabled(false);
      assertEquals(0, tracker.getData().size());

    } finally {
      if (shell != null) {
        shell.dispose();
      }
    }
  }

  @Test
  public void testEnableThenDisable() throws Exception {
    JavaEditor editor = closeAndOpenEditor();

    // Set the editor to select the package declaration:
    String className = unit.getTypes()[0].getElementName();
    int offset = getDocument(editor).get().indexOf(className);
    int len = className.length();
    editor.getSelectionProvider().setSelection(new TextSelection(offset, len));

    // Run the tracker to capture the event:
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(30);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // One data should be in the collection (the selected package declaration):
    assertEquals(1, tracker.getData().size());
    JavaEvent event = tracker.getData().iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
    assertEquals(getElementAtOffset(editor), event.getElement());
  }

  /**
   * Test an event on an anonymous. This event should be filtered on save, so
   * that instead of showing a user spent x amount of time on the anonymous
   * class, we show that a user spent x amount of time on the anonymous's parent
   * type element (a method or a type that is not anonymous).
   */
  @Test
  public void testFilter_anonymousClass() throws Exception {
    /*
     * Here we test that: a method containing an anonymous Runnable which also
     * contains another anonymous Runnable, and the most inner Runnable is
     * selected (to emulate that the user is working on that), then when filter
     * on save the data should indicate that the user has spent x amount of time
     * working on the method, not any of the Runnable's.
     */

    JavaEditor editor = closeAndOpenEditor();
    IDocument document = getDocument(editor);

    StringBuilder builder = new StringBuilder();
    builder.append("void aMethod() {");
    builder.append("  new Runnable() { ");
    builder.append("    public void run(){");
    builder.append("      new Runnable() {");
    builder.append("        public void run() {}");
    builder.append("      };");
    builder.append("    } ");
    builder.append("  };");
    builder.append("}");

    String content = document.get();
    int offset = content.indexOf("{") + 1;
    int len = 0;
    document.replace(offset, len, builder.toString());

    content = document.get();
    offset = content.indexOf("Runnable", content.indexOf("Runnable") + 1);
    len = "Runnable".length();
    editor.getSelectionProvider().setSelection(new TextSelection(offset, len));

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(30);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    assertEquals(1, tracker.getData().size());

    JavaEvent event = tracker.getData().iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    IJavaElement element = getElementAtOffset(editor);
    // This two are to check we've set the selection right in this test:
    assertEquals(IJavaElement.TYPE, element.getElementType());
    assertTrue(((IType) element).isAnonymous());
    // getParent().getParent().getParent() will give us the method:
    assertEquals(IJavaElement.METHOD, event.getElement().getElementType());
    assertEquals("aMethod", event.getElement().getElementName());
  }

  /**
   * This test is for the same purpose as
   * {@link #testFilter_deletedElement_typeMembers()}, but with a little
   * difference, that is, if the whole Java file is deleted, all the data about
   * the Java elements in the file will be stored under the Java file, even
   * though it's deleted.
   * 
   * @see #testFilter_deletedElement_typeMembers()
   */
  @Test
  public void testFilter_deletedElement_mainType() throws Exception {
    // Create a new class:
    String newClassName = unit.getTypes()[0].getElementName() + "abc";
    StringBuilder builder = new StringBuilder();
    builder.append(format("package %s;%n", pkg.getElementName()));
    builder.append(format("public class %s {", newClassName));
    builder.append(format("}%n"));
    ICompilationUnit myUnit = pkg.createCompilationUnit(newClassName + ".java",
        builder.toString(), true, null);

    JavaEditor editor = closeAndOpenEditor(myUnit);

    // Set the editor to select the package declaration:
    int offset = getDocument(editor).get().indexOf(pkg.getElementName());
    int len = pkg.getElementName().length();
    editor.getSelectionProvider().setSelection(new TextSelection(offset, len));
    // Make sure we got the selection right:
    assertEquals(IJavaElement.PACKAGE_DECLARATION,
        getElementAtOffset(editor).getElementType());

    // Run the tracker to capture the event:
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(35);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Delete the file:
    myUnit.getResource().delete(true, null);

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    // One data should be in the collection (the parent of the previously
    // selected package declaration):
    assertEquals(1, tracker.getData().size());
    JavaEvent event = tracker.getData().iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    // Test the data is placed under the root element:
    assertEquals(myUnit, event.getElement());
  }

  /**
   * Tests that if a Java element, a field for example, is deleted from the Java
   * source file, then when we save the tracker's data, the data about the
   * deleted field should be store as the parent's data.
   * <p>
   * For example: If we have a field called "fieldA" under the class "ClassA"
   * and the tracker has recorded that the user has spent 20 seconds working on
   * the field, but the field is then deleted from the class. So when the
   * tracker saves the data, instead of saving
   * "The user has spent 20 seconds working on fieldA" we store
   * "The user has spent 20 seconds working on classA".
   * </p>
   * <p>
   * Another important purpose of this feature is that: Then a user starts to
   * type a new java element, like a method, he/she knows what the name he/she
   * is going to type for the method, but we have no way of knowing that, so
   * lots of events may be recorded before he/she finishes typing the name. For
   * example, if the user want to type "hello" as the method name, there will be
   * events recorded about the java element "hel", or "hell", or "hello", we
   * only need one of them ("hello") but we also want to keep the time about the
   * invalid ones, so before we save the data, we check for non-existent java
   * elements, and instead of saving the data under those elements, we save the
   * data under the first existing parent of the elements, if all parents are
   * missing (e.g. deletes the file), we save it under the file parent, like
   * "File.java", even though the file has been deleted.
   * </p>
   * 
   * @see #testFilter_deletedElement_mainType()
   */
  @Test
  public void testFilter_deletedElement_typeMembers() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();
    final IDocument document = getDocument(editor);

    // Place a field in the body of the class, note that we don't want to add
    // errors to the class:

    String field = "private int aVeryUniqueFieldName = 0;";
    int offset = document.get().lastIndexOf('}') - 1;
    int len = 0;
    document.replace(offset, len, field);

    // Set the editor to select the field:
    offset = document.get().indexOf(field);
    len = field.length();
    editor.getSelectionProvider().setSelection(new TextSelection(offset, len));

    // Run the tracker to capture the event:
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(25);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Keeps a reference to the field statement first, for testing latter:
    final IJavaElement element = getElementAtOffset(editor);

    // Now delete the field statement from the source file, note that there
    // is no need to save the document (and we should not save the document,
    // other tests may depend on it):
    offset = document.get().indexOf(field);
    len = field.length();
    document.replace(offset, len, "");

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    // Gets the data, the data is remained in the tracker as long as we don't
    // enable it again (according to the contract of the tracker):
    //
    // One data should be in the collection
    // (the parent of the selected package declaration):
    assertEquals(1, tracker.getData().size());
    JavaEvent event = tracker.getData().iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    // Now check that the element is the parent of the package declaration
    // instead of the deleted package declaration itself:
    assertEquals(element.getParent(), event.getElement());
  }

  /**
   * Test an event on an import statement. This event should be filtered on
   * save, so that instead of showing a user spent x amount of time on the
   * import statement , we show that a user spent x amount of time on the type
   * root (ITypeRoot) element, (a.k.a the Java file).
   */
  @Test
  public void testFilter_existingElement_importStatement() throws Exception {
    final JavaEditor editor = closeAndOpenEditor();
    final IDocument document = getDocument(editor);
    String importStatement = "import java.util.*;";
    int offset = document.get().indexOf(";") + 1; // Position after package
    // declaration
    int len = 0;
    document.replace(offset, len, importStatement);

    offset = document.get().indexOf(importStatement);
    len = importStatement.length();
    ITextSelection selection = new TextSelection(offset, len);
    editor.getSelectionProvider().setSelection(selection);

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    assertEquals(1, tracker.getData().size());

    JavaEvent event = tracker.getData().iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
    assertEquals(getInput(editor), event.getElement());
  }

  /**
   * Test an event on a static initialiser. This event should not be filtered on
   * save.
   */
  @Test
  public void testFilter_existingElement_initializer() throws Exception {
    JavaEditor editor = closeAndOpenEditor();
    IDocument document = getDocument(editor);
    String staticName = "static";
    String methodText = staticName + " {}";
    int offset = document.get().indexOf("{") + 1;
    int len = 0;
    document.replace(offset, len, methodText);

    offset = document.get().indexOf(staticName);
    len = staticName.length();
    editor.getSelectionProvider().setSelection(new TextSelection(offset, len));

    IJavaElement element = getElementAtOffset(editor);
    // Check we got the selection right
    assertEquals(IJavaElement.INITIALIZER, element.getElementType());

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    assertEquals(1, tracker.getData().size());
    JavaEvent event = tracker.getData().iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
    assertEquals(element, event.getElement());
  }

  /**
   * Test an event on a method, that is a member of an anonymous class. This
   * event should be filtered so that we so the user has spent x amount of time
   * on the method's first non-anonymous parent.
   */
  @Test
  public void testFilter_existingElement_methodParentIsAnonymous()
      throws Exception {
    JavaEditor editor = closeAndOpenEditor();
    IDocument document = getDocument(editor);

    StringBuilder anonymous = new StringBuilder();
    anonymous.append("void aMethod() {");
    anonymous.append("  new Runnable() { ");
    anonymous.append("    public void run(){");
    anonymous.append("      new Runnable() {");
    anonymous.append("        public void run() {}");
    anonymous.append("      };");
    anonymous.append("    } ");
    anonymous.append("  };");
    anonymous.append("}");

    int offset = document.get().indexOf("{") + 1;
    int len = 0;
    document.replace(offset, len, anonymous.toString());

    String content = document.get();
    offset = content.indexOf("run", content.indexOf("run") + 1);
    len = "run".length();
    editor.getSelectionProvider().setSelection(new TextSelection(offset, len));

    IJavaElement element = getElementAtOffset(editor);
    // Make sure we got the selection right:
    assertEquals("run", element.getElementName());

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    assertEquals(1, tracker.getData().size());
    JavaEvent event = tracker.getData().iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
    assertEquals("aMethod", event.getElement().getElementName());
  }

  /**
   * Test an event on a method, that is a member of a non-anonymous class. This
   * event should not be filtered on save.
   */
  @Test
  public void testFilter_existingElement_methodParentNotAnonymous()
      throws Exception {
    JavaEditor editor = closeAndOpenEditor();
    IDocument document = getDocument(editor);
    String methodName = "aMethodName";
    String methodText = format("void %s() {}", methodName);
    int offset = document.get().indexOf("{") + 1;
    int length = 0;
    document.replace(offset, length, methodText);

    offset = document.get().indexOf(methodName);
    length = methodName.length();
    ITextSelection selection = new TextSelection(offset, length);
    editor.getSelectionProvider().setSelection(selection);

    IJavaElement element = getElementAtOffset(editor);
    // Make sure we got the selection right:
    assertEquals(IJavaElement.METHOD, element.getElementType());

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(30);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    assertEquals(1, tracker.getData().size());

    JavaEvent event = tracker.getData().iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
    assertEquals(element, event.getElement());
  }

  /**
   * Test an event on an package declaration. This event should be filtered on
   * save, so that instead of showing a user spent x amount of time on the
   * package declaration , we show that a user spent x amount of time on the
   * main type element.
   */
  @Test
  public void testFilter_existingElement_packageDeclaration() throws Exception {
    JavaEditor editor = closeAndOpenEditor();
    IDocument document = getDocument(editor);
    int offset = document.get().indexOf(pkg.getElementName());
    int len = pkg.getElementName().length();
    editor.getSelectionProvider().setSelection(new TextSelection(offset, len));

    IJavaElement element = getElementAtOffset(editor);
    // Make sure we got the selection right:
    assertEquals(IJavaElement.PACKAGE_DECLARATION, element.getElementType());

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(30);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    assertEquals(1, tracker.getData().size());

    JavaEvent event = tracker.getData().iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
    assertEquals(element.getParent(), event.getElement());
  }

  /**
   * Test an event on an anonymous type. This event should be filtered so that
   * we show the user has spent x amount of time on the type's first
   * non-anonymous parent.
   */
  @Test
  public void testFilter_existingElement_typeAnonymous() throws Exception {
    JavaEditor editor = closeAndOpenEditor();
    IDocument document = getDocument(editor);
    StringBuilder anonymous = new StringBuilder();
    anonymous.append("void aMethod() {");
    anonymous.append("  new Runnable() { ");
    anonymous.append("    public void run(){");
    anonymous.append("    } ");
    anonymous.append("  };");
    anonymous.append("}");

    int offset = document.get().indexOf("{") + 1;
    int len = 0;
    document.replace(offset, len, anonymous.toString());

    offset = document.get().indexOf("Runnable");
    len = "Runnable".length();
    ITextSelection selection = new TextSelection(offset, len);
    editor.getSelectionProvider().setSelection(selection);

    IJavaElement element = getElementAtOffset(editor);
    // Check that we got the selection right:
    assertEquals(IJavaElement.TYPE, element.getElementType());

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(35);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    assertEquals(1, tracker.getData().size());
    JavaEvent event = tracker.getData().iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
    assertEquals("aMethod", event.getElement().getElementName());
    assertEquals(IJavaElement.METHOD, event.getElement().getElementType());
  }

  /**
   * Test an event on an inner class. This event should be not filtered on save.
   */
  @Test
  public void testFilter_existingElement_typeInner() throws Exception {
    JavaEditor editor = closeAndOpenEditor();
    IDocument document = getDocument(editor);
    String innerClassName = "anInnerClassName";
    String innerClassText = format("%nstatic class %s {}", innerClassName);
    int offset = document.get().indexOf("{") + 1;
    int len = 0;
    document.replace(offset, len, innerClassText);

    offset = document.get().indexOf(innerClassName);
    len = innerClassName.length();
    editor.getSelectionProvider().setSelection(new TextSelection(offset, len));

    IJavaElement element = getElementAtOffset(editor);
    assertEquals(IJavaElement.TYPE, element.getElementType());

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    assertEquals(1, tracker.getData().size());
    final JavaEvent event = tracker.getData().iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
    assertEquals(innerClassName, element.getElementName());
    assertEquals(element, event.getElement());
  }

  /**
   * Test an event on a normal Java type (not anonymous, not inner class). This
   * event should not be filtered on save.
   */
  @Test
  public void testFilter_existingElement_typeNormal() throws Exception {
    JavaEditor editor = closeAndOpenEditor();
    String className = unit.getTypes()[0].getElementName();
    int offset = getDocument(editor).get().indexOf(className);
    int len = className.length();
    editor.getSelectionProvider().setSelection(new TextSelection(offset, len));

    IJavaElement element = getElementAtOffset(editor);
    assertEquals(IJavaElement.TYPE, element.getElementType());

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(30);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    assertEquals(1, tracker.getData().size());
    final JavaEvent event = tracker.getData().iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
    assertEquals(element, event.getElement());
  }

  /**
   * Test an event on a field. This event should be filtered on save, so that
   * instead of showing a user spent x amount of time on the field, we show that
   * a user spent x amount of time on the field's parent type.
   */
  @Test
  public void testFilter_exsitingElement_field() throws Exception {
    JavaEditor editor = closeAndOpenEditor();
    IDocument document = getDocument(editor);
    String fieldName = "aFieldName";
    String methodText = format("private int %s = 1;", fieldName);
    int offset = document.get().indexOf("{") + 1;
    int len = 0;
    document.replace(offset, len, methodText);

    offset = document.get().indexOf(fieldName);
    len = fieldName.length();
    editor.getSelectionProvider().setSelection(new TextSelection(offset, len));

    IJavaElement element = getElementAtOffset(editor);
    assertEquals(IJavaElement.FIELD, element.getElementType());

    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    // Ask the tracker to save the data, the data should be appropriately
    // filtered
    tracker.saveData();

    assertEquals(1, tracker.getData().size());
    JavaEvent event = tracker.getData().iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);

    // The filtered event should be on the field's parent, not on the field
    // itself:
    assertEquals(element.getParent(), event.getElement());
  }

  /**
   * Tests when the user becomes inactive.
   */
  @Test
  public void testUserInactive() throws Exception {
    JavaEditor editor = closeAndOpenEditor();

    // Set the editor to select the package declaration:
    int offset = getDocument(editor).get().indexOf(pkg.getElementName());
    int len = pkg.getElementName().length();
    editor.getSelectionProvider().setSelection(new TextSelection(offset, len));

    // Run the tracker to capture the event:
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(30);

    long preEnd = System.currentTimeMillis();
    callIdleDetectorToNotify();
    long postEnd = System.currentTimeMillis();

    // One data should be in the collection (the selected package declaration):
    assertEquals(1, tracker.getData().size());
    JavaEvent event = tracker.getData().iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
    assertEquals(getElementAtOffset(editor), event.getElement());
  }

  /**
   * Tests when the window lose focus.
   */
  @Test
  public void testWindowDeactivated() throws Exception {
    JavaEditor editor = closeAndOpenEditor();

    // Set the editor to select the package declaration:
    int offset = getDocument(editor).get().indexOf(pkg.getElementName());
    int len = pkg.getElementName().length();
    editor.getSelectionProvider().setSelection(new TextSelection(offset, len));

    // Run the tracker to capture the event:
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    Thread.sleep(20);

    Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    try {
      // Minimize the shell to cause it to lose focus:
      long preEnd = System.currentTimeMillis();
      shell.setMinimized(true);
      long postEnd = System.currentTimeMillis();

      // One data should be in the collection (the selected package
      // declaration):
      assertEquals(1, tracker.getData().size());
      JavaEvent event = tracker.getData().iterator().next();
      long start = event.getInterval().getStartMillis();
      long end = event.getInterval().getEndMillis();
      checkTime(preStart, start, postStart, preEnd, end, postEnd);
      assertEquals(getElementAtOffset(editor), event.getElement());

    } finally {
      shell.setMinimized(false);
    }
  }

  /**
   * Tests that events are recorded properly with the different states of the
   * window.
   */
  @Test
  public void testWindowDeactivatedThenActivated() throws Exception {
    JavaEditor editor = closeAndOpenEditor();

    // Set the editor to select the package declaration:
    int offset = getDocument(editor).get().indexOf(pkg.getElementName());
    int len = pkg.getElementName().length();
    editor.getSelectionProvider().setSelection(new TextSelection(offset, len));

    Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    try {
      // Now run the tracker to capture the event:
      long preStart = System.currentTimeMillis();
      tracker.setEnabled(true);
      long postStart = System.currentTimeMillis();

      Thread.sleep(20);

      // Minimize the shell to cause it to lose focus:
      long preEnd = System.currentTimeMillis();
      shell.setMinimized(true);
      long postEnd = System.currentTimeMillis();

      // One data should be in the collection (the selected package
      // declaration):
      assertEquals(1, tracker.getData().size());
      JavaEvent event = tracker.getData().iterator().next();
      long start = event.getInterval().getStartMillis();
      long end = event.getInterval().getEndMillis();
      checkTime(preStart, start, postStart, preEnd, end, postEnd);
      assertEquals(getElementAtOffset(editor), event.getElement());

      // Restore the shell to see if tracker will start tracking again:
      tracker.flushData();

      preStart = System.currentTimeMillis();
      shell.setMinimized(false);
      postStart = System.currentTimeMillis();

      Thread.sleep(25);

      // Minimise the shell to cause it to lose focus:
      preEnd = System.currentTimeMillis();
      shell.setMinimized(true);
      postEnd = System.currentTimeMillis();

      // One data should be in the collection (the selected package
      // declaration):
      assertEquals(1, tracker.getData().size());
      event = tracker.getData().iterator().next();
      start = event.getInterval().getStartMillis();
      end = event.getInterval().getEndMillis();
      checkTime(preStart, start, postStart, preEnd, end, postEnd);
      assertEquals(getElementAtOffset(editor), event.getElement());

    } finally {
      shell.setMinimized(false);
    }
  }

  /**
   * Hacks the global idle detector to cause it to notify it's observers.
   */
  protected void callIdleDetectorToNotify() throws Exception {
    Field isActive = IdleDetector.class.getDeclaredField("isActive");
    isActive.setAccessible(true);

    Method setChanged = Observable.class.getDeclaredMethod("setChanged");
    setChanged.setAccessible(true);

    Method notifyObservers = Observable.class.getDeclaredMethod("notifyObservers");
    notifyObservers.setAccessible(true);

    IdleDetector detector = TrackingPlugin.getDefault().getIdleDetector();
    detector.setRunning(true);
    isActive.set(detector, false);
    setChanged.invoke(detector);
    notifyObservers.invoke(detector);
    detector.setRunning(false);
  }

  /**
   * Closes all the editor in the workbench page, contents of editors are not
   * saved. Then opens the Java editor on {@link #unit}.
   * 
   * @return The editor.
   */
  protected JavaEditor closeAndOpenEditor() throws PartInitException {
    return closeAndOpenEditor(unit);
  }

  /**
   * Closes all the editor in the workbench page, contents of editors are not
   * saved. Then opens the Java editor on the file.
   * 
   * @param unit The Java file to open.
   * @return The editor.
   */
  protected JavaEditor closeAndOpenEditor(ICompilationUnit unit)
      throws PartInitException {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
    page.closeAllEditors(false);

    IFile file = (IFile) unit.getResource();
    IEditorDescriptor editor = workbench.getEditorRegistry().getDefaultEditor(
        file.getName());
    IEditorInput input = new FileEditorInput(file);
    return (JavaEditor) page.openEditor(input, editor.getId());
  }

  @Override
  protected JavaEvent createEvent() {
    return new JavaEvent(new Interval(0, 1),
        JavaCore.create("=Enfo/src<enfo{EnfoPlugin.java"));
  }

  @Override
  protected AbstractTracker<JavaEvent> createTracker() {
    return new JavaTracker();
  }

  /**
   * Gets the document from the editor.
   * 
   * @param editor The editor.
   * @return The document.
   */
  protected IDocument getDocument(JavaEditor editor) {
    IDocument doc = editor.getDocumentProvider().getDocument(
        editor.getEditorInput());
    if (doc == null) {
      fail("Document is null");
    }
    return doc;
  }

  private IViewDescriptor getRandomView() {
    IViewDescriptor[] v = PlatformUI.getWorkbench().getViewRegistry().getViews();
    return v[new Random().nextInt(v.length)];
  }

}
