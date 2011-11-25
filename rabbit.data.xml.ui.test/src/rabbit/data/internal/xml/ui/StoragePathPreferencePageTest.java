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
package rabbit.data.internal.xml.ui;

import rabbit.data.internal.xml.XmlPlugin;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

@RunWith(SWTBotJunit4ClassRunner.class)
public class StoragePathPreferencePageTest {

  private static SWTWorkbenchBot bot;

  /** Path to restore after the test. */
  private static IPath storageLocation;

  @AfterClass
  public static void afterClass() {
    XmlPlugin.getDefault().setStoragePathRoot(storageLocation.toFile());
  }

  @BeforeClass
  public static void beforeClass() {
    bot = new SWTWorkbenchBot();
    storageLocation = XmlPlugin.getDefault().getStoragePathRoot();
  }

  @Test
  public void testApply_storage() {
    openRabbitPreferences();

    String storagePath = System.getProperty("user.home") + File.separator
        + "rabbit.testing";
    SWTBotText text = bot.textWithLabel("Location:");
    text.setText(storagePath);
    bot.button("Apply").click();
    try {
      // A message box may be popped up asking to move the old files.
      bot.button("No").click();
    } catch (WidgetNotFoundException e) {}
    assertEquals(storagePath, XmlPlugin.getDefault().getStoragePathRoot()
        .toOSString());

    storagePath = System.getProperty("user.home") + File.separator
        + System.nanoTime() + "";
    text.setText(storagePath);
    bot.button("Apply").click();
    try {
      // A message box may be popped up asking to move the old files.
      bot.button("No").click();
    } catch (WidgetNotFoundException e) {}
    assertEquals(storagePath, XmlPlugin.getDefault().getStoragePathRoot()
        .toOSString());

    bot.activeShell().close();
  }

  @Test
  public void testDefaults_storage() {
    openRabbitPreferences();
    SWTBotText text = bot.textWithLabel("Location:");
    text.setText("what?");
    bot.button("Restore Defaults").click();
    assertEquals(XmlPlugin.getDefault().getStoragePathRoot().toOSString(), text
        .getText());
    bot.activeShell().close();
  }

  @Test
  public void testOk_storage() {
    openRabbitPreferences();

    String storagePath = System.getProperty("user.home") + File.separator
        + System.nanoTime() + "";
    SWTBotText text = bot.textWithLabel("Location:");
    text.setText(storagePath);
    bot.button("OK").click();
    try {
      // A message box may be popped up asking to move the old files.
      bot.button("No").click();
    } catch (WidgetNotFoundException e) {}
    assertEquals(storagePath, XmlPlugin.getDefault().getStoragePathRoot()
        .toOSString());

    openRabbitPreferences();
    storagePath = System.getProperty("user.home") + File.separator
        + System.nanoTime() + "";
    text = bot.textWithLabel("Location:");
    text.setText(storagePath);
    bot.button("OK").click();
    try {
      // A message box may be popped up asking to move the old files.
      bot.button("No").click();
    } catch (WidgetNotFoundException e) {}
    assertEquals(storagePath, XmlPlugin.getDefault().getStoragePathRoot()
        .toOSString());
  }

  private void openRabbitPreferences() {
    bot.menu("Window").menu("Preferences").click();
    bot.tree().expandNode("Rabbit").select("Storage Location");
  }

}
