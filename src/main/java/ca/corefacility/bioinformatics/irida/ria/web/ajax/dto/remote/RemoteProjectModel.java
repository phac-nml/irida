package ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.remote;

import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Used to represent a remote project for selecting from available remote projects in the create remote synchronized
 * project form
 */
public class RemoteProjectModel {
	private final Long id;
	private final String name;
	private final String remoteUrl;
	private final String key;

	public RemoteProjectModel(Project project) {
		this.id = project.getId();
		this.name = project.getName();
		this.remoteUrl = project.getRemoteStatus().getURL();
		this.key = "project-" + project.getId();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public String getKey() {
		return key;
	}
}
