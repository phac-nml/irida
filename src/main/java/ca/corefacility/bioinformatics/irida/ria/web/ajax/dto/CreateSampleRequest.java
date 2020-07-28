package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto;

/**
 * UI Request to create a new sample
 */
public class CreateSampleRequest {
	private  String name;
	private  String organism;

	public CreateSampleRequest() {
	}

	public CreateSampleRequest(String name, String organism) {
		this.name = name;
		this.organism = organism;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public String getName() {
		return name;
	}

	public String getOrganism() {
		return organism;
	}
}
