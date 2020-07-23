package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

public class SampleNameValidationRequest {
	private String name;

	public SampleNameValidationRequest(String name) {
		this.name = name;
	}

	public SampleNameValidationRequest() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
