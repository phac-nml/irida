package ca.corefacility.bioinformatics.irida.ria.web.projects.dto;

import java.util.List;

public class LinkerCmdRequest {
	private List<Long> sampleIds;
	private Long projectId;

	public LinkerCmdRequest() {
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
