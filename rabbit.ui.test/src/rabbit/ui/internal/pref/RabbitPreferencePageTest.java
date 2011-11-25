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
package rabbit.ui.internal.pref;

import rabbit.ui.internal.RabbitUI;
import rabbit.ui.internal.pref.RabbitPreferencePage;

import static org.junit.Assert.assertEquals;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotSpinner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for {@link RabbitPreferencePage}
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class RabbitPreferencePageTest {

  private static SWTWorkbenchBot bot;

  @BeforeClass
  public static void beforeClass() {
    bot = new SWTWorkbenchBot();
  }

  @Test
  public void testApply_period() {
    openRabbitPreferences();

    int numDays = 30;
    SWTBotSpinner datePeriod = bot.spinner();
    datePeriod.setSelection(numDays);
    bot.button("Apply").click();
    assertEquals(numDays, RabbitUI.getDefault().getDefaultDisplayDatePeriod());

    numDays = 7;
    datePeriod.setSelection(numDays);
    bot.button("Apply").click();
    assertEquals(numDays, RabbitUI.getDefault().getDefaultDisplayDatePeriod());

    bot.activeShell().close();
  }

  @Test
  public void testDefaults_period() {
    openRabbitPreferences();
    bot.spinner().setSelection(10);
    bot.button("Restore Defaults").click();
    assertEquals(7, bot.spinner().getSelection());
    bot.activeShell().close();
  }

  @Test
  public void testOk_period() {
    openRabbitPreferences();
    int numDays = 0;
    bot.spinner().setSelection(numDays);
    bot.button("OK").click();
    assertEquals(numDays, RabbitUI.getDefault().getDefaultDisplayDatePeriod());

    openRabbitPreferences();
    numDays = 9999;
    bot.spinner().setSelection(numDays);
    bot.button("OK").click(); // Closes the shell too.
    assertEquals(numDays, RabbitUI.getDefault().getDefaultDisplayDatePeriod());
  }

  @Test
  public void testPeriod() {
    openRabbitPreferences();
    assertEquals(bot.spinner().getSelection(), RabbitUI.getDefault()
        .getDefaultDisplayDatePeriod());
    assertEquals(0, bot.spinner().getMinimum());
    assertEquals(9999, bot.spinner().getMaximum());
    bot.activeShell().close();
  }

  private void openRabbitPreferences() {
    bot.menu("Window").menu("Preferences").click();
    bot.tree().select("Rabbit");
  }

}
