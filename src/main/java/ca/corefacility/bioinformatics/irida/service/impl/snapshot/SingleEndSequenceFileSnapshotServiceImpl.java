package ca.corefacility.bioinformatics.irida.service.impl.snapshot;

import javax.validation.Validator;

import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFileSnapshot;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFileSnapshotRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SingleEndSequenceFileSnapshotRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.snapshot.SingleEndSequenceFileSnapshotService;

@Service
public class SingleEndSequenceFileSnapshotServiceImpl extends CRUDServiceImpl<Long, SingleEndSequenceFileSnapshot>
		implements SingleEndSequenceFileSnapshotService {
	
	private SequenceFileSnapshotRepository fileRepository;
	
	public SingleEndSequenceFileSnapshotServiceImpl(SingleEndSequenceFileSnapshotRepository repository, SequenceFileSnapshotRepository fileRepository,
			Validator validator) {
		super(repository, validator, SingleEndSequenceFileSnapshot.class);
		this.fileRepository = fileRepository;
	}

	@Override
	public SingleEndSequenceFileSnapshot mirrorFile(SingleEndSequenceFile file) {
		SequenceFile seqFile = file.getSequenceFile();


		SequenceFileSnapshot snapshot = new SequenceFileSnapshot(seqFile);

		snapshot = fileRepository.save(snapshot);

		SingleEndSequenceFileSnapshot remoteSequenceFile= new SingleEndSequenceFileSnapshot(snapshot);

		return create(remoteSequenceFile);
	}

}
