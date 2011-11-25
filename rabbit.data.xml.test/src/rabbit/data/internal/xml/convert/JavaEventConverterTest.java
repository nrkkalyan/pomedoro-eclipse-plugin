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

import rabbit.data.internal.xml.convert.AbstractConverter;
import rabbit.data.internal.xml.convert.JavaEventConverter;
import rabbit.data.internal.xml.schema.events.JavaEventType;
import rabbit.data.store.model.JavaEvent;

import static org.junit.Assert.assertEquals;

import org.eclipse.jdt.core.JavaCore;
import org.joda.time.Interval;

/**
 * @see JavaEventConverter
 */
public class JavaEventConverterTest extends AbstractConverterTest<JavaEvent, JavaEventType> {

  @Override
  protected AbstractConverter<JavaEvent, JavaEventType> createConverter() {
    return new JavaEventConverter();
  }

  @Override
  public void testConvert() throws Exception {
    JavaEvent event = new JavaEvent(new Interval(0, 1), JavaCore.create("=Enfo/src<enfo{EnfoPlugin.java"));
    JavaEventType type = converter.convert(event);
    assertEquals(event.getInterval().toDurationMillis(), type.getDuration());
    assertEquals(event.getElement().getHandleIdentifier(), type.getHandleIdentifier());
  }

}
