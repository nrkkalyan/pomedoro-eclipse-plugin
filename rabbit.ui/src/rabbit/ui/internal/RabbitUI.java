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
package rabbit.ui.internal;

import rabbit.ui.IPage;
import rabbit.ui.internal.util.PageDescriptor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import java.util.Collection;
import java.util.Set;

/**
 * The activator class controls the plug-in life cycle
 */
public class RabbitUI extends AbstractUIPlugin {

  // The plug-in ID
  public static final String PLUGIN_ID = "rabbit.ui";

  public static final String UI_PAGE_EXTENSION_ID = "rabbit.ui.pages";

  public static final String DEFAULT_DISPLAY_DATE_PERIOD = "defaultDisplayDatePeriod";

  // The shared instance
  private static RabbitUI plugin;

  /**
   * Returns the shared instance
   * 
   * @return the shared instance
   */
  public static RabbitUI getDefault() {
    return plugin;
  }

  /**
   * The constructor
   */
  public RabbitUI() {
  }

  /**
   * Gets the default number of days to display the data in the main view.
   * 
   * @return The default number of days.
   */
  public int getDefaultDisplayDatePeriod() {
    return getPreferenceStore().getInt(DEFAULT_DISPLAY_DATE_PERIOD);
  }

  /**
   * Sets the default number of days to display the data in the main view.
   * 
   * @param numDays The number of days.
   */
  public void setDefaultDisplayDatePeriod(int numDays) {
    IPreferenceStore store = getPreferenceStore();
    store.setValue(DEFAULT_DISPLAY_DATE_PERIOD, numDays);
  }

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }
  
  /**
   * Loads the root pages.
   * 
   * @return The root pages.
   */
  public Collection<PageDescriptor> loadRootPages() {
    final Set<PageDescriptor> pages = Sets.newLinkedHashSet();
    for (final IConfigurationElement e : Platform.getExtensionRegistry()
        .getConfigurationElementsFor(UI_PAGE_EXTENSION_ID)) {

      SafeRunner.run(new ISafeRunnable() {

        @Override
        public void handleException(Throwable e) {
          e.printStackTrace();
        }

        @Override
        public void run() throws Exception {
          String id = e.getAttribute("id");
          String name = e.getAttribute("name");
          String desc = e.getAttribute("description");
          String imagePath = e.getAttribute("icon");
          String parent = e.getAttribute("parent");

          Object o = e.createExecutableExtension("class");
          if (!(o instanceof IPage)) {
            return;
          }

          ImageDescriptor image = null;
          if (imagePath != null) {
            image = imageDescriptorFromPlugin(e.getContributor().getName(),
                imagePath);
          }
          if (image == null) {
            image = PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT);
          }
          IPage page = (IPage) o;
          pages.add(new PageDescriptor(id, name, page, desc, image, parent));
        }
      });

    }

    // Run through all the elements and
    // restructure them:
    ImmutableSet.Builder<PageDescriptor> builder = ImmutableSet.builder();
    for (PageDescriptor child : pages) {
      if (child.getParentId() == null) {
        builder.add(child);
        continue;
      }
      boolean added = false;
      for (PageDescriptor parent : pages) {
        if (parent.getId().equals(child.getParentId())) {
          parent.getChildren().add(child);
          added = true;
          break;
        }
      }
      if (!added) {
        builder.add(child);
      }
    }
    return builder.build();
  }
}
