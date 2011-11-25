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

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * A label provider that simply delegates method calls to other
 * label providers until a not null value is found.
 */
public class CompositeCellLabelProvider
    extends ColumnLabelProvider implements IStyledLabelProvider {

  private final IBaseLabelProvider[] providers;

  /**
   * Constructor.
   * @param labelProviders the actual providers to ask for labels.
   * @throws NullPointerException if {@code labelProviders} contains
   *         <code>null</code>.
   */
  public CompositeCellLabelProvider(IBaseLabelProvider... labelProviders) {
    for (IBaseLabelProvider p : labelProviders) {
      checkNotNull(p);
    }
    this.providers = labelProviders.clone();
  }
  
  @Override
  public void dispose() {
    super.dispose();
    for (IBaseLabelProvider provider : providers) {
      provider.dispose();
    }
  }
  
  @Override
  public Color getBackground(Object element) {
    for (IBaseLabelProvider provider : providers) {
      if (provider instanceof IColorProvider) {
        Color color = ((IColorProvider) provider).getBackground(element);
        if (color != null) {
          return color;
        }
      }
    }
    return super.getBackground(element);
  }
  
  @Override
  public Font getFont(Object element) {
    for (IBaseLabelProvider p : providers) {
      if (p instanceof IFontProvider) {
        Font font = ((IFontProvider) p).getFont(element);
        if (font != null) {
          return font;
        }
      }
    }
    return super.getFont(element);
  }

  @Override
  public Color getForeground(Object element) {
    for (IBaseLabelProvider provider : providers) {
      if (provider instanceof IColorProvider) {
        Color color = ((IColorProvider) provider).getForeground(element);
        if (color != null) {
          return color;
        }
      }
    }
    return super.getForeground(element);
  }
  
  @Override
  public Image getImage(Object element) {
    for (IBaseLabelProvider provider : providers) {
      if (provider instanceof ILabelProvider) {
        Image image = ((ILabelProvider) provider).getImage(element);
        if (image != null) {
          return image;
        }
      }
    }
    return super.getImage(element);
  }

  @Override
  public StyledString getStyledText(Object element) {
    for (IBaseLabelProvider p : providers) {
      if (p instanceof IStyledLabelProvider) {
        StyledString str = ((IStyledLabelProvider) p).getStyledText(element);
        if (str != null) {
          return str;
        }
      }
    }
    String text = getText(element);
    if (text == null) {
      text = element.toString();
    }
    return new StyledString(text);
  }

  @Override
  public String getText(Object element) {
    for (IBaseLabelProvider provider : providers) {
      if (provider instanceof ILabelProvider) {
        String text = ((ILabelProvider) provider).getText(element);
        if (text != null) {
          return text;
        }
      }
    }
    return super.getText(element);
  }
}
