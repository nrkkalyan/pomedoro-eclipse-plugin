package rabbit.data.internal.xml;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

/**
 * Tests for {@link StoreNamesModule}.
 */
@RunWith(Parameterized.class)
public class StoreNamesModuleTest {

  @Parameters
  public static Collection<Object[]> data() {
    //@formatter:off
    return Arrays.asList(new Object[][]{
        {StoreNames.COMMAND_STORE,      DataStore.COMMAND_STORE},
        {StoreNames.FILE_STORE,         DataStore.FILE_STORE},
        {StoreNames.JAVA_STORE,         DataStore.JAVA_STORE},
        {StoreNames.LAUNCH_STORE,       DataStore.LAUNCH_STORE},
        {StoreNames.PART_STORE,         DataStore.PART_STORE},
        {StoreNames.PERSPECTIVE_STORE,  DataStore.PERSPECTIVE_STORE},
        {StoreNames.SESSION_STORE,      DataStore.SESSION_STORE},
        {StoreNames.TASK_STORE,         DataStore.TASK_STORE},
    });
    //@formatter:on
  }

  private Injector injector;
  private String storeName;
  private IDataStore store;

  public StoreNamesModuleTest(String storeName, DataStore store) {
    this.storeName = storeName;
    this.store = store;
    injector = Guice.createInjector(new StoreNamesModule());
  }

  @Test
  public void shouldBindTheCorrectStore() {
    assertThat(injector.getInstance(Key.get(IDataStore.class, Names.named(storeName))), is(store));
  }
}
