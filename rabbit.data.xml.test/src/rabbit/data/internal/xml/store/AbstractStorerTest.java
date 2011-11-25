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

import static rabbit.data.internal.xml.DatatypeUtil.toXmlDate;

import rabbit.data.internal.xml.DatatypeUtil;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.XmlPlugin;
import rabbit.data.internal.xml.convert.IConverter;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.schema.events.EventGroupType;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.store.model.DiscreteEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.runtime.IPath;
import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @see AbstractStorer
 */
public abstract class AbstractStorerTest<E extends DiscreteEvent, T, S extends EventGroupType> {

  /**
   * Location of the storage path, to be restored after the tests.
   */
  private static IPath originalStorageLocation;

  @AfterClass
  public static void afterClass() {
    XmlPlugin.getDefault().setStoragePathRoot(originalStorageLocation.toFile());
  }

  @BeforeClass
  public static void beforeClass() {
    originalStorageLocation = XmlPlugin.getDefault().getStoragePathRoot();
    XmlPlugin.getDefault().setStoragePathRoot(
        originalStorageLocation.append("Tests").toFile());
  }

  private AbstractStorer<E, T, S> storer = createStorer();

  @Before
  public void before() throws Exception {
    getDataField(storer).clear();
  }

  @Test
  public void testCommit() throws Exception {
    E event = createEvent(new DateTime());
    storer.insert(event);

    File file = getDataStore(storer).getDataFile(event.getTime().toLocalDate());
    if (file.exists() && !file.delete())
      fail("File must be deleted before test can continue");

    storer.commit();

    List<S> data = getCategories(storer, getDataStore(storer).read(file));
    assertEquals(1, data.size());
    assertEquals(toXmlDate(event.getTime()), data.get(0).getDate());

    List<T> elements = storer.getElements(data.get(0));
    assertEquals(1, elements.size());
    T element = elements.get(0);
    assertTrue(equal(getConverter(storer).convert(event), element));
  }

  @Test
  public void testCommit_emptyDataAfterward() throws Exception {
    E event = createEvent(new DateTime());
    storer.insert(event);
    assertFalse(getDataField(storer).isEmpty());
    storer.commit();
    assertTrue(getDataField(storer).isEmpty());
  }

  @Test
  public void testGetCategories() throws Exception {
    assertNotNull(getCategories(storer, new EventListType()));
  }

  @Test
  public void testGetConverter() throws Exception {
    assertNotNull(getConverter(storer));
  }

  @Test
  public void testGetDataStore() throws Exception {
    assertNotNull(getDataStore(storer));
  }

  @Test
  public void testInsert_singleElement() throws Exception {
    E event = createEvent(new DateTime());
    storer.insert(event);

    Collection<S> categories = getDataField(storer);
    assertEquals(1, categories.size());
    S category = categories.iterator().next();
    assertEquals(toXmlDate(event.getTime()), category.getDate());

    List<T> elements = storer.getElements(category);
    assertEquals(1, elements.size());
    T element = elements.iterator().next();
    assertTrue(equal(getConverter(storer).convert(event), element));
  }

  @Test
  public void testInsert_withConvertableElements() throws Exception {
    E event1 = createEvent(new DateTime());
    E event2 = createEvent(event1.getTime());
    storer.insert(event1);
    storer.insert(event2);

    Collection<S> categories = getDataField(storer);
    assertEquals(1, categories.size());
    S category = categories.iterator().next();
    assertEquals(toXmlDate(event1.getTime()), category.getDate());

    List<T> elements = storer.getElements(category);
    T t1 = getConverter(storer).convert(event1);
    T t2 = getConverter(storer).convert(event2);
    IMerger<T> merger = storer.getMerger();
    if (merger != null) { // Elements are mergeable
      assertEquals(1, elements.size());

      T element = elements.iterator().next();
      assertTrue(equal(merger.merge(t1, t2), element));

    } else { // Elements are not mergeable
      assertEquals(2, elements.size());

      Iterator<T> iterator = elements.iterator();
      // It doesn't matter what order we check, they are all equal, because
      // the two events are created equally:
      assertTrue(equal(t1, iterator.next()));
      assertTrue(equal(t2, iterator.next()));
    }
  }

  @Test
  public void testInsert_withElementsInDifferentDates() throws Exception {
    E event1 = createEvent(new DateTime().withDayOfYear(1));
    E event2 = createEventDiff(event1.getTime().plusDays(1));
    storer.insert(event1);
    storer.insert(event2);

    Collection<S> categories = getDataField(storer);
    assertEquals(2, categories.size());

    Iterator<S> iterator = categories.iterator();
    S category1 = iterator.next();
    S category2 = iterator.next();

    // Swap the categories if neccessary so that category1 contains event1 etc.
    if (category1.getDate().equals(toXmlDate(event2.getTime()))) {
      S tmp = category1;
      category1 = category2;
      category2 = tmp;
    }
    assertEquals(toXmlDate(event1.getTime()), category1.getDate());
    assertEquals(toXmlDate(event2.getTime()), category2.getDate());

    List<T> elements = storer.getElements(category1);
    assertEquals(1, elements.size());
    assertTrue(equal(getConverter(storer).convert(event1), elements.get(0)));

    elements = storer.getElements(category2);
    assertEquals(1, elements.size());
    assertTrue(equal(getConverter(storer).convert(event2), elements.get(0)));
  }

  @Test
  public void testInsert_withElementsInDifferentMonths() throws Exception {
    E event1 = createEvent(new DateTime());
    E event2 = createEvent(event1.getTime().plusMonths(1));
    storer.insert(event1);
    storer.insert(event2);

    // Now data about event1 should be commit, so only data about event2 is in
    // memory.

    Collection<S> categories = getDataField(storer);
    assertEquals(1, categories.size());
    S category = categories.iterator().next();
    assertEquals(toXmlDate(event2.getTime()), category.getDate());

    List<T> elements = storer.getElements(category);
    assertEquals(1, elements.size());
    T element = elements.iterator().next();
    assertTrue(equal(getConverter(storer).convert(event2), element));
  }

  @Test
  public void testInsert_withNonConvertableElements() throws Exception {
    E event1 = createEvent(new DateTime());
    E event2 = createEventDiff(event1.getTime());
    storer.insert(event1);
    storer.insert(event2);

    Collection<S> categories = getDataField(storer);
    assertEquals(1, categories.size());
    S category = categories.iterator().next();
    assertEquals(toXmlDate(event1.getTime()), category.getDate());

    List<T> elements = storer.getElements(category);
    assertEquals(2, elements.size());

    T t1 = getConverter(storer).convert(event1);
    T t2 = getConverter(storer).convert(event2);
    assertTrue(equal(t1, elements.get(0)));
    assertTrue(equal(t2, elements.get(1)));
  }

  @Test
  public void testNewCateogry() throws Exception {
    XMLGregorianCalendar date = newCalendar();
    S category = storer.newCategory(date);
    assertNotNull(category);
    assertEquals(date, category.getDate());
  }

  /**
   * Creates an event for testing. Subsequence calls to this method should
   * return equal objects.
   */
  protected abstract E createEvent(DateTime dateTime) throws Exception;

  /**
   * Creates an event for testing. The return event must be different to
   * {@link #createEvent(DateTime)} (Non mergeable). Subsequence calls to this
   * method should return equal objects.
   */
  protected abstract E createEventDiff(DateTime dateTime) throws Exception;

  /**
   * Creates a storer for testing.
   */
  protected abstract AbstractStorer<E, T, S> createStorer();

  /**
   * Checks whether the two objects have the exact same properties.
   */
  protected abstract boolean equal(T t1, T t2);

  /**
   * Calls the protected method
   * {@code AbstractStorer.getXmlTypeCategories(EventListType)}.
   */
  @SuppressWarnings("unchecked")
  protected List<S> getCategories(AbstractStorer<E, T, S> storer,
      EventListType events) throws Exception {

    Method method = AbstractStorer.class.getDeclaredMethod("getCategories",
        EventListType.class);
    method.setAccessible(true);
    return (List<S>) method.invoke(storer, events);
  }

  /**
   * Calls the protected method AbstractStorer.getConverter()
   */
  @SuppressWarnings("unchecked")
  protected IConverter<E, T> getConverter(AbstractStorer<E, T, S> storer)
      throws Exception {
    Method method = AbstractStorer.class.getDeclaredMethod("getConverter");
    method.setAccessible(true);
    return (IConverter<E, T>) method.invoke(storer);
  }

  @SuppressWarnings("unchecked")
  protected Collection<S> getDataField(AbstractStorer<E, T, S> s)
      throws Exception {
    Field f = AbstractStorer.class.getDeclaredField("data");
    f.setAccessible(true);
    return (Collection<S>) f.get(s);
  }

  /**
   * Calls the protected method {@code AbstractStorer.getDataStore()}.
   */
  protected IDataStore getDataStore(AbstractStorer<E, T, S> storer)
      throws Exception {
    Method method = AbstractStorer.class.getDeclaredMethod("getDataStore");
    method.setAccessible(true);
    return (IDataStore) method.invoke(storer);
  }

  private XMLGregorianCalendar newCalendar() {
    return DatatypeUtil.toXmlDate(new DateTime());
  }
}
