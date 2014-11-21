package ca.corefacility.bioinformatics.irida.model.snapshot.remote;

import java.nio.file.Path;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.snapshot.SequenceFileSnapshot;

@Entity
@Table(name = "remote_sequence_file_snapshot")
public class RemoteSequenceFileSnapshot extends SequenceFileSnapshot implements RemoteSnapshot {
	@OneToOne(fetch = FetchType.EAGER)
	private RESTLinks links;

	public RemoteSequenceFileSnapshot(RemoteSequenceFile sequenceFile, Path file) {
		super(sequenceFile, file);
		this.links = sequenceFile.getLinks();
	}

	@Override
	public RESTLinks getLinks() {
		return links;
	}

}
