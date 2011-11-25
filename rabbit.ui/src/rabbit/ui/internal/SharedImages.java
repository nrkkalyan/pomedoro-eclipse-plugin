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
package rabbit.ui.internal;

import static rabbit.ui.internal.RabbitUI.PLUGIN_ID;

import static org.eclipse.ui.plugin.AbstractUIPlugin.imageDescriptorFromPlugin;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Shared image descriptors.
 */
public class SharedImages {

  /** Refresh image. */
  public static final ImageDescriptor REFRESH = getObj16("refresh.gif");

  /** Expand all image. */
  public static final ImageDescriptor EXPAND_ALL = getObj16("expandall.gif");

  /** Collapse all image. */
  public static final ImageDescriptor COLLAPSE_ALL = getObj16("collapseall.gif");

  /** Calendar image. */
  public static final ImageDescriptor CALENDAR = getObj16("calendar.png");

  /** Generic element image. */
  public static final ImageDescriptor ELEMENT = getObj16("element.gif");

  /** Tree hierarchy image. */
  public static final ImageDescriptor HIERARCHY = getObj16("hierarchical.gif");

  /** Color brush image. */
  public static final ImageDescriptor BRUSH = getObj16("line-color.gif");

  /** Search image. */
  public static final ImageDescriptor SEARCH = getObj16("search.gif");

  /** Launch type image. */
  public static final ImageDescriptor LAUNCH_TYPE = getObj16("launch-type.gif");

  /** Launch mode image. */
  public static final ImageDescriptor LAUNCH_MODE = getObj16("launch-mode.gif");

  /** Launch image. */
  public static final ImageDescriptor LAUNCH = getObj16("run.gif");

  /** Default perspective image. */
  public static final ImageDescriptor PERSPECTIVE = getObj16("defaultpersp.gif");

  /** Editor area image. */
  public static final ImageDescriptor EDITOR = getObj16("editor-highlight.gif");

  /** View area image. */
  public static final ImageDescriptor VIEW = getObj16("view-highlight.gif");

  /** Workspace image. */
  public static final ImageDescriptor WORKSPACE = getObj16("workspace.gif");

  /** Task image. */
  public static final ImageDescriptor TASK = getObj16("task.gif");

  public static final ImageDescriptor JAVA_MEMBER = getObj16("java_member.gif");
  public static final ImageDescriptor JAVA_METHOD = getObj16("method.gif");
  public static final ImageDescriptor JAVA_TYPE = getObj16("class.gif");
  public static final ImageDescriptor JAVA_TYPE_ROOT = getObj16("java_file.gif");
  public static final ImageDescriptor JAVA_PACKAGE = getObj16("package.gif");
  public static final ImageDescriptor JAVA_PACKAGE_ROOT = getObj16("src_folder.gif");

  /**
   * Gets an image descriptor from the directory "icons/full/obj16/".
   * @param name The name of the file.
   * @return An image descriptor, or null if not found.
   */
  private static ImageDescriptor getObj16(String name) {
    return imageDescriptorFromPlugin(PLUGIN_ID, "icons/full/obj16/" + name);
  }
}
