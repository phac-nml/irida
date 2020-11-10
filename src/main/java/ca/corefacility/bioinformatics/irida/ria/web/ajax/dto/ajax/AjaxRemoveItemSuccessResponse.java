package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax;

public class AjaxRemoveItemSuccessResponse extends AjaxResponse {
	public String responseMessage;

	public AjaxRemoveItemSuccessResponse(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
}

