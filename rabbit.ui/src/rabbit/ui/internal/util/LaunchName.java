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
package rabbit.ui.internal.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * Represents a name of a launch with the associated launch type ID.
 */
public final class LaunchName {

  private final String launchName;
  private final String launchTypeId;

  /**
   * @param launchName the name of this launch.
   * @param launchTypeId the launch type ID of this launch.
   * @throws NullPointerException if any argument is null.
   */
  public LaunchName(String launchName, String launchTypeId) {
    this.launchName = checkNotNull(launchName);
    this.launchTypeId = checkNotNull(launchTypeId);
  }

  /**
   * @return the the name of this launch.
   */
  public String getLaunchName() {
    return launchName;
  }

  /**
   * @return the launch type ID of this launch.
   */
  public String getLaunchTypeId() {
    return launchTypeId;
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this)
        .addValue(getLaunchName())
        .addValue(getLaunchTypeId()).toString();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getLaunchName(), getLaunchTypeId());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof LaunchName) {
      LaunchName l = (LaunchName) obj;
      return Objects.equal(getLaunchName(), l.getLaunchName())
          && Objects.equal(getLaunchTypeId(), l.getLaunchTypeId());
    }
    return false;
  }
}
