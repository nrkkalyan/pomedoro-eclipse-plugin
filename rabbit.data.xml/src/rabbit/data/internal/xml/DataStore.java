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
package rabbit.data.internal.xml;

import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.ObjectFactory;

import com.google.common.collect.Lists;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.joda.time.LocalDate;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

/**
 * Data stores.
 */
public enum DataStore implements IDataStore {

  //@formatter:off
  COMMAND_STORE     ("commandEvents"),
  PART_STORE        ("partEvents"),
  PERSPECTIVE_STORE ("perspectiveEvents"),
  FILE_STORE        ("fileEvents"),
  TASK_STORE        ("taskEvents"),
  LAUNCH_STORE      ("launchEvents"),
  SESSION_STORE     ("sessionEvents"),
  JAVA_STORE        ("javaEvents");
  //@formatter:on

  /**
   * An object factory for creating XML object types.
   */
  private final ObjectFactory objectFactory = new ObjectFactory();

  private String id;

  private DataStore(String id) {
    this.id = id;
  }

  @Override
  public File getDataFile(LocalDate date) {
    return getDataFile(date, getStorageLocation());
  }

  @Override
  public File getDataFile(LocalDate date, IPath location) {
    return location.append(id + "-" + date.toString("yyyy-MM"))
        .addFileExtension("xml").toFile();
  }

  @Override
  public List<File> getDataFiles(LocalDate start, LocalDate end) {
    List<File> result = Lists.newLinkedList();
    IPath[] storagePaths = XmlPlugin.getDefault().getStoragePaths();
    for (IPath path : storagePaths) {
      result.addAll(getDataFiles(start, end, path));
    }
    return result;
  }

  @Override
  public List<File> getDataFiles(LocalDate start, LocalDate end, IPath location) {
    // Work out the number of months between the two dates, regardless of the
    // dateOfMonth of each date:
    int numMonths = (end.getYear() - start.getYear()) * 12;
    numMonths += end.getMonthOfYear() - start.getMonthOfYear();

    List<File> result = Lists.newLinkedList();
    for (; numMonths >= 0; numMonths--) {
      File f = getDataFile(end.minusMonths(numMonths), location);
      if (f.exists()) {
        result.add(f);
      }
    }
    return result;
  }

  @Override
  public IPath getStorageLocation() {
    IPath path = XmlPlugin.getDefault().getStoragePath();
    File f = path.toFile();
    if (!f.exists()) {
      if (!f.mkdirs()) {
        XmlPlugin
            .getDefault()
            .getLog()
            .log(
                new Status(IStatus.ERROR, XmlPlugin.PLUGIN_ID,
                    "Unable to create storage location. Perhaps no write permission?\n"
                        + f.getAbsolutePath()));
      }
    }
    return path;
  }

  @Override
  public EventListType read(File file) {
    try {
      if (file.exists()) {
        Object obj = JaxbUtil.unmarshal(file);
        if (obj instanceof JAXBElement<?>) {
          JAXBElement<?> element = (JAXBElement<?>) obj;
          if (element.getValue() instanceof EventListType) {
            return (EventListType) element.getValue();
          }
        }
      }

    } catch (JAXBException e) {
      return objectFactory.createEventListType();
    } catch (Exception e) {
      // XML file not valid?

      XmlPlugin
          .getDefault()
          .getLog()
          .log(
              new Status(IStatus.ERROR, XmlPlugin.PLUGIN_ID, e.getMessage(), e));
      return objectFactory.createEventListType();
    }
    return objectFactory.createEventListType();
  }

  @Override
  public boolean write(EventListType doc, File f) {
    if (doc == null || f == null) {
      throw new NullPointerException();
    }
    try {
      JaxbUtil.marshal(objectFactory.createEvents(doc), f);
      return true;
    } catch (JAXBException e) {
      XmlPlugin
          .getDefault()
          .getLog()
          .log(
              new Status(IStatus.ERROR, XmlPlugin.PLUGIN_ID,
                  "Unable to save data.", e));
      return false;
    }
  }

}
