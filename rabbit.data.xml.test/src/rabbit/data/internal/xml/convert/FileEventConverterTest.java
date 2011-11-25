/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.data.internal.xml.convert;

import rabbit.data.internal.xml.convert.FileEventConverter;
import rabbit.data.internal.xml.schema.events.FileEventType;
import rabbit.data.store.model.FileEvent;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.Path;
import org.joda.time.Interval;

/**
 * @see FileEventConverter
 */
public class FileEventConverterTest extends
    AbstractConverterTest<FileEvent, FileEventType> {

  @Override
  protected FileEventConverter createConverter() {
    return new FileEventConverter();
  }

  @Override
  public void testConvert() throws Exception {
    FileEvent event = new FileEvent(new Interval(0, 1), new Path("/file/acb"));
    FileEventType type = converter.convert(event);
    assertEquals(event.getFilePath().toString(), type.getFilePath());
    assertEquals(event.getInterval().toDurationMillis(), type.getDuration());
  }

}
