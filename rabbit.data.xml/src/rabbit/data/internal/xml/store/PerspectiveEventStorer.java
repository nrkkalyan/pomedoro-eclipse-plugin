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
import rabbit.data.internal.xml.schema.events.PerspectiveEventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;
import rabbit.data.store.model.PerspectiveEvent;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Stores {@link PerspectiveEvent}.
 */
@Singleton
public final class PerspectiveEventStorer extends 
    AbstractStorer<PerspectiveEvent, PerspectiveEventType, PerspectiveEventListType> {

  /**
   * Constructor.
   * 
   * @param converter Converter for converting an event to its corresponding XML
   *          type.
   * @param merger Merger for merging two XML types.
   * @param store The data store to store the data to.
   */
  @Inject 
  PerspectiveEventStorer(
      IConverter<PerspectiveEvent, PerspectiveEventType> converter,
      IMerger<PerspectiveEventType> merger, 
      @Named(StoreNames.PERSPECTIVE_STORE) IDataStore store) {
    super(converter, merger, store);
  }

  @Override
  protected List<PerspectiveEventListType> getCategories(EventListType events) {
    return events.getPerspectiveEvents();
  }

  @Override
  protected List<PerspectiveEventType> getElements(PerspectiveEventListType list) {
    return list.getPerspectiveEvent();
  }

  @Override
  protected PerspectiveEventListType newCategory(XMLGregorianCalendar date) {
    PerspectiveEventListType t = objectFactory.createPerspectiveEventListType();
    t.setDate(date);
    return t;
  }
}
