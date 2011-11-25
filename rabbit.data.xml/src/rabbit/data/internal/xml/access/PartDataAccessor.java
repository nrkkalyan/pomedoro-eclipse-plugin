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

import rabbit.data.access.model.IPartData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.access.model.PartData;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.StoreNames;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.PartEventListType;
import rabbit.data.internal.xml.schema.events.PartEventType;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Accesses workbench part event data.
 */
public class PartDataAccessor extends
    AbstractAccessor<IPartData, PartEventType, PartEventListType> {

  /**
   * Constructor.
   * 
   * @param store The data store to get the data from.
   * @throws NullPointerException If argument is null.
   */
  @Inject
  PartDataAccessor(@Named(StoreNames.PART_STORE) IDataStore store) {
    super(store);
  }

  @Override
  protected Collection<PartEventListType> getCategories(EventListType doc) {
    return doc.getPartEvents();
  }

  @Override
  protected IPartData createDataNode(LocalDate date, WorkspaceStorage ws,
      PartEventType t) throws Exception {
    return new PartData(date, ws, new Duration(t.getDuration()), t.getPartId());
  }

  @Override
  protected Collection<PartEventType> getElements(PartEventListType list) {
    return list.getPartEvent();
  }
}
