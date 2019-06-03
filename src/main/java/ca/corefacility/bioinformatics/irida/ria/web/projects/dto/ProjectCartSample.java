package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * This class is USED ONLY for when a user selects all sample to be added to
 * the cart from the project samples page.  Since the samples might not be
 * currently loaded into the table, a minimum representation needs to be returned
 * so that they can properly be added to the updated cart.
 */
public class ProjectCartSample {
	private long id;
	private long projectId;
	private String sampleName;

	public ProjectCartSample(Sample sample, long projectId) {
		this.id = sample.getId();
		this.sampleName = sample.getSampleName();
		this.projectId = projectId;
	}

	public long getId() {
		return id;
	}

	public long getProjectId() {
		return projectId;
	}

	public String getSampleName() {
		return sampleName;
	}
}