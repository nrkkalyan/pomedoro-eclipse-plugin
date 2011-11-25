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

import static rabbit.data.internal.xml.DatatypeUtil.isSameDate;
import static rabbit.data.internal.xml.DatatypeUtil.isSameMonthInYear;
import static rabbit.data.internal.xml.DatatypeUtil.toXmlDate;

import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.XmlPlugin;
import rabbit.data.internal.xml.convert.IConverter;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.merge.Mergers;
import rabbit.data.internal.xml.schema.events.EventGroupType;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.ObjectFactory;
import rabbit.data.store.IStorer;
import rabbit.data.store.model.DiscreteEvent;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * This abstract class is designed specifically for the XML schema. This class
 * contains implementations for common behaviors.
 * 
 * @param <E> The event type.
 * @param <T> The corresponding XML object type of the event type, this is the
 *          form when the event is stored in XML.
 * @param <S> A category type that holds the XML types according to event date.
 */
public abstract class AbstractStorer<E extends DiscreteEvent, T, S extends EventGroupType>
    implements IStorer<E> {

  /** Factory object for creating XML schema Java types. */
  protected final ObjectFactory objectFactory;

  /** Data in memory, not yet saved. */
  private final Set<S> data;

  /** The current month. */
  private LocalDate currentMonth;

  /** Converter for converting an event to its corresponding XML type. */
  private final IConverter<E, T> converter;

  /** Merger for merging two XML types. */
  private final IMerger<T> merger;

  /** The data store to store the data to. */
  private final IDataStore store;

  /**
   * Constructor.
   * 
   * @param converter Converter for converting an event to its corresponding XML
   *          type.
   * @param merger Merger for merging two XML types.
   * @param store The data store to store the data to.
   */
  protected AbstractStorer(IConverter<E, T> converter, IMerger<T> merger,
      IDataStore store) {
    this.converter = checkNotNull(converter);
    this.merger = checkNotNull(merger);
    this.store = checkNotNull(store);
    data = new LinkedHashSet<S>();
    currentMonth = new LocalDate();
    objectFactory = new ObjectFactory();
  }

  @Override
  public void commit() {
    if (data.isEmpty())
      return;

    File f = getDataStore().getDataFile(currentMonth);
    EventListType events = getDataStore().read(f);
    List<S> categories = getCategories(events);

    IMerger<T> merger = getMerger();
    for (S mergeFrom : data) {

      boolean done = false;
      for (S mergeTo : categories) {
        if (mergeFrom.getDate().equals(mergeTo.getDate())) {
          if (merger != null)
            Mergers.merge(merger, getElements(mergeTo), getElements(mergeFrom));
          else
            getElements(mergeTo).addAll(getElements(mergeFrom));

          done = true;
          break;
        }
      }

      if (!done) {
        categories.add(mergeFrom);
      }
    }

    if (!getDataStore().write(events, f)) {
      XmlPlugin.getDefault().getLog().log(
          new Status(IStatus.ERROR, XmlPlugin.PLUGIN_ID, "Unable to save data."));
    }
    data.clear();
  }

  @Override
  public void insert(Collection<? extends E> collection) {
    for (E elements : collection)
      insert(elements);
  }

  @Override
  public void insert(E event) {

    DateTime time = event.getTime();
    if (!isSameMonthInYear(event.getTime(), currentMonth)) {
      commit();
      currentMonth = time.toLocalDate();
    }

    IMerger<T> merger = getMerger();
    T element = getConverter().convert(event);
    for (S category : data) {
      if (isSameDate(event.getTime(), category.getDate())) {

        if (merger != null)
          Mergers.merge(merger, getElements(category), element);
        else
          getElements(category).add(element);

        return;
      }
    }

    S category = newCategory(toXmlDate(event.getTime()));
    getElements(category).add(element);
    data.add(category);
  }

  /**
   * Gets the XML categories for grouping the event objects by date in a
   * {@link EventListType}.
   * 
   * @param events The root element.
   * @return A list of groups.
   */
  protected abstract List<S> getCategories(EventListType events);

  /**
   * Gets a converter for converting events to XML types.
   * 
   * @return A converter, must not be null.
   */
  protected final IConverter<E, T> getConverter() {
    return converter;
  }

  /**
   * Gets the data store.
   * 
   * @return The data store.
   */
  protected final IDataStore getDataStore() {
    return store;
  }

  /**
   * Gets the XML elements from the given group.
   * 
   * @param list The group holding the XML types.
   * @return A list of XML elements.
   */
  protected abstract List<T> getElements(S list);

  /**
   * Gets the merger for merging identical elements.
   * 
   * @return An {@linkplain IMerger}, or null if all elements are to be treated
   *         uniquely.
   */
  protected final IMerger<T> getMerger() {
    return merger;
  }

  /**
   * Creates a new category from the given date.
   * 
   * @param date The date.
   * @return A new category configured with the date.
   */
  protected abstract S newCategory(XMLGregorianCalendar date);
}
