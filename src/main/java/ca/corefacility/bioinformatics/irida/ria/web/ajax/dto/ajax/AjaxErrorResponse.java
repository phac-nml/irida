package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax;

/**
 * Response returned if there is an error creating a new item in IRIDA.
 * Will return the internationalized explanation for the error.
 */
public class AjaxErrorResponse extends AjaxResponse {
	private final String error;

	public AjaxErrorResponse(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}
}
