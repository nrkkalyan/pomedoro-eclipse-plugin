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

import static rabbit.data.internal.xml.DatatypeUtil.toXmlDate;

import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.xml.XmlPlugin;
import rabbit.data.internal.xml.access.AbstractAccessor;
import rabbit.data.internal.xml.schema.events.EventGroupType;
import rabbit.data.internal.xml.schema.events.EventListType;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * @see AbstractAccessor
 */
public abstract class AbstractAccessorTest2<T, E, S extends EventGroupType> {

  /**
   * The original storage root, to be restored after the tests.
   */
  private static final File ORIGINAL_ROOT = XmlPlugin.getDefault().getStoragePathRoot().toFile();

  /**
   * The storage root for the duration of the tests.
   */
  public static File testRoot;

  @AfterClass
  public static void afterClass() {
    XmlPlugin.getDefault().setStoragePathRoot(ORIGINAL_ROOT);
    FileUtils.deleteQuietly(testRoot);
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    testRoot = new File(ORIGINAL_ROOT, nowString());
    assertTrue(testRoot.mkdir());
    assertTrue(XmlPlugin.getDefault().setStoragePathRoot(testRoot));
  }

  /**
   * @return A string representing System.nanoTime().
   */
  private static String nowString() {
    return String.valueOf(System.nanoTime());
  }

  protected AbstractAccessor<T, E, S> accessor;

  @Before
  public void before() throws Exception {
    accessor = create();
  }

  @Test
  public void getCategoriesShouldReturnTheCorrectCategories() {
    EventListType eventList = new EventListType();
    S category = createCategory();
    accessor.getCategories(eventList).add(category);
    assertThat(accessor.getCategories(eventList), hasItem(category));
    assertThat(accessor.getCategories(eventList).size(), is(1));
  }

  @Test
  public void getCategoriesShouldReturnTheCorrectCollection() {
    EventListType t = new EventListType();
    assertSame(getCategories(t), accessor.getCategories(t));
  }

  @Test
  public void getDataShouldNotReturnTheDataOutsideOfTheDates() throws Exception {
    LocalDate date = new LocalDate();
    writeData(date);
    Collection<T> data = accessor.getData(date.plusDays(1), date.plusDays(2));
    assertThat(data.size(), is(0));
  }

  @Test
  public void getDataShouldReturnAnEmptyCollectionIfNoData() {
    LocalDate start = new LocalDate().plusDays(100);
    LocalDate end = start.plusDays(2);
    assertThat(accessor.getData(start, end), is(notNullValue()));
    assertThat(accessor.getData(start, end).isEmpty(), is(true));
  }

  @Test
  public void getDataShouldReturnTheCorrectDataForMultipleDays() {
    LocalDate start = new LocalDate(2010, 1, 1);
    LocalDate end = new LocalDate(2010, 1, 3);
    E e1 = createElement();
    E e2 = createElement();

    S list1 = createCategory();
    list1.setDate(toXmlDate(start));
    getElements(list1).add(e1);

    S list2 = createCategory();
    list2.setDate(toXmlDate(end));
    getElements(list2).add(e2);

    EventListType events = new EventListType();
    accessor.getCategories(events).add(list1);
    accessor.getCategories(events).add(list2);

    File f = accessor.getDataStore().getDataFile(start);
    accessor.getDataStore().write(events, f);

    Collection<T> data = accessor.getData(start, end);
    assertThat(data.size(), is(2));
  }

  @Test
  public void getDataShouldReturnTheCorrectDataForTheSameDate()
      throws Exception {
    LocalDate date = new LocalDate();
    E element = createElement();
    File file = accessor.getDataStore().getDataFile(date);
    writeData(element, date, file);

    Collection<T> data = accessor.getData(date, date);
    assertThat(data.size(), is(1));
    WorkspaceStorage ws = new WorkspaceStorage(new Path(
        file.getParentFile().getAbsolutePath()), currentWorkspacePath());
    assertValues(element, date, ws, data.iterator().next());
  }

  @Test
  public void getDataShouldReturnTheCorrectDataStoredForAnotherWorkspace() {
    LocalDate date = new LocalDate().minusDays(1);
    E element = createElement();

    // Go to the root of the storage folder for current workspace:
    File file = accessor.getDataStore().getDataFile(date).getParentFile();
    // Go to the parent folder of the storage folder for current workspace:
    file = file.getParentFile();
    // Create another storage folder:
    file = new File(file, nowString());
    file.mkdir();
    // The new file for storing the data, need to keep the same name:
    file = new File(file, accessor.getDataStore().getDataFile(date).getName());

    writeData(element, date, file);

    Collection<T> data = accessor.getData(date, date);
    assertThat(data.size(), is(1));

    WorkspaceStorage ws = new WorkspaceStorage(
    // null because the custom storage folder is not mapped.
        new Path(file.getParentFile().getAbsolutePath()), null);
    assertValues(element, date, ws, data.iterator().next());
  }

  @Test(expected = NullPointerException.class)
  public void getDataShouldThrowNullPointerExceptionIfEndDateIsNull() {
    accessor.getData(new LocalDate(), null);
  }

  @Test(expected = NullPointerException.class)
  public void getDataShouldThrowNullPointerExceptionIfStartDateIsNull() {
    accessor.getData(null, new LocalDate());
  }

  @Test
  public void getDataStoreShouldNotReturnNull() throws Exception {
    assertThat(accessor.getDataStore(), is(notNullValue()));
  }

  @Test
  public void getElementsShouldReturnTheCorrectCollection() {
    S category = createCategory();
    assertSame(getElements(category), accessor.getElements(category));
  }

  @Test
  public void shouldCreateADataNodeCorrectly() throws Exception {
    LocalDate date = new LocalDate();
    WorkspaceStorage ws = new WorkspaceStorage(new Path(""), new Path("/a"));
    E expected = createElement();
    T actual = accessor.createDataNode(date, ws, expected);
    assertValues(expected, date, ws, actual);
  }

  /**
   * Checks the data for correctness.
   * 
   * @param expected The element containing the expected properties.
   * @param expectedDate The expected date for the data.
   * @param expectedWs The expected workspace.
   * @param actual The actual object to be checked for correctness.
   */
  protected abstract void assertValues(E expected, LocalDate expectedDate,
      WorkspaceStorage expectedWs, T actual);

  /**
   * Creates a subject for testing.
   */
  protected abstract AbstractAccessor<T, E, S> create();

  /**
   * @return A new category type.
   */
  protected abstract S createCategory();

  /**
   * Creates a new XML type with fields filled. Subsequence call to this method
   * should return an object with the same properties.
   */
  protected abstract E createElement();

  /**
   * @return The categories from the events.
   */
  protected abstract List<S> getCategories(EventListType events);

  /**
   * Gets the list of XML event types.
   */
  protected abstract List<E> getElements(S list);

  /**
   * @return The current workspace location.
   */
  private IPath currentWorkspacePath() {
    return ResourcesPlugin.getWorkspace().getRoot().getLocation();
  }

  /**
   * Writes the given element to file.
   * 
   * @param element The element.
   * @param date The date for the data.
   * @param file The file to write to.
   */
  private void writeData(E element, LocalDate date, File file) {
    // Add the element to the list:
    S category = createCategory();
    category.setDate(toXmlDate(date));
    getElements(category).add(element);

    // Create the document:
    EventListType events = new EventListType();
    accessor.getCategories(events).add(category);

    // Saves to file:
    accessor.getDataStore().write(events, file);
  }

  /**
   * Writes some data to file.
   */
  private void writeData(LocalDate date) {
    E element = createElement();
    File file = accessor.getDataStore().getDataFile(date);
    writeData(element, date, file);
  }
}
