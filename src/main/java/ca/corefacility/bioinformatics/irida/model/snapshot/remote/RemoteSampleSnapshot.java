package ca.corefacility.bioinformatics.irida.model.snapshot.remote;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.snapshot.SampleSnapshot;

@Entity
@Table(name = "remote_sample_snapshot")
public class RemoteSampleSnapshot extends SampleSnapshot implements RemoteSnapshot {

	@OneToOne(fetch = FetchType.EAGER)
	private RESTLinks links;

	public RemoteSampleSnapshot(RemoteSample sample) {
		super(sample);
		this.links = sample.getRestLinks();
	}

	@Override
	public RESTLinks getLinks() {
		return links;
	}

}
