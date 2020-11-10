package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax;

/**
 * Response returned if an upload of an item is successful.
 * Will return either the success text.
 */
public class AjaxSuccessResponse extends AjaxResponse {
	private final String message;

	public AjaxSuccessResponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}

