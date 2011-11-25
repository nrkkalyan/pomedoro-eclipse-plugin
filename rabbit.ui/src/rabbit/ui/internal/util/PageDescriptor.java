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
package rabbit.ui.internal.util;

import rabbit.ui.IPage;

import org.eclipse.jface.resource.ImageDescriptor;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a page extension descriptor.
 */
public class PageDescriptor {

  private final String parentId;
  private final String id;
  private final String name;
  private final String description;
  private final IPage page;
  private final ImageDescriptor image;
  private final Set<PageDescriptor> pages;

  /**
   * Constructor.
   * 
   * @param id The unique identifier.
   * @param name The name of the page.
   * @param page The java class.
   * @param description The description.
   * @param image The image icon.
   * @param parentId The parent ID.
   */
  public PageDescriptor(String id, String name, IPage page, String description,
      ImageDescriptor image, String parentId) {
    this.id = id;
    this.parentId = parentId;
    this.page = page;
    this.name = name;
    this.image = image;
    this.description = description;
    pages = new HashSet<PageDescriptor>();
  }
  
  /**
   * Gets the ID of the parent page.
   * @return The ID of the parent page.
   */
  public String getParentId() {
    return parentId;
  }
  
  /**
   * Gets the ID of this extension.
   * @return The ID.
   */
  public String getId() {
    return id;
  }
  
  /**
   * Gets the name of this page.
   * @return The name.
   */
  public String getName() {
    return name;
  }
  
  /**
   * Gets the description.
   * @return The description.
   */
  public String getDescription() {
    return description;
  }
  
  /**
   * Gets the actual page.
   * @return The page.
   */
  public IPage getPage() {
    return page;
  }
  
  /**
   * Gets the image icon.
   * @return The icon.
   */
  public ImageDescriptor getImage() {
    return image;
  }
  
  /**
   * Gets the children page.
   * @return A set of children, modifiable.
   */
  public Set<PageDescriptor> getChildren() {
    return pages;
  }

  @Override
  public String toString() {
    return name + ": " + id;
  }
}
