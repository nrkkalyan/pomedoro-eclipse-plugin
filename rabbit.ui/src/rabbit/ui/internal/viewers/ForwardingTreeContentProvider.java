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

import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * Forwards all method calls to an {@link ITreeContentProvider}.
 */
public abstract class ForwardingTreeContentProvider
    extends ForwardingStructuredContentProvider implements ITreeContentProvider {

  protected ForwardingTreeContentProvider() {}

  @Override
  public Object[] getChildren(Object parentElement) {
    return delegate().getChildren(parentElement);
  }

  @Override
  public Object getParent(Object element) {
    return delegate().getParent(element);
  }

  @Override
  public boolean hasChildren(Object element) {
    return delegate().hasChildren(element);
  }

  @Override
  protected abstract ITreeContentProvider delegate();

}
