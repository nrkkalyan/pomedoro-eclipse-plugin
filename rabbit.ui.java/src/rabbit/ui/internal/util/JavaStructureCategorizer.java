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
import static rabbit.ui.internal.pages.Category.JAVA_MEMBER;
import static rabbit.ui.internal.pages.Category.JAVA_PACKAGE;
import static rabbit.ui.internal.pages.Category.JAVA_PACKAGE_ROOT;
import static rabbit.ui.internal.pages.Category.JAVA_TYPE_ROOT;
import static rabbit.ui.internal.pages.Category.PROJECT;
import static rabbit.ui.internal.pages.Category.WORKSPACE;

import rabbit.data.access.model.WorkspaceStorage;

import static com.google.common.base.Predicates.instanceOf;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ITypeRoot;
import org.joda.time.LocalDate;

import java.util.Map;

/**
 * An instance of this class is intended to be used with a
 * {@link ICategoryProvider} for specifying which elements belong to which
 * structural categories in order for the elements to be structured in a certain
 * way.
 * 
 * @see ICategoryProvider
 * @see CategoryProvider
 * @see CategoryProvider#CategoryProvider(ICategory[], ICategory...)
 */
public final class JavaStructureCategorizer implements ICategorizer {

  private final ICategorizer categorizer;

  public JavaStructureCategorizer() {

    /*
     * NOTE: we do not use JAVA_TYPE and JAVA_METHOD here because they are for
     * painting those elements in the viewer only, we use JAVA_MEMBER instead,
     * which represents both JAVA_TYPE and JAVA_METHOD (so that the structure of
     * a class is unchanged when building the tree paths with a builder, because
     * a class/method can have inner class/method etc, too messy).
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
        .put(instanceOf(IMember.class),              JAVA_MEMBER)
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
