package ca.corefacility.bioinformatics.irida.service.impl.snapshot;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileSnapshotRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SingleEndSequenceFileSnapshotRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.snapshot.SingleEndSequenceFileSnapshotService;

/**
 * Implementation of {@link SingleEndSequenceFileSnapshotService} using a
 * {@link SequenceFileSnapshotRepository}
 */
@Service
public class SingleEndSequenceFileSnapshotServiceImpl extends CRUDServiceImpl<Long, SingleEndSequenceFileSnapshot>
		implements SingleEndSequenceFileSnapshotService {

	private SequenceFileSnapshotRepository fileRepository;

	@Autowired
	public SingleEndSequenceFileSnapshotServiceImpl(SingleEndSequenceFileSnapshotRepository repository,
			SequenceFileSnapshotRepository fileRepository, Validator validator) {
		super(repository, validator, SingleEndSequenceFileSnapshot.class);
		this.fileRepository = fileRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SingleEndSequenceFileSnapshot mirrorFile(SingleEndSequenceFile file) {
		SequenceFile seqFile = file.getSequenceFile();

		SequenceFileSnapshot snapshot = new SequenceFileSnapshot(seqFile);

		snapshot = fileRepository.save(snapshot);

		SingleEndSequenceFileSnapshot remoteSequenceFile = new SingleEndSequenceFileSnapshot(file, snapshot);

		return create(remoteSequenceFile);
	}

}
