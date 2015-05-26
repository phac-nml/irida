package ca.corefacility.bioinformatics.irida.ria.dialects.processors.icons;

/**
 * {@link Exception} thrown when the icon is not in the predefined list.
 */
public class IconNotFoundException extends Exception {
	/**
	 * Construct a new {@link IconNotFoundException} with the specified message.
	 *
	 * @param message
	 *            the message explaining what icon was not found.
	 */
	public IconNotFoundException(String message) {
		super(message);
	}
}
