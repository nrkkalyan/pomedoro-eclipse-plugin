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

import rabbit.data.internal.xml.schema.events.ObjectFactory;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItems;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.MutableDateTime;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @see DataStore
 */
public class DataStoreTest {

  private DataStore store = DataStore.PART_STORE;

  @Test
  public void testGetDataFile() {
    assertNotNull(store.getDataFile(new LocalDate()));
  }

  /**
   * {@link IDataStore#getDataFiles(LocalDate, LocalDate)}
   */
  @Test
  public void testGetDataFiles_startEnd() throws IOException {

    LocalDate lowerBound = new LocalDate(1, 1, 10);
    LocalDate upperBound = new LocalDate(3, 1, 1);

    LocalDate insideLowerBound = lowerBound.plusMonths(1);
    LocalDate insideUpperBound = upperBound.minusMonths(1);

    Set<File> files = new HashSet<File>();
    files.add(store.getDataFile(lowerBound));
    files.add(store.getDataFile(upperBound));
    files.add(store.getDataFile(insideLowerBound));
    files.add(store.getDataFile(insideUpperBound));
    for (File f : files) {
      if (!f.exists() && !f.createNewFile()) {
        throw new RuntimeException();
      }
    }

    List<File> returnedFiles = store.getDataFiles(lowerBound, upperBound);
    assertEquals(files.size(), returnedFiles.size());
    for (File f : returnedFiles) {
      assertTrue(files.contains(f));
    }
    assertEquals(0, store.getDataFiles(upperBound, lowerBound).size());
    
    for (File f : files) {
      FileUtils.forceDelete(f);
    }
    assertEquals(0, store.getDataFiles(lowerBound, upperBound).size());

    // Temporary test for testing files across multiple workspaces:
    LocalDate end = new LocalDate();
    LocalDate start = end.minusYears(1);

    List<File> result = new ArrayList<File>();
    IPath[] storagePaths = XmlPlugin.getDefault().getStoragePaths();
    MutableDateTime date = start.toDateTime(new LocalTime(0, 0, 0)).toMutableDateTime();
    while (new LocalDate(date.toInstant().getMillis()).compareTo(end) <= 0) {

      for (IPath path : storagePaths) {
        File f = store.getDataFile(new LocalDate(date.getMillis()), path);
        if (f.exists()) {
          result.add(f);
        }
      }
      date.addMonths(1);
    }
    assertEquals(result.size(), store.getDataFiles(start, end).size());
  }

  /**
   * @see IDataStore#getDataFiles(LocalDate, LocalDate, IPath)
   */
  @Test
  public void testGetDataFiles_startEndLocation() throws Exception {

    LocalDate lowerBound = new LocalDate(1, 1, 10);
    LocalDate upperBound = new LocalDate(3, 1, 1);

    LocalDate insideLowerBound = lowerBound.plusMonths(1);
    LocalDate insideUpperBound = upperBound.minusMonths(1);
    
    IPath path = new Path(System.getProperty("java.io.tmpdir"));

    Set<File> expectedFiles = new HashSet<File>();
    expectedFiles.add(store.getDataFile(lowerBound, path));
    expectedFiles.add(store.getDataFile(upperBound, path));
    expectedFiles.add(store.getDataFile(insideLowerBound, path));
    expectedFiles.add(store.getDataFile(insideUpperBound, path));
    for (File f : expectedFiles) {
      if (!f.exists() && !f.createNewFile()) {
          throw new RuntimeException();
      }
    }

    List<File> actualFiles = store.getDataFiles(lowerBound, upperBound, path);
    assertThat(actualFiles.size(), equalTo(expectedFiles.size()));
    assertThat(actualFiles, hasItems(expectedFiles.toArray(new File[0])));
    assertTrue(store.getDataFiles(upperBound, lowerBound, path).isEmpty());
    
    for (File f : expectedFiles) {
      FileUtils.forceDelete(f);
    }
    assertTrue(store.getDataFiles(lowerBound, upperBound, path).isEmpty());
  }
  
  @Test
  public void testGetStorageLocation() {
    assertNotNull(store.getStorageLocation());
  }

  @Test
  public void testRead() throws IOException {

    LocalDate cal = new LocalDate(1, 1, 1);
    File f = store.getDataFile(cal);

    if (f.exists()) {
      assertNotNull(store.read(f));

    } else {
      if (!f.createNewFile()) {
        throw new RuntimeException();
      }

      assertNotNull(store.read(f));
      if (!f.delete()) {
        System.err.println("File is not deleted.");
      }
    }
  }

  @Test
  public void testWrite() {

    File f = new File(System.getProperty("user.home") + File.separator
        + "tmpTestFile.xml");
    assertFalse(f.exists());

    store.write(new ObjectFactory().createEventListType(), f);
    assertTrue(f.exists());
    if (!f.delete()) {
      System.err.println("File is not deleted.");
    }
  }

  @Test(expected = NullPointerException.class)
  public void testWrite_dataNull() {
    store.write(null, new File("/"));
  }

  @Test(expected = NullPointerException.class)
  public void testWrite_fileNull() {
    store.write(new ObjectFactory().createEventListType(), null);
  }
}
