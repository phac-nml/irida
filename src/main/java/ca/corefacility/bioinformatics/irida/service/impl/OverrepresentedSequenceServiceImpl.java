package ca.corefacility.bioinformatics.irida.service.impl;

import java.util.Set;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.OverrepresentedSequenceRepository;
import ca.corefacility.bioinformatics.irida.repositories.SequenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.OverrepresentedSequenceService;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Service
public class OverrepresentedSequenceServiceImpl extends CRUDServiceImpl<Long, OverrepresentedSequence> implements
		OverrepresentedSequenceService {

	private SequenceFileRepository sequenceFileRepository;

	protected OverrepresentedSequenceServiceImpl() {
		super(null, null, OverrepresentedSequence.class);
	}

	@Autowired
	public OverrepresentedSequenceServiceImpl(OverrepresentedSequenceRepository repository,
			SequenceFileRepository sequenceFileRepository, Validator validator) {
		super(repository, validator, OverrepresentedSequence.class);
		this.sequenceFileRepository = sequenceFileRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public Set<OverrepresentedSequence> getOverrepresentedSequencesForSequenceFile(SequenceFile sequenceFile) {
		SequenceFile loaded = sequenceFileRepository.findOne(sequenceFile.getId());
		loaded.getOverrepresentedSequences().forEach(os -> os.getId());
		return loaded.getOverrepresentedSequences();
	}

}
