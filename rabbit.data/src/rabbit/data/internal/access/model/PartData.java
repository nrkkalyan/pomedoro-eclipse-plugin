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
package rabbit.data.internal.access.model;

import rabbit.data.access.model.IKey;
import rabbit.data.access.model.IPartData;
import rabbit.data.access.model.WorkspaceStorage;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewRegistry;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Contains information about the time spent on a workbench tool.
 */
public class PartData implements IPartData {
  
  /**
   * Immutable map of data.
   */
  private final Map<IKey<? extends Object>, Object> data;
  
  /**
   * Constructor.
   * @param date The date of the session.
   * @param workspace The workspace of the session.
   * @param duration The duration.
   * @param partId The workbench tool ID.
   * @throws NullPointerException If any of the arguments are null.
   */
  public PartData(
      LocalDate date, 
      WorkspaceStorage workspace, 
      Duration duration, 
      String partId) {
    
    data = new KeyMapBuilder()
        .put(DATE,      checkNotNull(date, "date"))
        .put(WORKSPACE, checkNotNull(workspace, "workspace"))
        .put(DURATION,  checkNotNull(duration, "duration"))
        .put(PART_ID,   checkNotNull(partId, "partId"))
        .build();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T get(@Nullable IKey<T> key) {
    return (T) data.get(key);
  }

  @Override
  public IWorkbenchPartDescriptor getPart() {
    IWorkbenchPartDescriptor part = viewRegistry().find(get(PART_ID));
    if (part == null) {
      return editorRegistry().findEditor(get(PART_ID));
    }
    return part;
  }

  /**
   * @return The workbench editor registry.
   */
  private IEditorRegistry editorRegistry() {
    return PlatformUI.getWorkbench().getEditorRegistry();
  }
  
  /**
   * @return The workbench view registry.
   */
  private IViewRegistry viewRegistry() {
    return PlatformUI.getWorkbench().getViewRegistry();
  }
}
