package rabbit.data.internal.xml.access;

import rabbit.data.access.model.ISessionData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.access.model.SessionData;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.StoreNames;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.SessionEventListType;
import rabbit.data.internal.xml.schema.events.SessionEventType;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Accesses session data events.
 */
public class SessionDataAccessor extends
    AbstractAccessor<ISessionData, SessionEventType, SessionEventListType> {

  /**
   * Constructor.
   * 
   * @param store The data store to get the data from.
   * @throws NullPointerException If argument is null.
   */
  @Inject
  SessionDataAccessor(@Named(StoreNames.SESSION_STORE) IDataStore store) {
    super(store);
  }

  @Override
  protected ISessionData createDataNode(LocalDate cal, WorkspaceStorage ws,
      SessionEventType type) throws Exception {
    return new SessionData(cal, ws, new Duration(type.getDuration()));
  }

  @Override
  protected Collection<SessionEventListType> getCategories(EventListType list) {
    return list.getSessionEvents();
  }

  @Override
  protected Collection<SessionEventType> getElements(SessionEventListType list) {
    return list.getSessionEvent();
  }
}
