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

import static rabbit.data.access.model.IFileData.DATE;
import static rabbit.data.access.model.IFileData.DURATION;
import static rabbit.data.access.model.IFileData.FILE;
import static rabbit.data.access.model.IFileData.WORKSPACE;

import rabbit.data.access.model.IFileData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.access.FileDataAccessor;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.FileEventListType;
import rabbit.data.internal.xml.schema.events.FileEventType;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.core.runtime.Path;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * @see FileDataAccessor
 */
public class FileDataAccessorTest extends
    AbstractAccessorTest2<IFileData, FileEventType, FileEventListType> {

  @Override
  protected void assertValues(FileEventType expected, LocalDate expectedDate,
      WorkspaceStorage expectedWs, IFileData actual) {

    assertThat(actual.get(DATE), is(expectedDate));
    assertThat(actual.get(DURATION).getMillis(), is(expected.getDuration()));
    assertThat(actual.get(WORKSPACE), is(expectedWs));
    assertThat(actual.get(FILE).getFullPath(),
        is(Path.fromPortableString(expected.getFilePath())));
  }

  @Override
  protected FileDataAccessor create() {
    return new FileDataAccessor(DataStore.FILE_STORE);
  }

  @Override
  protected FileEventListType createCategory() {
    return new FileEventListType();
  }

  @Override
  protected FileEventType createElement() {
    FileEventType type = new FileEventType();
    type.setDuration(1000);
    type.setFilePath("/project/file.txt");
    return type;
  }

  @Override
  protected List<FileEventListType> getCategories(EventListType events) {
    return events.getFileEvents();
  }

  @Override
  protected List<FileEventType> getElements(FileEventListType list) {
    return list.getFileEvent();
  }
}
