package ca.corefacility.bioinformatics.irida.plugins;

public class IridaPluginException extends RuntimeException {
	public IridaPluginException(String message, Throwable cause) {
		super(message, cause);
	}

	public IridaPluginException(String message) {
		super(message);
	}
}
