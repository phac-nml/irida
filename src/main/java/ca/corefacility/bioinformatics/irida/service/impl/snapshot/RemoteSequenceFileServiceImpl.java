package ca.corefacility.bioinformatics.irida.service.impl.snapshot;

import javax.validation.Validator;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.RemoteSequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.snapshot.RemoteSequenceFileService;

public class RemoteSequenceFileServiceImpl extends CRUDServiceImpl<Long, RemoteSequenceFile> implements
		RemoteSequenceFileService {

	public RemoteSequenceFileServiceImpl(RemoteSequenceFileRepository repository, Validator validator) {
		super(repository, validator, RemoteSequenceFile.class);
	}

	public RemoteSequenceFile mirrorFile(SequenceFile file) {

		RemoteSequenceFile mirror = new RemoteSequenceFile(file);

		return create(mirror);
	}

}
