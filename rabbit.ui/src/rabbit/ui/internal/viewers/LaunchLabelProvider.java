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

import rabbit.ui.internal.util.LaunchName;

import static org.eclipse.debug.core.ILaunchManager.DEBUG_MODE;
import static org.eclipse.debug.core.ILaunchManager.RUN_MODE;
import static org.eclipse.debug.ui.IDebugUIConstants.IMG_OBJS_ENVIRONMENT;
import static org.eclipse.debug.ui.IDebugUIConstants.IMG_OBJS_LAUNCH_DEBUG;
import static org.eclipse.debug.ui.IDebugUIConstants.IMG_OBJS_LAUNCH_RUN;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * Label provider for launches.
 */
public final class LaunchLabelProvider extends NullLabelProvider {

  private static final Color GRAY = PlatformUI.getWorkbench().getDisplay()
      .getSystemColor(SWT.COLOR_DARK_GRAY);

  private final ILaunchManager manager;

  public LaunchLabelProvider() {
    manager = DebugPlugin.getDefault().getLaunchManager();
  }

  @Override
  public Color getForeground(Object element) {
    if (element instanceof ILaunchMode) {
      String id = ((ILaunchMode) element).getIdentifier();
      if (manager.getLaunchMode(id) == null) {
        return GRAY;
      }
    } else if (element instanceof ILaunchConfigurationType) {
      String id = ((ILaunchConfigurationType) element).getIdentifier();
      if (manager.getLaunchConfigurationType(id) == null) {
        return GRAY;
      }
    }
    return super.getForeground(element);
  }

  @Override
  public Image getImage(Object element) {
    if (element instanceof ILaunchMode) {
      return getLaunchModeImage(((ILaunchMode) element).getIdentifier());

    } else if (element instanceof ILaunchConfigurationType) {
      return DebugUITools.getImage(((ILaunchConfigurationType) element)
          .getIdentifier());

    } else if (element instanceof LaunchName) {
      return DebugUITools.getImage(((LaunchName) element).getLaunchTypeId());
    }
    return super.getImage(element);
  }

  @Override
  public String getText(Object element) {
    if (element instanceof ILaunchMode) {
      return ((ILaunchMode) element).getLabel().replace("&", "");
    } else if (element instanceof ILaunchConfigurationType) {
      return ((ILaunchConfigurationType) element).getName();
    } else if (element instanceof LaunchName) {
      return ((LaunchName) element).getLaunchName();
    }
    return super.getText(element);
  }

  private Image getLaunchModeImage(String modeId) {
    if (modeId.equals(DEBUG_MODE)) {
      return DebugUITools.getImage(IMG_OBJS_LAUNCH_DEBUG);
    } else if (modeId.equals(RUN_MODE)) {
      return DebugUITools.getImage(IMG_OBJS_LAUNCH_RUN);
    } else {
      return DebugUITools.getImage(IMG_OBJS_ENVIRONMENT);
    }
  }
}
