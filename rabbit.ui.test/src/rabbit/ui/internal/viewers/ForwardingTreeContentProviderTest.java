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

import rabbit.ui.internal.viewers.ForwardingTreeContentProvider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.junit.Test;

/**
 * Tests for a {@link ForwardingTreeContentProvider}.
 */
public class ForwardingTreeContentProviderTest
    extends ForwardingStructuredContentProviderTest {

  @Test
  public void getChildrenShouldBeDelegated() {
    Object parent = "aParent";
    create().getChildren(parent);
    assertCall("getChildren", parent);
  }

  @Test
  public void getParentShouldBeDelegated() {
    Object child = "aChild";
    create().getParent(child);
    assertCall("getParent", child);
  }

  @Test
  public void hasChildrenShouldBeDelegated() {
    Object parent = "aParent";
    create().hasChildren(parent);
    assertCall("hasChildren", parent);
  }

  @Override
  protected ForwardingTreeContentProvider create() {
    final ITreeContentProvider p = newProxy(ITreeContentProvider.class);
    return new ForwardingTreeContentProvider() {

      @Override
      protected ITreeContentProvider delegate() {
        return p;
      }
    };
  }
}
