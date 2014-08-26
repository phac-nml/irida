package ca.corefacility.bioinformatics.irida.ria.utilities;

import ca.corefacility.bioinformatics.irida.ria.web.BaseController;

/**
 * Class storing a property name and a message name to display if the property
 * is in error.
 * 
 * To be used with the getErrorsFromDataIntegrityViolationException method of
 * {@link BaseController}
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class ExceptionPropertyAndMessage {
	private String propertyName;
	private String messageName;

	/**
	 * Create an ExceptionPropertyAndMessage storing the affected property name
	 * and the message name to display if that property is in error.
	 * 
	 * @param propertyName
	 *            The name of the property
	 * @param messageName
	 *            The message name to display if an error occurs
	 */
	public ExceptionPropertyAndMessage(String propertyName, String messageName) {
		this.propertyName = propertyName;
		this.messageName = messageName;
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @return the messageName
	 */
	public String getMessageName() {
		return messageName;
	}

}
