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

import static rabbit.ui.internal.pages.Category.DATE;
import static rabbit.ui.internal.pages.Category.JAVA_METHOD;
import static rabbit.ui.internal.pages.Category.JAVA_PACKAGE;
import static rabbit.ui.internal.pages.Category.JAVA_PACKAGE_ROOT;
import static rabbit.ui.internal.pages.Category.JAVA_TYPE;
import static rabbit.ui.internal.pages.Category.JAVA_TYPE_ROOT;
import static rabbit.ui.internal.pages.Category.PROJECT;
import static rabbit.ui.internal.pages.Category.WORKSPACE;

import rabbit.data.access.model.WorkspaceStorage;
import rabbit.ui.IProvider;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.base.Predicates.or;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.joda.time.LocalDate;

import java.util.Map;

/**
 * An instance of this class is intended to be used with a
 * {@link TreePathValueProvider} for specifying which elements belong to which
 * visual categories.
 * 
 * @see TreePathValueProvider
 * @see TreePathValueProvider#TreePathValueProvider(ICategorizer, IProvider,
 *      IConverter, ICategory)
 * @see TreePathValueProvider#setVisualCategory(ICategory)
 * @see TreePathValueProvider#getVisualCategory()
 * @see TreePathValueProvider#shouldPaint(Object)
 */
public final class JavaVisualCategorizer implements ICategorizer {

  private final ICategorizer categorizer;

  public JavaVisualCategorizer() {

    /*
     * Note that we don't use JAVA_MEMBER here because it's for structuring the
     * elements only (when building the tree paths). We use JAVA_TYPE and
     * JAVA_METHOD here instead so that the corresponding elements in the tree
     * can be painted separately.
     */

    // @formatter:off
    Map<Predicate<Object>, ICategory> categories = ImmutableMap
        .<Predicate<Object>, ICategory> builder()
        .put(instanceOf(WorkspaceStorage.class),     WORKSPACE)
        .put(instanceOf(LocalDate.class),            DATE)
        .put(instanceOf(IJavaProject.class),         PROJECT)
        .put(instanceOf(IPackageFragmentRoot.class), JAVA_PACKAGE_ROOT)
        .put(instanceOf(IPackageFragment.class),     JAVA_PACKAGE)
        .put(instanceOf(ITypeRoot.class),            JAVA_TYPE_ROOT)
        .put(instanceOf(IType.class),                JAVA_TYPE)
        .put(or(
            instanceOf(IMethod.class),
            instanceOf(IInitializer.class)),         JAVA_METHOD)
        .build();
    // @formatter:on
    categorizer = new Categorizer(categories);
  }

  @Override
  public ICategory getCategory(Object element) {
    return categorizer.getCategory(element);
  }

  @Override
  public boolean hasCategory(ICategory category) {
    return categorizer.hasCategory(category);
  }
}
