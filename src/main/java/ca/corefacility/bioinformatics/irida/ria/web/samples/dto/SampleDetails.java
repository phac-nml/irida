package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Used to return details of a {@link Sample} back to the user interface.
 */
public class SampleDetails {
	private Sample sample;
	private boolean modifiable;
	private Long projectId;
	private String projectName;
	private boolean inCart;

	public SampleDetails(Sample sample, boolean modifiable, Project project, boolean inCart) {
		this.sample = sample;
		this.modifiable = modifiable;
		this.projectId = project.getId();
		this.projectName = project.getName();
		this.inCart = inCart;
	}

	public Sample getSample() {
		return sample;
	}

	public boolean isModifiable() {
		return modifiable;
	}

	public Long getProjectId() {
		return projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public boolean isInCart() {
		return inCart;
	}
}
