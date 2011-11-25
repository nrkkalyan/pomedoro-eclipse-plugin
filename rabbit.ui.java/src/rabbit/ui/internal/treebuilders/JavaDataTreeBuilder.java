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
package rabbit.ui.internal.treebuilders;

import rabbit.data.access.model.IJavaData;
import rabbit.ui.IProvider;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.util.ICategorizer;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.util.JavaStructureCategorizer;
import rabbit.ui.internal.viewers.ITreePathBuilder;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

import com.google.common.collect.Lists;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jface.viewers.TreePath;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.List;

public final class JavaDataTreeBuilder implements ITreePathBuilder {

  /**
   * Provides {@link IJavaData}.
   */
  public static interface IJavaDataProvider extends IProvider<IJavaData> {}

  private final ICategoryProvider provider;
  private final ICategorizer categorizer;

  public JavaDataTreeBuilder(ICategoryProvider provider) {
    this.provider = checkNotNull(provider);
    this.categorizer = new JavaStructureCategorizer();
  }

  @Override
  public List<TreePath> build(Object input) {
    if (!(input instanceof IJavaDataProvider)) {
      return emptyList();
    }

    Collection<IJavaData> dataCol = ((IJavaDataProvider) input).get();
    if (dataCol == null) {
      return emptyList();
    }

    List<TreePath> result = newArrayList();
    for (IJavaData data : dataCol) {

      List<Object> segments = newArrayList();
      for (ICategory c : provider.getSelected()) {
        if (!(c instanceof Category)) {
          continue;
        }

        List<IJavaElement> elements = getHierarchy(data
            .get(IJavaData.JAVA_ELEMENT));
        switch ((Category) c) {
        case WORKSPACE:
          segments.add(data.get(IJavaData.WORKSPACE));
          break;
        case DATE:
          segments.add(data.get(IJavaData.DATE));
          break;
        default:
          if (!categorizer.hasCategory(c)) {
            continue;
          }

          for (IJavaElement e : elements) {
            // We don't want to show any of the fields and anonymous classes:
            if (isAnonymousType(e) || isField(e)) {
              break;
            }
            if (c.equals(categorizer.getCategory(e))) {
              segments.add(e);
            }
          }
          break;
        }
      }
      segments.add(data.get(IJavaData.DURATION));
      result.add(new TreePath(segments.toArray()));
    }

    return result;
  }

  /**
   * Gets the hierarchy from the given element to the java project.
   * @param element the element.
   * @return an ordered list of elements, the first element is the highest
   *         parent, the last element is the argument itself.
   */
  private List<IJavaElement> getHierarchy(IJavaElement element) {
    List<IJavaElement> elements = Lists.newArrayList();
    elements.add(element);
    while ((element = element.getParent()) != null) {
      elements.add(0, element);
    }
    if (elements.get(0) instanceof IJavaModel) {
      elements.remove(0);
    }
    return elements;
  }

  /**
   * Checks whether the given Java element is an anonymous type.
   * @param type the element to check.
   * @return true if the element is anonymous, false otherwise.
   */
  private boolean isAnonymousType(IJavaElement type) {
    if (type.getElementType() == IJavaElement.TYPE) {
      return type.getParent().getElementType() == IJavaElement.METHOD;
    }
    return false;
  }

  /**
   * Checks whether the given Java element is a field.
   * @param element the element to check.
   * @return true if the element is a field, false otherwise.
   */
  private boolean isField(IJavaElement element) {
    return element.getElementType() == IJavaElement.FIELD;
  }
}
