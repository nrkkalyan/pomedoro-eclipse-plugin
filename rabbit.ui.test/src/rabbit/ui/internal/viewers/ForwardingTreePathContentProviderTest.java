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

import rabbit.ui.internal.viewers.ForwardingTreePathContentProvider;

import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.junit.Test;

/**
 * Tests for a {@link ForwardingTreePathContentProvider}.
 */
public class ForwardingTreePathContentProviderTest
    extends ForwardingStructuredContentProviderTest {
  
  @Test
  public void getChildrenShouldBeDelegated() {
    TreePath parent = TreePath.EMPTY;
    create().getChildren(parent);
    assertCall("getChildren", parent);
  }
  
  @Test
  public void getParentsShouldBeDelegated() {
    Object child = new Object();
    create().getParents(child);
    assertCall("getParents", child);
  }
  
  @Test
  public void hasChildrenShouldBeDelegated() {
    TreePath parent = TreePath.EMPTY;
    create().hasChildren(parent);
    assertCall("hasChildren", parent);
  }

  @Override
  protected ForwardingTreePathContentProvider create() {
    final ITreePathContentProvider p = newProxy(ITreePathContentProvider.class);
    return new ForwardingTreePathContentProvider() {
      
      @Override
      protected ITreePathContentProvider delegate() {
        return p;
      }
    };
  }
}
