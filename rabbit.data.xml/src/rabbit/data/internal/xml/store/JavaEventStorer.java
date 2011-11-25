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

import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.StoreNames;
import rabbit.data.internal.xml.convert.IConverter;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.JavaEventListType;
import rabbit.data.internal.xml.schema.events.JavaEventType;
import rabbit.data.store.model.JavaEvent;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Stores {@link JavaEvent}s.
 */
@Singleton
public final class JavaEventStorer extends
    AbstractStorer<JavaEvent, JavaEventType, JavaEventListType> {

  /**
   * Constructor.
   * 
   * @param converter Converter for converting an event to its corresponding XML
   *          type.
   * @param merger Merger for merging two XML types.
   * @param store The data store to store the data to.
   */
  @Inject
  JavaEventStorer(
      IConverter<JavaEvent, JavaEventType> converter,
      IMerger<JavaEventType> merger,
      @Named(StoreNames.JAVA_STORE) IDataStore store) {
    super(converter, merger, store);
  }

  @Override
  protected List<JavaEventListType> getCategories(EventListType events) {
    return events.getJavaEvents();
  }

  @Override
  protected List<JavaEventType> getElements(JavaEventListType list) {
    return list.getJavaEvent();
  }

  @Override
  protected JavaEventListType newCategory(XMLGregorianCalendar date) {
    JavaEventListType type = new JavaEventListType();
    type.setDate(date);
    return type;
  }

}
