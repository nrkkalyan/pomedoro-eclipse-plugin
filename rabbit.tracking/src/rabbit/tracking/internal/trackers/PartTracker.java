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
package rabbit.tracking.internal.trackers;

import rabbit.data.handler.DataHandler;
import rabbit.data.store.IStorer;
import rabbit.data.store.model.PartEvent;

import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Interval;

/**
 * Tracks workbench part usage.
 */
public class PartTracker extends AbstractPartTracker<PartEvent> {

  /**
   * Constructor.
   */
  public PartTracker() {
    super();
  }

  @Override
  protected IStorer<PartEvent> createDataStorer() {
    return DataHandler.getStorer(PartEvent.class);
  }

  @Override
  protected PartEvent tryCreateEvent(long start, long end, IWorkbenchPart part) {
    return new PartEvent(new Interval(start, end), part);
  }
}
