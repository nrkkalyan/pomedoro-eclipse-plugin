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
package rabbit.tracking.internal.trackers;

import static rabbit.data.store.model.BreakpointEvent.Status.ADDED;
import static rabbit.data.store.model.BreakpointEvent.Status.DISABLED;
import static rabbit.data.store.model.BreakpointEvent.Status.ENABLED;
import static rabbit.data.store.model.BreakpointEvent.Status.REMOVED;

import rabbit.data.store.IStorer;
import rabbit.data.store.model.BreakpointEvent;
import rabbit.data.store.model.BreakpointEvent.Status;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

// TODO remove
@Deprecated
public class BreakpointTracker extends AbstractTracker<BreakpointEvent>
    implements IBreakpointListener {

  /*
   * We choose to store the marker ID instead of an breakpoint or an marker is
   * because these objects may be replaced by other objects at runtime, causing
   * Map.Contains(Object) to return false all the time, so we store IDs, which
   * will stay the same through out replacement objects.
   */
  /**
   * A map of breakpoint status. The keys are {@code IMarker.getId()}, values
   * are {@code IBreakpoint.isEnabled()}.
   */
  private Map<Long, Boolean> status;

  public BreakpointTracker() {
    status = new HashMap<Long, Boolean>();
  }

  @Override
  protected IStorer<BreakpointEvent> createDataStorer() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void doDisable() {
    DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(
        this);
  }

  @Override
  protected void doEnable() {
    DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(this);
  }

  @Override
  public void breakpointAdded(IBreakpoint breakpoint) {
    IMarker marker = breakpoint.getMarker();
    if (marker == null)
      return;

    try {
      boolean enabled = breakpoint.isEnabled();
      status.put(marker.getId(), enabled);
      
      addData(new BreakpointEvent(new DateTime(), breakpoint, ADDED));
    } catch (CoreException e) {
      // Just ignore this breakpoint.
    }
  }

  @Override
  public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
    IMarker marker = breakpoint.getMarker();
    if (marker == null)
      return;

    long markerId = marker.getId();
    Boolean isEnabledBefore = status.get(markerId);
    if (isEnabledBefore == null)
      return; // Don't know what's changed.

    try {
      boolean isEnabledNow = breakpoint.isEnabled();
      if (isEnabledBefore.booleanValue() != isEnabledNow) {
        Status stat = isEnabledNow ? ENABLED : DISABLED;
        addData(new BreakpointEvent(new DateTime(), breakpoint, stat));
        
        status.put(markerId, isEnabledNow);
      }
    } catch (CoreException e) {
      // Just ignore this breakpoint.
    }
  }

  @Override
  public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
    IMarker marker = breakpoint.getMarker();
    if (marker != null) {
      addData(new BreakpointEvent(new DateTime(), breakpoint, REMOVED));
      
      status.remove(marker.getId());
    }
  }

}
