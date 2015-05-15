package ca.corefacility.bioinformatics.irida.service.impl.snapshot;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.RemoteSequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.RemoteSequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.snapshot.RemoteSequenceFileService;

/**
 * {@link CRUDService} implementation of {@link RemoteSequenceFileService}
 */
@Service
public class RemoteSequenceFileServiceImpl extends CRUDServiceImpl<Long, RemoteSequenceFile> implements
		RemoteSequenceFileService {

	@Autowired
	public RemoteSequenceFileServiceImpl(RemoteSequenceFileRepository repository, Validator validator) {
		super(repository, validator, RemoteSequenceFile.class);
	}

	/**
	 * {@inheritDoc}
	 */
	public RemoteSequenceFile mirrorFile(SequenceFile file) {

		RemoteSequenceFile mirror = new RemoteSequenceFile(file);

		return create(mirror);
	}

}
