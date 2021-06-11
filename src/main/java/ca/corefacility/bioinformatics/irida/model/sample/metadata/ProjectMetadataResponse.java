package ca.corefacility.bioinformatics.irida.model.sample.metadata;

import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.project.Project;

public class ProjectMetadataResponse {
	private Project project;
	private Map<Long, Set<MetadataEntry>> metadata;

	public ProjectMetadataResponse(Project project, Map<Long, Set<MetadataEntry>> metadata) {
		this.project = project;
		this.metadata = metadata;
	}

	public Map<Long, Set<MetadataEntry>> getMetadata() {
		return metadata;
	}

	public Project getProject() {
		return project;
	}
}
