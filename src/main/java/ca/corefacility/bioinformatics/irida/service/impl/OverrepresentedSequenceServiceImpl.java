package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Set;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
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
	/**
	 * Reference to {@link OverrepresentedSequenceRepository}.
	 */
	private OverrepresentedSequenceRepository overrepresentedSequenceRepository;

	protected OverrepresentedSequenceServiceImpl() {
		super(null, null, OverrepresentedSequence.class);
	}

	@Autowired
	public OverrepresentedSequenceServiceImpl(OverrepresentedSequenceRepository repository, Validator validator) {
		super(repository, validator, OverrepresentedSequence.class);
		this.overrepresentedSequenceRepository = repository;
	}

	@Override
	@Transactional(readOnly = true)
	public Set<OverrepresentedSequence> getOverrepresentedSequencesForSequenceFile(SequenceFile sequenceFile) {
		return overrepresentedSequenceRepository.findOverrepresentedSequencesForSequenceFile(sequenceFile);
	}

}
