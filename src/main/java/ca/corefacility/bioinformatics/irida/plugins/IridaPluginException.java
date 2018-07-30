package ca.corefacility.bioinformatics.irida.plugins;

/**
 * An exception thrown when initializing IRIDA pipelines
 */
public class IridaPluginException extends Exception {
	
	public IridaPluginException(String message, Throwable cause) {
		super(message, cause);
	}

	public IridaPluginException(String message) {
		super(message);
	}
}
