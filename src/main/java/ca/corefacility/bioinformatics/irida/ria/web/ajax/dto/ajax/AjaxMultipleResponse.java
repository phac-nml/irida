package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax;

import java.util.Map;

/**
 * AJAX response to return multiple responses to the client
 */
public class AjaxMultipleResponse extends AjaxResponse {
	private Map<String, Object> responses;

	public AjaxMultipleResponse(Map<String, Object> responses) {
		this.responses = responses;
	}

	public Map<String, Object> getResponses() {
		return responses;
	}
}

