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
package rabbit.ui.internal.pages;

/**
 * @see TaskPage
 */
@SuppressWarnings("restriction")
public class TaskPageTest extends AbsPageTest {

  @Override
  protected AbsPage create() {
    return new TaskPage();
  }

  @Override
  protected Category[] getSupportedCategories() {
    return new Category[]{
        Category.WORKSPACE,
        Category.DATE,
        Category.TASK,
        Category.PROJECT,
        Category.FOLDER,
        Category.FILE,};
  }

}
