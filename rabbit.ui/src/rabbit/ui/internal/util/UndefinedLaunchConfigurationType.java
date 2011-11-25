/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.ui.internal.util;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputer;

import java.util.Collections;
import java.util.Set;

/**
 * Represents an undefined launch configuration type.
 */
@SuppressWarnings("rawtypes")
public class UndefinedLaunchConfigurationType
    implements ILaunchConfigurationType {

  private final String identifier;

  /**
   * Constructor.
   * @param id The ID of this launch configuration type.
   * @throws NullPointerException If argument is null.
   */
  public UndefinedLaunchConfigurationType(String id) {
    checkNotNull(id);
    identifier = id;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (obj.getClass() != getClass())
      return false;

    UndefinedLaunchConfigurationType t = (UndefinedLaunchConfigurationType) obj;
    return getIdentifier().equals(t.getIdentifier());
  }

  @Override
  public int hashCode() {
    return getIdentifier().hashCode();
  }

  @Override
  public String getAttribute(String attributeName) {
    return null;
  }

  @Override
  public String getCategory() {
    return null;
  }

  @Override
  public String getContributorName() {
    return "";
  }

  @Override
  public ILaunchConfigurationDelegate getDelegate() throws CoreException {
    throw new CoreException(new Status(IStatus.ERROR, "",
        "Unsupported operation"));
  }

  @Override
  public ILaunchConfigurationDelegate getDelegate(String mode)
      throws CoreException {
    throw new CoreException(new Status(IStatus.ERROR, "",
        "Unsupported operation"));
  }

  @Override
  public ILaunchDelegate[] getDelegates(Set modes) throws CoreException {
    return new ILaunchDelegate[0];
  }

  @Override
  public String getIdentifier() {
    return identifier;
  }

  @Override
  public String getName() {
    return getIdentifier();
  }

  @Override
  public String getPluginIdentifier() {
    return "";
  }

  @Override
  public ILaunchDelegate getPreferredDelegate(Set modes) throws CoreException {
    return null;
  }

  @Override
  public String getSourceLocatorId() {
    return null;
  }

  @Override
  public ISourcePathComputer getSourcePathComputer() {
    return null;
  }

  @Override
  public Set getSupportedModeCombinations() {
    return Collections.emptySet();
  }

  @Override
  public Set getSupportedModes() {
    return Collections.emptySet();
  }

  @Override
  public boolean isPublic() {
    return false;
  }

  @Override
  public ILaunchConfigurationWorkingCopy newInstance(IContainer container,
      String name) throws CoreException {
    throw new CoreException(new Status(IStatus.ERROR, "",
        "Unsupported operation"));
  }

  @Override
  public void setPreferredDelegate(Set modes, ILaunchDelegate delegate)
      throws CoreException {
    throw new CoreException(new Status(IStatus.ERROR, "",
        "Unsupported operation"));
  }

  @Override
  public boolean supportsMode(String mode) {
    return false;
  }

  @Override
  public boolean supportsModeCombination(Set modes) {
    return false;
  }

  @Override
  public Object getAdapter(Class adapter) {
    return null;
  }
}
