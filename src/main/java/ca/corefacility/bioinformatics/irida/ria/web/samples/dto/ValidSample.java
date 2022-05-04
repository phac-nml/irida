package ca.corefacility.bioinformatics.irida.ria.web.samples.dto;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * DTO for when checking if a sample name is valid.  Returns the minimal details of the sample.
 */
public class ValidSample {
	final Long sampleId;
	final String sampleName;
	final Long projectId;
	final String projectName;

	public ValidSample(Project project, Sample sample) {
		this.sampleId = sample.getId();
		this.sampleName = sample.getSampleName();
		this.projectId = project.getId();
		this.projectName = project.getName();
	}

	public Long getSampleId() {
		return sampleId;
	}

	public String getSampleName() {
		return sampleName;
	}

	public Long getProjectId() {
		return projectId;
	}

	public String getProjectName() {
		return projectName;
	}
}
