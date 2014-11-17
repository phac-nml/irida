package ca.corefacility.bioinformatics.irida.model.snapshot.remote;

import java.nio.file.Path;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.model.remote.resource.RESTLinks;
import ca.corefacility.bioinformatics.irida.model.snapshot.SequenceFileSnapshot;

@Entity
@Audited
@EntityListeners(AuditingEntityListener.class)
public class RemoteSequenceFileSnapshot extends SequenceFileSnapshot implements RemoteSnapshot {
	@Embedded
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
