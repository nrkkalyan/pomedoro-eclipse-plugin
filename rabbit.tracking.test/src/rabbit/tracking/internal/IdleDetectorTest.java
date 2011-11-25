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
package rabbit.tracking.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @see IdleDetector
 */
public class IdleDetectorTest {

  /**
   * Helper observer for testing.
   */
  private static class ObserverTester implements Observer {
    private int activeCount = 0;
    private int inactiveCount = 0;

    @Override
    public synchronized void update(Observable o, Object arg) {
      IdleDetector detect = (IdleDetector) o;
      if (detect.isUserActive()) {
        activeCount++;
      } else {
        inactiveCount++;
      }
    }
  }

  private Display display;
  private Shell shell;

  @After
  public void after() {
    shell.dispose();
    display.dispose();
  }

  @Before
  public void before() {
    display = new Display();
    shell = new Shell(display);
  }

  @Test
  public void shouldBeAbleToDetectThatTheUserHasReturnedToActiveByPressingAKey() throws Exception {
    long idleInterval = 50;
    long runDelay = 10;
    IdleDetector d = create(display, idleInterval, runDelay);
    d.setRunning(true);

    Thread.sleep((idleInterval + runDelay) * 2);
    assertThat(d.isUserActive(), is(false));

    shell.notifyListeners(SWT.KeyDown, new Event());
    assertThat(d.isUserActive(), is(true));
  }

  @Test
  public void observersShouldBeNotifiedWhenTheUserReturnsToActiveByPressingAKey() throws Exception {
    long idleInterval = 100;
    long runDelay = 10;
    IdleDetector d = new IdleDetector(display, idleInterval, runDelay);

    ObserverTester ob = new ObserverTester();
    d.addObserver(ob);
    d.setRunning(true);

    Thread.sleep(idleInterval + (runDelay * 2));
    Thread.sleep(idleInterval + (runDelay * 2));
    assertFalse(d.isUserActive());

    shell.notifyListeners(SWT.KeyDown, new Event());
    shell.notifyListeners(SWT.KeyDown, new Event());

    assertEquals(1, ob.inactiveCount);
    assertEquals(1, ob.activeCount);
  }

  @Test
  public void shouldBeAbleToDetectThatTheUserHasReturnedToActiveByClickingTheMouse() throws Exception {
    long idleInterval = 500;
    long runDelay = 10;
    IdleDetector d = new IdleDetector(display, idleInterval, runDelay);
    d.setRunning(true);

    Thread.sleep(idleInterval + (runDelay * 2));
    assertFalse(d.isUserActive());

    shell.notifyListeners(SWT.MouseDown, new Event());
    assertTrue(d.isUserActive());
  }

  @Test
  public void observersShouldBeNotifiedWhenTheUserReturnsToActiveByClickingTheMouse() throws Exception {
    long idleInterval = 500;
    long runDelay = 10;
    IdleDetector d = new IdleDetector(display, idleInterval, runDelay);

    ObserverTester ob = new ObserverTester();
    d.addObserver(ob);
    d.setRunning(true);

    Thread.sleep(idleInterval + (runDelay * 2));
    Thread.sleep(idleInterval + (runDelay * 2));
    assertFalse(d.isUserActive());

    shell.notifyListeners(SWT.MouseDown, new Event());
    shell.notifyListeners(SWT.MouseDown, new Event());
    shell.notifyListeners(SWT.MouseDown, new Event());

    assertEquals(1, ob.inactiveCount);
    assertEquals(1, ob.activeCount);
  }
  
  @Test
  public void observersShouldNotBeNotifiedWhenTheUserIsActive() throws Exception {
    long idleInterval = 50;
    long runDelay = 10;
    IdleDetector d = new IdleDetector(display, idleInterval, runDelay);

    ObserverTester ob = new ObserverTester();
    d.addObserver(ob);
    d.setRunning(true);

    Thread.sleep(idleInterval / 2);
    assertTrue(d.isUserActive());
    shell.notifyListeners(SWT.MouseDown, new Event());

    Thread.sleep(idleInterval / 2);
    assertTrue(d.isUserActive());
    shell.notifyListeners(SWT.MouseDown, new Event());

    Thread.sleep(idleInterval / 2);
    assertTrue(d.isUserActive());
    shell.notifyListeners(SWT.MouseDown, new Event());

    assertEquals(0, ob.inactiveCount);
    assertEquals(0, ob.activeCount);
  }
  

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADisplay() {
    new IdleDetector(null, 10, 10);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfConstructedWithANegativeDelay() {
    new IdleDetector(display, 10, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfConstructedWithANegativeInterval() {
    new IdleDetector(display, -1, 10);
  }

  @Test
  public void shouldDoNothingAfterTheDisplayHasBeenDisposed() {
    IdleDetector d = new IdleDetector(display, 10, 10);
    display.dispose();

    try {
      d.setRunning(true);
      d.setRunning(false);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void shouldReturnTheDisplay() {
    Display actualDisplay = create(display, 1, 1).getDisplay();
    assertThat(actualDisplay, sameInstance(display));
  }

  @Test
  public void shouldReturnTheIdleInterval() {
    long expected = 1936l;
    long actual =  create(display, expected, 1).getIdleInterval();
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldReturnTheDelay() {
    long expectedDelay = 1231;
    long actualDelay = create(display, 101010, expectedDelay).getRunDelay();
    assertThat(actualDelay, is(expectedDelay));
  }

  @Test
  public void shouldNotBeRunningWhenFirstConstructed() {
    assertThat(create(display, 10, 10).isRunning(), is(false));
  }
  
  private IdleDetector create(Display display, long idleTime, long delay) {
    return new IdleDetector(display, idleTime, delay);
  }

  @Test
  public void theUserShouldBeActiveWhenTheIdleDetectorIsFirstConstructed() {
    assertTrue(new IdleDetector(display, 10, 10).isUserActive());
  }

  @Test
  public void shouldNotNotifyAnyObserversIfNotEnabled() throws Exception {
    // IdleDetector is not running, so no observers should be notified
    long idleInterval = 500;
    long runDelay = 10;
    IdleDetector d = new IdleDetector(display, idleInterval, runDelay);

    ObserverTester ob = new ObserverTester();
    d.addObserver(ob);
    d.setRunning(false);

    Thread.sleep(idleInterval + (runDelay * 2));
    Thread.sleep(idleInterval + (runDelay * 2));

    shell.notifyListeners(SWT.KeyDown, new Event());
    shell.notifyListeners(SWT.MouseDown, new Event());

    assertEquals(0, ob.inactiveCount);
    assertEquals(0, ob.activeCount);
  }

  @Test
  public void testSetRunning() {
    IdleDetector d = new IdleDetector(display, 100, 100);
    assertFalse(d.isRunning());

    try {
      d.setRunning(false);
      d.setRunning(false);
    } catch (Exception e) {
      fail();
    }

    try {
      d.setRunning(true);
      d.setRunning(true);
    } catch (Exception e) {
      fail();
    }

    d.setRunning(true);
    assertTrue(d.isRunning());
    try {
      assertFalse(getTimer(d).isShutdown());
    } catch (Exception e) {
      fail();
    }

    d.setRunning(false);
    assertFalse(d.isRunning());
    try {
      assertTrue(getTimer(d).isShutdown());
    } catch (Exception e) {
      fail();
    }
  }

  private ScheduledThreadPoolExecutor getTimer(IdleDetector d) throws Exception {
    Field field = d.getClass().getDeclaredField("timer");
    field.setAccessible(true);
    return (ScheduledThreadPoolExecutor) field.get(d);
  }
}
