package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * An exception that gets thrown for an invalid configuration option for an execution manager.
 *
 */
public class ExecutionManagerConfigurationException extends Exception {
	
	private static final long serialVersionUID = -7160561564868237213L;

	/**
	 * Constructs a new ExecutionManagerConfigurationException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param configurationProperty  The configuration property causing the issue.
	 * @param cause  The cause of this message.
	 */
	public ExecutionManagerConfigurationException(String message, String configurationProperty, Throwable cause) {
		super(message + " for property \"" + configurationProperty + "\"", cause);
	}

	/**
	 * Constructs a new ExecutionManagerConfigurationException with the given property name.
	 * @param message  The property name causing the error.
	 * @param configurationProperty  The configuration property causing the issue.
	 */
	public ExecutionManagerConfigurationException(String message, String configurationProperty) {
		super(message + " for property \"" + configurationProperty + "\"");
	}
}
