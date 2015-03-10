package ca.corefacility.bioinformatics.irida.model.snapshot.remote;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.snapshot.ProjectSnapshot;

@Entity
@Table(name = "remote_project_snapshot")
public class RemoteProjectSnapshot extends ProjectSnapshot implements RemoteSnapshot {

	@OneToOne(fetch = FetchType.EAGER)
	private RESTLinks links;

	public RemoteProjectSnapshot(RemoteProject project) {
		super(project);
		this.links = project.getRestLinks();
	}

	public RESTLinks getLinks() {
		return links;
	}

}
