package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.dto;

import java.util.List;

/**
 * UI request to validate sample names.
 */
public class ValidateSampleNamesRequest {
	private List<ValidateSampleNameModel> samples;
	private List<Long> associated_project_ids;

	public ValidateSampleNamesRequest() {
	}

	public List<ValidateSampleNameModel> getSamples() {
		return samples;
	}

	public void setSamples(List<ValidateSampleNameModel> samples) {
		this.samples = samples;
	}

	public List<Long> getAssociated_project_ids() {
		return associated_project_ids;
	}

	public void setAssociated_project_ids(List<Long> associated_project_ids) {
		this.associated_project_ids = associated_project_ids;
	}
}
