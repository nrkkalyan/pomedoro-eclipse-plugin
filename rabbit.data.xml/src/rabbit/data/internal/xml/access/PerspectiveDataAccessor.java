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

import rabbit.data.access.model.IPerspectiveData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.access.model.PerspectiveData;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.StoreNames;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Accesses perspective event data.
 */
public class PerspectiveDataAccessor
    extends
    AbstractAccessor<IPerspectiveData, PerspectiveEventType, PerspectiveEventListType> {

  /**
   * Constructor.
   * 
   * @param store The data store to get the data from.
   * @throws NullPointerException If argument is null.
   */
  @Inject
  PerspectiveDataAccessor(@Named(StoreNames.PERSPECTIVE_STORE) IDataStore store) {
    super(store);
  }

  @Override
  protected IPerspectiveData createDataNode(LocalDate date,
      WorkspaceStorage ws, PerspectiveEventType t) throws Exception {
    Duration duration = new Duration(t.getDuration());
    return new PerspectiveData(date, ws, duration, t.getPerspectiveId());
  }

  @Override
  protected Collection<PerspectiveEventListType> getCategories(EventListType t) {
    return t.getPerspectiveEvents();
  }

  @Override
  protected Collection<PerspectiveEventType> getElements(
      PerspectiveEventListType list) {
    return list.getPerspectiveEvent();
  }
}
