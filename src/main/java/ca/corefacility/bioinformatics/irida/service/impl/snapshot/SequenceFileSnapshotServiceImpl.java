package ca.corefacility.bioinformatics.irida.service.impl.snapshot;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileSnapshotRepository;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.snapshot.SequenceFileSnapshotService;

/**
 * {@link CRUDService} implementation of {@link SequenceFileSnapshotService}
 */
@Service
public class SequenceFileSnapshotServiceImpl extends CRUDServiceImpl<Long, SequenceFileSnapshot> implements
		SequenceFileSnapshotService {

	@Autowired
	public SequenceFileSnapshotServiceImpl(SequenceFileSnapshotRepository repository, Validator validator) {
		super(repository, validator, SequenceFileSnapshot.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public SequenceFileSnapshot mirrorFile(SequenceFile file) {

		SequenceFileSnapshot mirror = new SequenceFileSnapshot(file);

		return create(mirror);
	}

}
