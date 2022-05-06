package ca.corefacility.bioinformatics.irida.model.sample.metadata;

import java.util.Map;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Object grouping a project and all the metadata for samples within that project.
 * The main reason for this class is so that spring security can analyze the metadata being requested in the context of
 * the project it's requested from.  This will ensure that a user is only given metadata they have permissions to read.
 *
 * @see ca.corefacility.bioinformatics.irida.security.permissions.metadata.ReadProjectMetadataResponsePermission
 */
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
