package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax;

public class AjaxSuccessResponse extends AjaxResponse {
	private final String message;

	public AjaxSuccessResponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}

