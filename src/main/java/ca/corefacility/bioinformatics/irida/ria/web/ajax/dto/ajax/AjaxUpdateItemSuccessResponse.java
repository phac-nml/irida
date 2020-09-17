package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax;

public class AjaxUpdateItemSuccessResponse extends AjaxResponse {
	public String responseMessage;

	public AjaxUpdateItemSuccessResponse(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
}
