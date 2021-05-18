package ca.corefacility.bioinformatics.irida.ria.web.errors;

/**
 * Error thrown if an item is not found on an Ajax Request
 */
public class AjaxItemNotFoundException extends Error{
	public AjaxItemNotFoundException(String message) {
		super(message);
	}
}
