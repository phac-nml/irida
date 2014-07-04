package ca.corefacility.bioinformatics.irida.service.impl;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.repositories.OverrepresentedSequenceRepository;
import ca.corefacility.bioinformatics.irida.service.OverrepresentedSequenceService;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Service
public class OverrepresentedSequenceServiceImpl extends CRUDServiceImpl<Long, OverrepresentedSequence> implements
		OverrepresentedSequenceService {

	protected OverrepresentedSequenceServiceImpl() {
		super(null, null, OverrepresentedSequence.class);
	}

	@Autowired
	public OverrepresentedSequenceServiceImpl(OverrepresentedSequenceRepository repository, Validator validator) {
		super(repository, validator, OverrepresentedSequence.class);
	}
	
}
