package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

public class SampleNameValidationResponse {
	private final String status;
	private final String help;

	public String getStatus() {
		return status;
	}

	public String getHelp() {
		return help;
	}

	public SampleNameValidationResponse(String status, String help) {
		this.status = status;
		this.help = help;
	}
}
