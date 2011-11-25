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
package rabbit.tracking.internal.util;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Utility class for working with the workbench.
 * 
 */
public final class WorkbenchUtil {

  /**
   * Gets the current active workbench window.
   * @return the active workbench window, or null.
   */
  public static IWorkbenchWindow getActiveWindow() {
    if (Display.getCurrent() != null) {
      return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    }

    final IWorkbenchWindow[] win = new IWorkbenchWindow[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        win[0] = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      }
    });
    return win[0];
  }

  /**
   * Checks whether the given window's shell is active.
   */
  public static boolean isActiveShell(IWorkbenchWindow win) {
    final Shell shell = win.getShell();
    final boolean[] result = new boolean[1];
    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        result[0] = shell.getDisplay().getActiveShell() == shell
            && !shell.getMinimized();
      }
    });
    return result[0];
  }

  /**
   * Gets the current window's active part.
   * @return the active part, or null.
   */
  public static IWorkbenchPart getActivePart() {
    return getActiveWindow().getPartService().getActivePart();
  }

  /**
   * Gets all the {@link IPartService} from the currently opened windows.
   * @return A Set of IPartService.
   */
  public static Set<IPartService> getPartServices() {
    Set<IPartService> result = new LinkedHashSet<IPartService>();
    IWorkbenchWindow[] ws = PlatformUI.getWorkbench().getWorkbenchWindows();
    for (IWorkbenchWindow w : ws) {
      result.add(w.getPartService());
    }
    return result;
  }

  /**
   * Gets the perspective of the given window.
   * @param win The window.
   * @return The perspective, or null.
   */
  public static IPerspectiveDescriptor getPerspective(
      @Nullable IWorkbenchWindow win) {
    if (win == null) {
      return null;
    }
    IWorkbenchPage page = win.getActivePage();
    if (page != null) {
      return page.getPerspective();
    }
    return null;
  }
}
