package ca.corefacility.bioinformatics.irida.plugins;

import org.pf4j.ExtensionPoint;

import java.util.Properties;

/**
 * Interface describing the methods which must be exposed by an IRIDA pipeline plugin
 */
public interface IridaPlugin extends ExtensionPoint {

	/**
	 * Get the messages to be displayed in the UI for an IRIDA pipeline plugin
	 *
	 * @return a {@link Properties} object containing the messages
	 */
	public Properties getMessages();
}
