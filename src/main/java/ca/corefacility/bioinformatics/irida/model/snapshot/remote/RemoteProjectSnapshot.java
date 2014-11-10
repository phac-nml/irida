package ca.corefacility.bioinformatics.irida.model.snapshot.remote;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.snapshot.ProjectSnapshot;

@Entity
@Table(name = "remote_project_snapshot")
public class RemoteProjectSnapshot extends ProjectSnapshot implements RemoteSnapshot {

	@Embedded
	private RESTLinks links;

	public RemoteProjectSnapshot(RemoteProject project) {
		super(project);
		this.links = project.getLinks();
	}

	public RESTLinks getLinks() {
		return links;
	}

}
