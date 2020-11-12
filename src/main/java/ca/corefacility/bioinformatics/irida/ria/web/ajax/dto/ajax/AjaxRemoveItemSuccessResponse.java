package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax;

/**
 * UI Response for successfully removing an item. Will return the internationalized
 * success text.
 */
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

