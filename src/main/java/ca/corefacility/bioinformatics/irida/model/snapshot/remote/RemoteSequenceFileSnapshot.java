package ca.corefacility.bioinformatics.irida.model.snapshot.remote;

import java.nio.file.Path;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.snapshot.SequenceFileSnapshot;

@Entity
@Table(name = "remote_sequence_file_snapshot")
public class RemoteSequenceFileSnapshot extends SequenceFileSnapshot implements RemoteSnapshot {
	@Embedded
	private RESTLinks links;

	public RemoteSequenceFileSnapshot(RemoteSequenceFile sequenceFile, Path file) {
		super(file, sequenceFile.getOptionalProperties());
		this.links = sequenceFile.getLinks();
	}

	@Override
	public RESTLinks getLinks() {
		return links;
	}

}
