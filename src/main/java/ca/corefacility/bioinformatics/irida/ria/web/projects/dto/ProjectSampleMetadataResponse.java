package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.List;

/**
 * Response for saving sample metadata from a file
 */
public class ProjectSampleMetadataResponse {

	private String message;
	private List<String> errorList;

	public ProjectSampleMetadataResponse() {
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<String> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<String> errorList) {
		this.errorList = errorList;
	}
}
