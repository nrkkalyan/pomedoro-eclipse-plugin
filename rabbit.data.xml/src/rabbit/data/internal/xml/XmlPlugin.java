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

import static com.google.common.collect.Sets.newHashSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;
import java.util.Set;

/**
 * Activator class for this plug-in.
 */
public class XmlPlugin extends AbstractUIPlugin {

  public static final String PLUGIN_ID = "rabbit.data.xml";

  private static XmlPlugin plugin;

  /**
   * The default location of the storage root.
   */
  private static final String DEFAULT_STORAGE_ROOT = FilenameUtils.concat(
      System.getProperty("user.home"), "Rabbit");

  /**
   * Constant string to use with a java.util.Properties to get/set the storage
   * root.
   */
  private static final String PROP_STORAGE_ROOT = "storage.root";

  public static XmlPlugin getDefault() {
    return plugin;
  }

  /** The settings. */
  private Properties properties = new Properties();

  public XmlPlugin() {}

  /**
   * Gets the full path to the storage location of this workspace. The returned
   * path should not be cached because it is changeable.
   * 
   * @return The full path to the storage location folder.
   */
  public IPath getStoragePath() {
    String workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation()
        .toOSString();
    workspace = workspace.replace(File.separatorChar, '.');
    workspace = workspace.replace(":", "");
    IPath path = getStoragePathRoot().append(workspace);
    File file = path.toFile();
    if (!file.exists() && !file.mkdirs()) {
      getLog().log(new Status(IStatus.ERROR, PLUGIN_ID,
          "Unable to create folder (" + file + ") for saving Rabbit's data!"));
    }
    return path;
  }

  /**
   * Gets the root of the storage location. The returned path should not be
   * cached because it's changeable.
   * 
   * @return The path to the root of the storage location.
   */
  public IPath getStoragePathRoot() {
    return Path.fromOSString(properties.getProperty(PROP_STORAGE_ROOT));
  }

  /**
   * Gets the paths to all the workspace storage locations for this plug-in.
   * Includes {@link #getStoragePath()}. The returned paths should not be cached
   * because they are changeable.
   * 
   * @return The paths to all the workspace storage locations
   */
  public IPath[] getStoragePaths() {
    IPath root = getStoragePathRoot();
    File rootFile = root.toFile();
    File[] files = rootFile.listFiles();
    if (files == null) {
      return new IPath[0];
    }

    Set<IPath> paths = newHashSet();
    for (File file : files) {
      if (file.isDirectory()) {
        paths.add(Path.fromOSString(file.getAbsolutePath()));
      }
    }
    paths.add(getStoragePath());

    return paths.toArray(new IPath[paths.size()]);
  }

  /**
   * Sets the storage root.
   * 
   * @param dir The new storage root.
   * @return true if the setting is applied; false if any of the followings is
   *         true:
   *         <ul>
   *         <li>The directory is null.</li>
   *         <li>The directory does not exist.</li>
   *         <li>The directory cannot be read from.</li>
   *         <li>The directory cannot be written to.</li>
   *         <li>If error occurs while saving the setting.</li>
   *         </ul>
   */
  public boolean setStoragePathRoot(File dir) {
    if (dir == null
        || !dir.isDirectory()
        || !dir.exists()
        || !dir.canRead()
        || !dir.canWrite()) {
      return false;
    }

    properties.setProperty(PROP_STORAGE_ROOT, dir.getAbsolutePath());
    return true;
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;

    Reader reader = null;
    try {
      reader = new BufferedReader(new FileReader(getPropertiesFile()));
      properties.load(reader);

      // If exceptions occur, just restore to the defaults:
    } catch (FileNotFoundException e) {
      System.err.println(getClass().getSimpleName() + " - start: "
          + e.getMessage());
    } catch (IOException e) {
      System.err.println(getClass().getSimpleName() + " - start: "
          + e.getMessage());
    } catch (IllegalArgumentException e) {
      System.err.println(getClass().getSimpleName() + " - start: "
          + e.getMessage());
    } finally {
      IOUtils.closeQuietly(reader);
      checkProperties(properties);
    }
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    Writer writer = null;
    try {
      checkProperties(properties);
      writer = new BufferedWriter(new FileWriter(getPropertiesFile()));
      String comment = String.format("This file contains configurations"
          + " for the Rabbit Eclipse plugin.%nPlease do not delete, otherwise"
          + " Rabbit will not work properly.");
      properties.store(writer, comment);

    } catch (IOException e) { // Nothing we can do
      System.err.println(getClass().getSimpleName() + " - stop: "
          + e.getMessage());
    } finally {
      IOUtils.closeQuietly(writer);
    }

    plugin = null;
    super.stop(context);
  }

  /**
   * Checks the properties to make sure all important properties are present, if
   * not defaults will be set.
   * @param prop The properties to check.
   */
  private void checkProperties(Properties prop) {
    if (prop.getProperty(PROP_STORAGE_ROOT) == null) {
      prop.setProperty(PROP_STORAGE_ROOT, DEFAULT_STORAGE_ROOT);
    }

    // Maps the name of the storage folder for this workspace with the actual
    // OS path:
    prop.setProperty(getStoragePropertyString(getStoragePath()),
        ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString());
  }

  /**
   * Gets the property string for the storage path.
   * @param storagePath the storage path.
   * @return a property string for getting/setting the property.
   */
  public String getStoragePropertyString(IPath storagePath) {
    return "_ws_" + storagePath.lastSegment();
  }

  private String getProperty(String key) {
    return properties.getProperty(key);
  }

  public IPath getWorkspacePath(IPath storagePath) {
    String workspacePathString = getProperty(getStoragePropertyString(storagePath));
    if (workspacePathString != null) {
      return new Path(workspacePathString);
    }
    return null;
  }

  /**
   * Gets the properties file for saving the storage root property.
   */
  private File getPropertiesFile() {
    String str = FilenameUtils.concat(System.getProperty("user.home"),
        ".rabbit.properties");
    return new File(str);
  }
}
