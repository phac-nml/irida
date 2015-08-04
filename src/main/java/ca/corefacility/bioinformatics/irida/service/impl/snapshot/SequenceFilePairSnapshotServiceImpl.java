package ca.corefacility.bioinformatics.irida.service.impl.snapshot;

import java.util.Iterator;
import java.util.Set;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePairSnapshot;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFilePairSnapshotRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileSnapshotRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.snapshot.SequenceFilePairSnapshotService;

/**
 * {@link CRUDServiceImpl} implementation of
 * {@link SequenceFilePairSnapshotService}
 */
@Service
@PreAuthorize("permitAll")
public class SequenceFilePairSnapshotServiceImpl extends CRUDServiceImpl<Long, SequenceFilePairSnapshot> implements
		SequenceFilePairSnapshotService {

	private SequenceFileSnapshotRepository fileRepository;

	@Autowired
	public SequenceFilePairSnapshotServiceImpl(SequenceFilePairSnapshotRepository repository,
			SequenceFileSnapshotRepository fileRepository, Validator validator) {
		super(repository, validator, SequenceFilePairSnapshot.class);
		this.fileRepository = fileRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SequenceFilePairSnapshot mirrorPair(SequenceFilePair pair) {
		Set<SequenceFile> files = pair.getFiles();

		Iterator<SequenceFile> filesIterator = files.iterator();
		SequenceFileSnapshot f1 = new SequenceFileSnapshot(filesIterator.next());
		SequenceFileSnapshot f2 = new SequenceFileSnapshot(filesIterator.next());

		f1 = fileRepository.save(f1);
		f2 = fileRepository.save(f2);

		SequenceFilePairSnapshot remoteSequenceFilePair = new SequenceFilePairSnapshot(f1, f2);

		return create(remoteSequenceFilePair);
	}

}
