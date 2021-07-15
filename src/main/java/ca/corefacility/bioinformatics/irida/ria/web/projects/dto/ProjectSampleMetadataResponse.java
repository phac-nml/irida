package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.List;

/**
 * Response for saving sample metadata from a file
 */
public class ProjectSampleMetadataResponse {

	private String messageKey;
	private String message;
	private List errorList;

	public ProjectSampleMetadataResponse() {
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List getErrorList() {
		return errorList;
	}

	public void setErrorList(List errorList) {
		this.errorList = errorList;
	}
}
