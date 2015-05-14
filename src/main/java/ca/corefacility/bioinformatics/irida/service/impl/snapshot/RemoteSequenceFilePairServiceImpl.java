package ca.corefacility.bioinformatics.irida.service.impl.snapshot;

import javax.validation.Validator;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.RemoteSequenceFilePair;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.RemoteSequenceFilePairRepository;
import ca.corefacility.bioinformatics.irida.service.impl.CRUDServiceImpl;
import ca.corefacility.bioinformatics.irida.service.snapshot.RemoteSequenceFilePairService;

public class RemoteSequenceFilePairServiceImpl extends CRUDServiceImpl<Long, RemoteSequenceFilePair> implements
		RemoteSequenceFilePairService {

	public RemoteSequenceFilePairServiceImpl(RemoteSequenceFilePairRepository repository, Validator validator) {
		super(repository, validator, RemoteSequenceFilePair.class);
	}

}
