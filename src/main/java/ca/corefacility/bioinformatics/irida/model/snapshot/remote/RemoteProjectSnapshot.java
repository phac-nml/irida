package ca.corefacility.bioinformatics.irida.model.snapshot.remote;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.snapshot.ProjectSnapshot;

@Entity
public class RemoteProjectSnapshot extends ProjectSnapshot implements RemoteSnapshot {

	@OneToOne
	private RESTLinks links;

	public RemoteProjectSnapshot(RemoteProject project) {
		super(project);
		this.links = project.getLinks();
	}

	public RESTLinks getLinks() {
		return links;
	}

}
