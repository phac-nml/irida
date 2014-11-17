package ca.corefacility.bioinformatics.irida.model.snapshot.remote;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.snapshot.SampleSnapshot;

@Entity
@Audited
@EntityListeners(AuditingEntityListener.class)
public class RemoteSampleSnapshot extends SampleSnapshot implements RemoteSnapshot {

	@Embedded
	private RESTLinks links;

	public RemoteSampleSnapshot(RemoteSample sample) {
		super(sample);
		this.links = sample.getLinks();
	}

	@Override
	public RESTLinks getLinks() {
		return links;
	}

}
