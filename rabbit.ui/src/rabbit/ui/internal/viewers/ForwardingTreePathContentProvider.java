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

import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;

/**
 * Forwards all method calls to an {@link ITreePathContentProvider}.
 */
public abstract class ForwardingTreePathContentProvider
    extends ForwardingStructuredContentProvider
    implements ITreePathContentProvider {

  protected ForwardingTreePathContentProvider() {}

  @Override
  public Object[] getChildren(TreePath parentPath) {
    return delegate().getChildren(parentPath);
  }

  @Override
  public TreePath[] getParents(Object element) {
    return delegate().getParents(element);
  }

  @Override
  public boolean hasChildren(TreePath path) {
    return delegate().hasChildren(path);
  }

  @Override
  protected abstract ITreePathContentProvider delegate();
}
