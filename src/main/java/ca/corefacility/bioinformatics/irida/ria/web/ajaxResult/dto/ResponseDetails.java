package ca.corefacility.bioinformatics.irida.ria.web.ajaxResult.dto;

/**
 * Used as a response for ajax result
 */

public class ResponseDetails {
  private String responseCode;
  private String message;

	public ResponseDetails() {
	}

	public ResponseDetails(String responseCode, String message) {
    this.responseCode = responseCode;
    this.message = message;
  }

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
