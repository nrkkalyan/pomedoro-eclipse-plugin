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
import rabbit.data.internal.xml.schema.events.LaunchEventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventType;
import rabbit.data.store.model.LaunchEvent;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Stores {@link LaunchEvent}
 */
@Singleton
public final class LaunchEventStorer extends
    AbstractStorer<LaunchEvent, LaunchEventType, LaunchEventListType> {

  /**
   * Constructor.
   * 
   * @param converter Converter for converting an event to its corresponding XML
   *          type.
   * @param merger Merger for merging two XML types.
   * @param store The data store to store the data to.
   */
  @Inject
  LaunchEventStorer(
      IConverter<LaunchEvent, LaunchEventType> converter,
      IMerger<LaunchEventType> merger, 
      @Named(StoreNames.LAUNCH_STORE) IDataStore store) {
    super(converter, merger, store);
  }

  @Override
  protected List<LaunchEventListType> getCategories(EventListType events) {
    return events.getLaunchEvents();
  }

  @Override
  protected List<LaunchEventType> getElements(LaunchEventListType list) {
    return list.getLaunchEvent();
  }

  @Override
  protected LaunchEventListType newCategory(XMLGregorianCalendar date) {
    LaunchEventListType type = objectFactory.createLaunchEventListType();
    type.setDate(date);
    return type;
  }
}
