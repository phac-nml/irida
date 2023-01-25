package ca.corefacility.bioinformatics.irida.web.assembler.resource;

import ca.corefacility.bioinformatics.irida.model.IridaRepresentationModel;

/**
 * Resource class for storing a full project hash for the REST API
 */
public class ProjectHashResource extends IridaRepresentationModel {
	private Integer projectHash;

	public ProjectHashResource() {
	}

	public ProjectHashResource(Integer projectHash) {
		this.projectHash = projectHash;
	}

	public Integer getProjectHash() {
		return projectHash;
	}
}
