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
package rabbit.ui.internal.viewers;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.ProblemsLabelDecorator;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * Label provider for java elements.
 */
public final class JavaLabelProvider
    extends NullLabelProvider implements IStyledLabelProvider {

  private final ProblemsLabelDecorator decorator;
  private final JavaElementLabelProvider provider;
  private final Color gray;

  public JavaLabelProvider() {
    decorator = new ProblemsLabelDecorator();
    provider = new JavaElementLabelProvider(
        JavaElementLabelProvider.SHOW_DEFAULT
            | JavaElementLabelProvider.SHOW_RETURN_TYPE
            | JavaElementLabelProvider.SHOW_SMALL_ICONS) {

      @Override
      public StyledString getStyledText(Object element) {
        return JavaElementLabels.getStyledTextLabel(element,
              JavaElementLabels.ALL_DEFAULT
                  | JavaElementLabels.COLORIZE // We want this bit
                  | JavaElementLabels.M_APP_RETURNTYPE);
      }
    };
    gray = PlatformUI.getWorkbench().getDisplay()
        .getSystemColor(SWT.COLOR_DARK_GRAY);
  }

  @Override
  public void dispose() {
    super.dispose();
    provider.dispose();
    decorator.dispose();
  }

  @Override
  public Color getForeground(Object element) {
    if (element instanceof IJavaElement) {
      if (!((IJavaElement) element).exists()) {
        return gray;
      }
    }
    return super.getForeground(element);
  }

  @Override
  public Image getImage(Object element) {
    if (element instanceof IJavaElement) {
      Image image = provider.getImage(element);
      Image decorated = decorator.decorateImage(image, element);
      if (decorated != null) {
        return decorated;
      }
      return image;
    }
    return super.getImage(element);
  }

  @Override
  public StyledString getStyledText(Object element) {
    if (element instanceof IJavaElement) {
      return provider.getStyledText(element);
    }
    return super.getStyledText(element);
  }

  @Override
  public String getText(Object element) {
    if (element instanceof IJavaElement) {
      return provider.getText(element);
    }
    return super.getText(element);
  }
}
