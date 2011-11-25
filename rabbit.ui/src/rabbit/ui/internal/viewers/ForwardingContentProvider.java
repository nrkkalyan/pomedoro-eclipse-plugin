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

import com.google.common.collect.ForwardingObject;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Forwards all method calls to an {@link IContentProvider}.
 */
public abstract class ForwardingContentProvider extends ForwardingObject
    implements IContentProvider {

  protected ForwardingContentProvider() {}

  @Override
  public void dispose() {
    delegate().dispose();
  }

  @Override
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    delegate().inputChanged(viewer, oldInput, newInput);
  }

  @Override
  protected abstract IContentProvider delegate();

}
