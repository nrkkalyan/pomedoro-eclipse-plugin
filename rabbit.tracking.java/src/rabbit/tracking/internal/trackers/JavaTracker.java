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

import rabbit.data.handler.DataHandler;
import rabbit.data.store.IStorer;
import rabbit.data.store.model.JavaEvent;
import rabbit.tracking.internal.IdleDetector;
import rabbit.tracking.internal.TrackingPlugin;
import rabbit.tracking.internal.util.Recorder;
import rabbit.tracking.internal.util.WorkbenchUtil;

import com.google.common.collect.Sets;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.actions.SelectionConverter;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Interval;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Tracks time spent on Java elements such as classes, methods.
 */
@SuppressWarnings("restriction")
public class JavaTracker extends AbstractTracker<JavaEvent> {

  /*
   * Note that a lot of elements may be tracked by this tracker, and many of
   * them are of no interest to us, for example, invalid elements, anonymous
   * classes (their have no unique identifier) etc. Therefore we should perform
   * filtering on the data before saving. Filtering the data does not remove the
   * elements we don't want, instead we replace the element with a parent which
   * is of our interest. For example, a data node before filter may be
   * "the user spent 2 minutes on elementA", and after filter it may be
   * "the user spent 2 minutes on the parent of elementA", where "elementA" is
   * of no interest to us, but the parent of the element does.
   * 
   * The following element types are of interest to us:
   * 
   * 1) Type elements (classes, interfaces etc) that are not anonymous. 2)
   * Methods (includes constructors) that are not enclosed in anonymous types.
   * 3) Static initializers.
   * 
   * 
   * Other elements will be converted.
   * 
   * (A secrete note: doing so also reduces the size of the data files on disk,
   * shhhh!)
   */

  /**
   * A set of all text widgets that are currently being listened to. This set is
   * not synchronised.
   */
  private final Set<StyledText> registeredWidgets;

  /**
   * Recorder for recording time duration.
   */
  private final Recorder<IJavaElement> recorder = new Recorder<IJavaElement>();

  /**
   * A part listener listening for Java editor events.
   */
  private final IPartListener partListener = new IPartListener() {

    @Override
    public void partActivated(IWorkbenchPart part) {
      checkStart(part);
    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part) {
      // Do nothing.
    }

    @Override
    public void partClosed(IWorkbenchPart part) {
      if (part instanceof JavaEditor) {
        deregister((JavaEditor) part);
      }
    }

    @Override
    public void partDeactivated(IWorkbenchPart part) {
      if (part instanceof JavaEditor) {
        recorder.stop();
      }
    }

    @Override
    public void partOpened(IWorkbenchPart part) {
      if (part instanceof JavaEditor) {
        register((JavaEditor) part);
      }
    }
  };

  /**
   * A window listener listening to window focus.
   */
  private final IWindowListener winListener = new IWindowListener() {

    @Override
    public void windowActivated(IWorkbenchWindow window) {
      checkStart(window.getPartService().getActivePart());
    }

    @Override
    public void windowClosed(IWorkbenchWindow window) {
      recorder.stop();
      deregister(window);
    }

    @Override
    public void windowDeactivated(IWorkbenchWindow window) {
      recorder.stop();
    }

    @Override
    public void windowOpened(IWorkbenchWindow window) {
      register(window);
      if (window.getWorkbench().getActiveWorkbenchWindow() == window) {
        checkStart(window.getPartService().getActivePart());
      }
    }
  };

  /**
   * An observer observing on the {@link #recorder} and use activeness.
   */
  private final Observer observer = new Observer() {
    @Override
    public void update(Observable o, Object arg) {
      if (!isEnabled()) {
        return;
      }

      if (o == TrackingPlugin.getDefault().getIdleDetector()) {
        if (((IdleDetector) o).isUserActive()) {
          IWorkbenchWindow win = WorkbenchUtil.getActiveWindow();
          if (win != null && WorkbenchUtil.isActiveShell(win)) {
            checkStart(win.getPartService().getActivePart());
          }
        } else {
          recorder.stop();
        }
      } else if (o == recorder) {
        long start = recorder.getLastRecord().getStartTimeMillis();
        long end = recorder.getLastRecord().getEndTimeMillis();
        IJavaElement element = recorder.getLastRecord().getUserData();
        if (element != null) {
          addData(new JavaEvent(new Interval(start, end), element));
        }
      }
    }
  };

  /**
   * Listener to listen to keyboard input and mouse input on text widgets of
   * editors.
   */
  private final Listener listener = new Listener() {

    // This listener used to provide compatibility with Eclipse 3.4, otherwise
    // org.eclipse.swt.custom.CaretListener might be a better option (Eclipse
    // * 3.5+).

    @Override
    public void handleEvent(Event event) {
      checkStart();
    }
  };

  /**
   * Constructor.
   */
  public JavaTracker() {
    super();
    registeredWidgets = Sets.newHashSet();
    recorder.addObserver(observer);
  }

  @Override
  public void saveData() {
    filterData();
    super.saveData();
  }

  @Override
  protected IStorer<JavaEvent> createDataStorer() {
    return DataHandler.getStorer(JavaEvent.class);
  }

  @Override
  protected void doDisable() {
    recorder.stop();
    TrackingPlugin.getDefault().getIdleDetector().deleteObserver(observer);

    IWorkbench workbench = PlatformUI.getWorkbench();
    workbench.removeWindowListener(winListener);
    for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
      deregister(window);
    }
  }

  @Override
  protected void doEnable() {
    IWorkbench workbench = PlatformUI.getWorkbench();
    workbench.addWindowListener(winListener);
    for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
      register(window);
    }
    TrackingPlugin.getDefault().getIdleDetector().addObserver(observer);

    // If there is an Java editor already active, start tracking:
    checkStart();
  }

  /**
   * Tries to start a tracking session, if the current element is not change,
   * will do nothing, otherwise ends a session if there is one running, then if
   * the currently selected element in Eclipse's active editor is not null,
   * starts a new session.
   */
  private void checkStart() {
    IWorkbenchWindow win = WorkbenchUtil.getActiveWindow();
    if (WorkbenchUtil.isActiveShell(win)) {
      checkStart(win.getPartService().getActivePart());
    }
  }

  /**
   * Tries to start a tracking session, if the current element is not change,
   * will do nothing, otherwise ends a session if there is one running, then if
   * the currently selected element in Eclipse's active editor is not null,
   * starts a new session.
   * 
   * @param activePart The currently active part of the workbench, may be null.
   */
  private void checkStart(final IWorkbenchPart activePart) {
    if (!(activePart instanceof JavaEditor)) {
      return;
    }

    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        IJavaElement element = null;
        try {
          element = SelectionConverter.getElementAtOffset((JavaEditor) activePart);
          if (element != null) {
            recorder.start(element);
          }
        } catch (JavaModelException e) {
          // Nothing we can do.
          System.err.println(getClass().getSimpleName() + " - checkStart: "
              + e.getMessage());
        }
      }
    });
  }

  /**
   * Removes the workbench window so that it's no longer being tracked.
   * 
   * @param window The workbench window.
   */
  private void deregister(IWorkbenchWindow window) {
    window.getPartService().removePartListener(partListener);
    for (IWorkbenchPage page : window.getPages()) {
      for (IEditorReference ref : page.getEditorReferences()) {
        IEditorPart editor = ref.getEditor(false);
        if (editor instanceof JavaEditor) {
          deregister((JavaEditor) editor);
        }
      }
    }
  }

  /**
   * Removes the editor no that it's no longer being tracked.
   * 
   * @param editor The editor.
   */
  private synchronized void deregister(JavaEditor editor) {
    final StyledText widget = editor.getViewer().getTextWidget();
    if (registeredWidgets.contains(widget)) {
      PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
        @Override
        public void run() {
          widget.removeListener(SWT.KeyDown, listener);
          widget.removeListener(SWT.MouseDown, listener);
        }
      });
      registeredWidgets.remove(widget);
    }
  }

  /**
   * Performs filtering of the data before saving.
   * <p>
   * NOTE: Then a user starts to type a new java element, like a method, he/she
   * knows what the name he/she is going to type for the method, but we have no
   * way of knowing that, so lots of events may be recorded before he/she
   * finishes typing the name. For example, if the user want to type "hello" as
   * the method name, there will be events recorded about the java element
   * "hel", or "hell", or "hello", we only need one of them ("hello") but we
   * also want to keep the time about the invalid ones, so before we save the
   * data, we check for non-existent java elements, and instead of saving the
   * data under those elements, we save the data under the first existing parent
   * of the elements, if all parents are missing (e.g. deletes the file), we
   * save it under the file parent, like "File.java".
   * </p>
   */
  private void filterData() {
    Set<JavaEvent> filteredData = Sets.newLinkedHashSet();
    for (JavaEvent event : getData()) {
      IJavaElement e = event.getElement();
      // ITypeRoot represents the file, xxx.java. Everything above that is not
      // modifiable in a JavaEditor, so no need to check them:
      if (!e.exists()) {
        for (; !e.exists() && !(e instanceof ITypeRoot); e = e.getParent());
        filteredData.add(new JavaEvent(event.getInterval(), e));

      } else {
        IJavaElement actual = null;
        try {
          actual = filterElement(e);
        } catch (JavaModelException ex) {
          actual = null;
          ex.printStackTrace();
        }

        if (actual == null) {
          filteredData.add(event);
        } else {
          filteredData.add(new JavaEvent(event.getInterval(), actual));
        }
      }
    }
    // Replace the old data with the filtered:
    flushData();
    for (JavaEvent event : filteredData) {
      addData(event);
    }
  }

  /**
   * Gets the actual element that we want before saving. One of the following
   * types is returned:
   * 
   * <ul>
   * <li>A type that is not anonymous.</li>
   * <li>A method that is not enclosed in an anonymous type.</li>
   * <li>An initializer.</li>
   * <li>A compilation unit.</li>
   * <li>A class file.</li>
   * <li>Null</li>
   * </ul>
   * 
   * @param element The element to filter.
   * @return A filtered element, or null if not found.
   * @throws JavaModelException If this element does not exist or if an
   *           exception occurs while accessing its corresponding resource.
   */
  private IJavaElement filterElement(@Nullable IJavaElement element)
      throws JavaModelException {

    if (element == null) {
      return null;
    }

    switch (element.getElementType()) {
      case IJavaElement.TYPE:
        if (((IType) element).isAnonymous()) {
          return filterElement(element.getParent());
        }
        return element;

      case IJavaElement.METHOD:
        if (((IType) element.getParent()).isAnonymous()) {
          return filterElement(element.getParent());
        }
        return element;

      case IJavaElement.INITIALIZER:
      case IJavaElement.COMPILATION_UNIT:
      case IJavaElement.CLASS_FILE:
        return element;

      default:
        return filterElement(element.getParent());
    }
  }

  /**
   * Registers the given workbench window to be tracked.
   * 
   * @param window The workbench window.
   */
  private void register(IWorkbenchWindow window) {
    window.getPartService().addPartListener(partListener);
    for (IWorkbenchPage page : window.getPages()) {
      for (IEditorReference ref : page.getEditorReferences()) {
        IEditorPart editor = ref.getEditor(false);
        if (editor instanceof JavaEditor) {
          register((JavaEditor) editor);
        }
      }
    }
  }

  /**
   * Registers the given editor to be tracked. Has no effect if the editor is
   * already registered.
   * 
   * @param editor The editor.
   */
  private synchronized void register(JavaEditor editor) {
    final StyledText widget = editor.getViewer().getTextWidget();
    if (!registeredWidgets.contains(widget)) {
      PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
        @Override
        public void run() {
          widget.addListener(SWT.KeyDown, listener);
          widget.addListener(SWT.MouseDown, listener);
        }
      });
      registeredWidgets.add(widget);
    }
  }
}
