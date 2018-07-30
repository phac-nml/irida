package ca.corefacility.bioinformatics.irida.plugins;

import org.pf4j.ExtensionPoint;

import java.util.Properties;

public interface IridaPlugin extends ExtensionPoint {

	public Properties getMessagesFile();
}
