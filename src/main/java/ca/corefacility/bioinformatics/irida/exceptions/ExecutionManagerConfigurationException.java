package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * An exception that gets thrown for an invalid configuration option for an execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class ExecutionManagerConfigurationException extends Exception {
	
	private static final long serialVersionUID = -7160561564868237213L;

	/**
	 * Constructs a new ExecutionManagerConfigurationException with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public ExecutionManagerConfigurationException(String configurationProperty, Throwable cause) {
		super("Invalid configuration property \"" + configurationProperty + "\"", cause);
	}

	/**
	 * Constructs a new ExecutionManagerConfigurationException with the given property name.
	 * @param message  The property name causing the error.
	 */
	public ExecutionManagerConfigurationException(String configurationProperty) {
		super("Invalid configuration property \"" + configurationProperty + "\"");
	}
}
