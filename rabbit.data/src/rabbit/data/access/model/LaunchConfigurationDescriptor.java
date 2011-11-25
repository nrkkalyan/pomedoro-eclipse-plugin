package rabbit.data.access.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchMode;

/**
 * Data descriptor for launch configurations.
 */
public class LaunchConfigurationDescriptor {

  private final String launchName;
  private final String launchModeId;
  private final String launchTypeId;

  /**
   * Constructs a new descriptor.
   * 
   * @param name The name of the launch configuration.
   * @param launchModeId The ID of the launch mode.
   * @param launchTypeId The ID of the launch type.
   * @throws NullPointerException If any of the arguments is null.
   */
  public LaunchConfigurationDescriptor(String name, 
                                       String launchModeId, 
                                       String launchTypeId) {
    
    this.launchName = checkNotNull(name);
    this.launchModeId = checkNotNull(launchModeId);
    this.launchTypeId = checkNotNull(launchTypeId);
  }
  
  /**
   * Finds the launch configuration type that has the same ID as
   * {@link #getLaunchTypeId()}.
   * @return The launch configuration type, or null if not found.
   */
  public final ILaunchConfigurationType findLaunchConfigurationType() {
    return DebugPlugin.getDefault().getLaunchManager()
        .getLaunchConfigurationType(getLaunchTypeId());
  }
  
  /**
   * Finds the launch mode that has the same ID as {@link #getLaunchModeId()}.
   * @return The launch mode, or null if not found.
   */
  public final ILaunchMode findLaunchMode() {
    return DebugPlugin.getDefault().getLaunchManager()
        .getLaunchMode(getLaunchModeId());
  }

  /**
   * Gets the ID of the launch mode.
   * 
   * @return The ID of the launch mode, never null.
   */
  public final String getLaunchModeId() {
    return launchModeId;
  }

  /**
   * Gets the ID of the launch type.
   * 
   * @return The ID of the launch type, never null.
   */
  public final String getLaunchTypeId() {
    return launchTypeId;
  }

  /**
   * Gets the name of the launch configuration.
   * 
   * @return The name of the launch configuration, never null.
   */
  public final String getLaunchName() {
    return launchName;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getLaunchName(), getLaunchModeId(),
        getLaunchTypeId());
  }

  @Override
  public boolean equals(Object obj) {
    if (null == obj)
      return false;
    if (this == obj)
      return true;
    if (getClass() != obj.getClass())
      return false;

    LaunchConfigurationDescriptor des = (LaunchConfigurationDescriptor) obj;
    return getLaunchName().equals(des.getLaunchName())
        && getLaunchModeId().equals(des.getLaunchModeId())
        && getLaunchTypeId().equals(des.getLaunchTypeId());
  }
}
