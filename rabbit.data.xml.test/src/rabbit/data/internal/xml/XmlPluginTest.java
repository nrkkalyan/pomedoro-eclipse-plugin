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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

/**
 * Test for {@link XmlPlugin}
 */
public class XmlPluginTest {

  private static XmlPlugin plugin = XmlPlugin.getDefault();

  @Test
  public void getStoragePropertyStringShouldReturnTheCorrectString() {
    String expected = "_ws_ccc";
    IPath path = new Path("/a/b/ccc");
    assertThat(plugin.getStoragePropertyString(path), equalTo(expected));
  }

  @Test
  public void getStoragePathShouldReturnTheCurrentStoragePath() {
    assertNotNull(plugin.getStoragePath());
    assertTrue(plugin.getStoragePath().toFile().exists());
    assertTrue(plugin.getStoragePath().toFile().isDirectory());
  }

  @Test
  public void getStoragePathRootShouldReturnTheParentOfTheStoragePath() {
    assertThat(plugin.getStoragePathRoot(),
        equalTo(plugin.getStoragePath().removeLastSegments(1)));
  }

  @Test
  public void thePluginIdShouldEqualToTheBundleSymbolicName() {
    assertEquals(XmlPlugin.PLUGIN_ID, plugin.getBundle().getSymbolicName());
  }

  @Test
  public void setStoragePathRootTests() throws IOException {
    IPath oldPath = plugin.getStoragePathRoot();
    try {
      IPath path = oldPath.append(System.currentTimeMillis() + "");

      File file = path.toFile();

      // File not exist, should return false:
      assertFalse(file.exists());
      assertFalse(plugin.setStoragePathRoot(file));

      // File exists, readable, writable, should return true:
      assertTrue(file.mkdirs());
      assertTrue(file.setReadable(true));
      assertTrue(file.setWritable(true));
      assertTrue(plugin.setStoragePathRoot(file));
    } finally {
      plugin.setStoragePathRoot(oldPath.toFile());
    }
  }

  @Test
  public void testStoragePaths() {
    Collection<IPath> paths = newHashSet(plugin.getStoragePaths());
    assertThat(paths, hasItem(plugin.getStoragePath()));

    File root = plugin.getStoragePathRoot().toFile();
    Set<File> files = Sets.newHashSet(root.listFiles(new FileFilter() {
      @Override
      public boolean accept(File file) {
        return file.isDirectory();
      }
    }));
    files.add(plugin.getStoragePath().toFile());
    paths = Collections2.transform(files, new Function<File, IPath>() {
      @Override
      public IPath apply(File input) {
        return Path.fromOSString(input.getAbsolutePath());
      }
    });

    assertThat(newHashSet(plugin.getStoragePaths()), equalTo(newHashSet(paths)));
  }
}
