package ca.corefacility.bioinformatics.irida.service.impl;

import javax.validation.Validator;

import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;

@Service
public class SequencingObjectServiceImpl extends CRUDServiceImpl<Long, SequencingObject> {

	public SequencingObjectServiceImpl(SequencingObjectRepository repository, Validator validator,
			Class<SequencingObject> valueType) {
		super(repository, validator, valueType);
	}

}
