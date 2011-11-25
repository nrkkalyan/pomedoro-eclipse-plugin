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

import rabbit.ui.internal.viewers.ForwardingContentProvider;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.junit.Test;

/**
 * Tests for a {@link ForwardingContentProvider}.
 */
public class ForwardingContentProviderTest extends ForwardingTestCase {

  @Test
  public void disposeShouldBeDelegated() {
    create().dispose();
    assertThat(methodsCalled(), is(callToString("dispose")));
  }

  @Test
  public void inputChangedShouldBeDelegated() {
    Viewer vr = null;
    Object oldIn = "a";
    Object newIn = 123;
    create().inputChanged(vr, oldIn, newIn);
    assertCall("inputChanged", vr, oldIn, newIn);
  }

  /**
   * @return a object to be tested.
   */
  protected ForwardingContentProvider create() {
    final IContentProvider content = newProxy(IContentProvider.class);
    return new ForwardingContentProvider() {
      @Override
      protected IContentProvider delegate() {
        return content;
      }
    };
  }
}
