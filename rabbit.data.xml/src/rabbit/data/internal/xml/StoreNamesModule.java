package rabbit.data.internal.xml;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

/**
 * Binds data stores to store names.
 */
public class StoreNamesModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(IDataStore.class)
        .annotatedWith(Names.named(StoreNames.COMMAND_STORE))
        .toInstance(DataStore.COMMAND_STORE);
    bind(IDataStore.class)
        .annotatedWith(Names.named(StoreNames.FILE_STORE))
        .toInstance(DataStore.FILE_STORE);
    bind(IDataStore.class)
        .annotatedWith(Names.named(StoreNames.JAVA_STORE))
        .toInstance(DataStore.JAVA_STORE);
    bind(IDataStore.class)
        .annotatedWith(Names.named(StoreNames.LAUNCH_STORE))
        .toInstance(DataStore.LAUNCH_STORE);
    bind(IDataStore.class)
        .annotatedWith(Names.named(StoreNames.PART_STORE))
        .toInstance(DataStore.PART_STORE);
    bind(IDataStore.class)
        .annotatedWith(Names.named(StoreNames.PERSPECTIVE_STORE))
        .toInstance(DataStore.PERSPECTIVE_STORE);
    bind(IDataStore.class)
        .annotatedWith(Names.named(StoreNames.SESSION_STORE))
        .toInstance(DataStore.SESSION_STORE);
    bind(IDataStore.class)
        .annotatedWith(Names.named(StoreNames.TASK_STORE))
        .toInstance(DataStore.TASK_STORE);
  }

}
