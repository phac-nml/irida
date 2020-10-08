package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.List;

/**
 * Data transfer object for creating a command for the ngs-linker.pl
 */
public class NGSLinkerCmdRequest {
	private List<Long> sampleIds;
	private Long projectId;

	public NGSLinkerCmdRequest() {
	}

	public List<Long> getSampleIds() {
		return sampleIds;
	}

	public void setSampleIds(List<Long> sampleIds) {
		this.sampleIds = sampleIds;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
}
