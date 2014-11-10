package ca.corefacility.bioinformatics.irida.model.snapshot.remote;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.snapshot.SampleSnapshot;

@Entity
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
