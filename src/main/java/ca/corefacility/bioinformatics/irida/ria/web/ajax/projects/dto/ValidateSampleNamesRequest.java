package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects.dto;

import java.util.List;

/**
 * UI request to validate sample names.
 */
public class ValidateSampleNamesRequest {
	private List<ValidateSampleNameModel> samples;
	private List<Long> associatedProjectIds;

	public ValidateSampleNamesRequest() {
	}

	public List<ValidateSampleNameModel> getSamples() {
		return samples;
	}

	public void setSamples(List<ValidateSampleNameModel> samples) {
		this.samples = samples;
	}

	public List<Long> getAssociatedProjectIds() {
		return associatedProjectIds;
	}

	public void setAssociatedProjectIds(List<Long> associatedProjectIds) {
		this.associatedProjectIds = associatedProjectIds;
	}
}
