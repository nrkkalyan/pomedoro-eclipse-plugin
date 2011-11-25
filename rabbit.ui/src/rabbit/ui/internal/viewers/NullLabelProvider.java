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

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import javax.annotation.Nullable;

/**
 * A {@link NullLabelProvider} specifies that if an element is not recognized by
 * this provider, calling {@link #getText(Object)} or {@link #getImage(Object)}
 * on that element will always return null.
 */
public class NullLabelProvider extends BaseLabelProvider implements
    ILabelProvider, IFontProvider, IColorProvider, IStyledLabelProvider {

  protected NullLabelProvider() {}

  @Override
  public Image getImage(@Nullable Object element) {
    return null;
  }

  @Override
  public String getText(@Nullable Object element) {
    return null;
  }

  @Override
  public Color getForeground(@Nullable Object element) {
    return null;
  }

  @Override
  public Color getBackground(@Nullable Object element) {
    return null;
  }

  @Override
  public Font getFont(@Nullable Object element) {
    return null;
  }

  @Override
  public StyledString getStyledText(@Nullable Object element) {
    return null;
  }
}
