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
package rabbit.data.access.model;

import rabbit.data.common.TaskId;

import org.eclipse.core.commands.Command;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.Set;

/**
 * A set of common keys.
 */
public class Keys {

  /**
   * Key for a command.
   */
  public static final IKey<Command> COMMAND = Key.create();
  
  /**
   * Key for a count.
   */
  public static final IKey<Integer> COUNT = Key.create();
  
  /**
   * Key for a date.
   */
  public static final IKey<LocalDate> DATE = Key.create();
  
  /**
   * Key for a duration.
   */
  public static final IKey<Duration> DURATION = Key.create();
  
  /**
   * Key for a file.
   */
  public static final IKey<IFile> FILE = Key.create();
  
  /**
   * Key for a set of files.
   */
  public static final IKey<Set<IFile>> FILES = Key.create();
  

  /**
   * Key for a Java element.
   */
  public static final IKey<IJavaElement> JAVA_ELEMENT = Key.create();

  /**
   * Key for a launch configuration.
   */
  public static final IKey<LaunchConfigurationDescriptor> LAUNCH_CONFIG = Key.create();

  /**
   * Key for a workbench part ID.
   */
  public static final IKey<String> PART_ID = Key.create();

  /**
   * Key for a perspective ID.
   */
  public static final IKey<String> PERSPECTIVE_ID = Key.create();
  
  /**
   * Key for a task ID.
   */
  public static final IKey<TaskId> TASK_ID = Key.create();

  /**
   * Key for a workspace.
   */
  public static final IKey<WorkspaceStorage> WORKSPACE = Key.create();
  
  private Keys() {}
}
