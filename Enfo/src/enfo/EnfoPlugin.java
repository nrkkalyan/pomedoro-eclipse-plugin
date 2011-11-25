package enfo;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class EnfoPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "Enfo";

	// The shared instance
	private static EnfoPlugin plugin;
	
	/**
	 * The constructor
	 */
	public EnfoPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static EnfoPlugin getDefault() {
		return plugin;
	}
}
