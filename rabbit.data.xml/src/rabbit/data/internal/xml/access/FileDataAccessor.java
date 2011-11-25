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
package rabbit.data.internal.xml.access;

import rabbit.data.access.model.IFileData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.access.model.FileData;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.StoreNames;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.FileEventListType;
import rabbit.data.internal.xml.schema.events.FileEventType;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Accesses file event data.
 */
public class FileDataAccessor extends
    AbstractAccessor<IFileData, FileEventType, FileEventListType> {

  /**
   * Constructor.
   * 
   * @param store The data store to get the data from.
   * @throws NullPointerException If argument is null.
   */
  @Inject
  FileDataAccessor(@Named(StoreNames.FILE_STORE) IDataStore store) {
    super(store);
  }

  @Override
  protected IFileData createDataNode(LocalDate date, WorkspaceStorage ws,
      FileEventType type) throws Exception {
    return new FileData(date, ws, new Duration(type.getDuration()),
        workspaceRoot().getFile(new Path(type.getFilePath())));
  }

  @Override
  protected Collection<FileEventType> getElements(FileEventListType list) {
    return list.getFileEvent();
  }

  @Override
  protected Collection<FileEventListType> getCategories(EventListType doc) {
    return doc.getFileEvents();
  }

  /**
   * @return The root of the current workspace.
   */
  private IWorkspaceRoot workspaceRoot() {
    return ResourcesPlugin.getWorkspace().getRoot();
  }
}
