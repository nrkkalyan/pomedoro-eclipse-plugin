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
package rabbit.data.internal.xml.store;

import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.convert.FileEventConverter;
import rabbit.data.internal.xml.merge.FileEventTypeMerger;
import rabbit.data.internal.xml.schema.events.FileEventListType;
import rabbit.data.internal.xml.schema.events.FileEventType;
import rabbit.data.store.model.FileEvent;

import com.google.common.base.Objects;

import org.eclipse.core.runtime.Path;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * @see FileEventStorer
 */
public class FileEventStorerTest extends
    AbstractStorerTest<FileEvent, FileEventType, FileEventListType> {

  @Override
  protected FileEventStorer createStorer() {
    return new FileEventStorer(new FileEventConverter(), 
                               new FileEventTypeMerger(), 
                               DataStore.FILE_STORE);
  }

  @Override
  protected FileEvent createEvent(DateTime t) {
    return new FileEvent(new Interval(t, t.plus(1)), new Path("/some"));
  }

  @Override
  protected FileEvent createEventDiff(DateTime t) {
    return new FileEvent(new Interval(t, t.plus(2)), new Path("/some/some"));
  }

  @Override
  protected boolean equal(FileEventType t1, FileEventType t2) {
    return Objects.equal(t1.getFilePath(), t2.getFilePath())
        && t1.getDuration() == t2.getDuration();
  }

}
