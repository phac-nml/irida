package ca.corefacility.bioinformatics.irida.model.snapshot.remote;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.snapshot.ProjectSnapshot;

@Entity
@Audited
@Table(name = "remote_project_snapshot")
@EntityListeners(AuditingEntityListener.class)
public class RemoteProjectSnapshot extends ProjectSnapshot implements RemoteSnapshot {

	@OneToOne(fetch = FetchType.EAGER)
	private RESTLinks links;

	public RemoteProjectSnapshot(RemoteProject project) {
		super(project);
		this.links = project.getLinks();
	}

	public RESTLinks getLinks() {
		return links;
	}

}
