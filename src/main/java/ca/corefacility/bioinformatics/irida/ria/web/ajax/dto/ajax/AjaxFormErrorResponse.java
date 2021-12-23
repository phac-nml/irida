package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax;

import java.util.Map;

public class AjaxFormErrorResponse extends AjaxResponse {
	private Map<String, String> errors;

	public AjaxFormErrorResponse(Map<String, String> errors) {
		this.errors = errors;
	}

	public Map<String, String> getErrors() {
		return errors;
	}
}
