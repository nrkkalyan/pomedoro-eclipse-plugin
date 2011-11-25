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

import rabbit.data.access.model.ILaunchData;
import rabbit.data.access.model.LaunchConfigurationDescriptor;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.access.model.LaunchData;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.StoreNames;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventType;

import static com.google.common.collect.Sets.newHashSet;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Set;

/**
 * Accesses launch event data.
 */
public class LaunchDataAccessor extends
    AbstractAccessor<ILaunchData, LaunchEventType, LaunchEventListType> {

  /**
   * Constructor.
   * 
   * @param store The data store to get the data from.
   * @throws NullPointerException If argument is null.
   */
  @Inject
  LaunchDataAccessor(@Named(StoreNames.LAUNCH_STORE) IDataStore store) {
    super(store);
  }

  @Override
  protected ILaunchData createDataNode(LocalDate date, WorkspaceStorage ws,
      LaunchEventType type) throws Exception {
    Duration duration = new Duration(type.getTotalDuration());
    LaunchConfigurationDescriptor config = new LaunchConfigurationDescriptor(
        type.getName(), type.getLaunchModeId(), type.getLaunchTypeId());
    Set<IFile> files = newHashSet();
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    for (String str : type.getFilePath()) {
      try {
        files.add(root.getFile(new Path(str)));
      } catch (Exception e) {
        System.err.println(getClass().getSimpleName()
            + " - Invalid file path: str\n\t" + e.getMessage());
      }
    }
    return new LaunchData(date, ws, config, type.getCount(), duration, files);
  }

  @Override
  protected Collection<LaunchEventListType> getCategories(EventListType doc) {
    return doc.getLaunchEvents();
  }

  @Override
  protected Collection<LaunchEventType> getElements(LaunchEventListType list) {
    return list.getLaunchEvent();
  }
}
