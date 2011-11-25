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

import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.util.ICategory;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * Internal enum of categories, shared by pages.
 */
public enum Category implements ICategory {

  /** Date category. */
  DATE("Dates", SharedImages.CALENDAR),

  /** Command category. */
  COMMAND("Commands", SharedImages.ELEMENT),

  /** Launch category. */
  LAUNCH("Launches", SharedImages.LAUNCH),

  /** Launch mode category. */
  LAUNCH_MODE("Launch Modes", SharedImages.LAUNCH_MODE),

  /** Launch type category */
  LAUNCH_TYPE("Launch Types", SharedImages.LAUNCH_TYPE),

  /** Project category */
  PROJECT("Projects", PlatformUI.getWorkbench().getSharedImages()
      .getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT)),

  /** Folder category */
  FOLDER("Folders", PlatformUI.getWorkbench().getSharedImages()
      .getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER)),

  /** File category */
  FILE("Files", PlatformUI.getWorkbench().getSharedImages()
      .getImageDescriptor(ISharedImages.IMG_OBJ_FILE)),

  /** Workbench tool category */
  WORKBENCH_TOOL("Workbench Tools", PlatformUI.getWorkbench().getSharedImages()
      .getImageDescriptor(ISharedImages.IMG_DEF_VIEW)),

  /** Perspective category */
  PERSPECTIVE("Perspectives", SharedImages.PERSPECTIVE),

  /** Workspace category */
  WORKSPACE("Workspaces", SharedImages.WORKSPACE),

  /** Task category */
  TASK("Tasks", SharedImages.TASK),

  /** Java package root (source folder) category */
  JAVA_PACKAGE_ROOT("Source Folders", SharedImages.JAVA_PACKAGE_ROOT),

  /** Java package category */
  JAVA_PACKAGE("Packages", SharedImages.JAVA_PACKAGE),

  /** Java type root (Java file) category */
  JAVA_TYPE_ROOT("Files", SharedImages.JAVA_TYPE_ROOT),

  /** Java type category */
  JAVA_TYPE("Types", SharedImages.JAVA_TYPE),

  /** Java method category */
  JAVA_METHOD("Methods", SharedImages.JAVA_METHOD),

  /** Java member category, includes {@link #JAVA_TYPE} and {@link #JAVA_METHOD} */
  JAVA_MEMBER("Java Members", SharedImages.JAVA_MEMBER),

  ;

  private String text;
  private ImageDescriptor image;

  private Category(String text, ImageDescriptor image) {
    this.text = text;
    this.image = image;
  }

  @Override
  public ImageDescriptor getImageDescriptor() {
    return image;
  }

  @Override
  public String getText() {
    return text;
  }

}
