package rabbit.ui.internal.pages.java;

import static rabbit.ui.internal.pages.Category.DATE;
import static rabbit.ui.internal.pages.Category.JAVA_MEMBER;
import static rabbit.ui.internal.pages.Category.JAVA_PACKAGE;
import static rabbit.ui.internal.pages.Category.JAVA_PACKAGE_ROOT;
import static rabbit.ui.internal.pages.Category.JAVA_TYPE_ROOT;
import static rabbit.ui.internal.pages.Category.PROJECT;
import static rabbit.ui.internal.pages.Category.WORKSPACE;

import rabbit.ui.internal.pages.AbsPage;
import rabbit.ui.internal.pages.AbsPageTest;
import rabbit.ui.internal.pages.Category;

/**
 * @see JavaPage
 */
public final class JavaPageTest extends AbsPageTest {

  @Override
  protected AbsPage create() {
    return new JavaPage();
  }

  @Override
  protected Category[] getSupportedCategories() {
    return new Category[]{
        WORKSPACE,
        DATE,
        PROJECT,
        JAVA_PACKAGE_ROOT,
        JAVA_PACKAGE,
        JAVA_TYPE_ROOT,
        JAVA_MEMBER};
  }

}
