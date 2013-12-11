package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.repositories.OverrepresentedSequenceRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sequencefile.SequenceFileOverrepresentedSequenceJoinRepository;
import ca.corefacility.bioinformatics.irida.service.OverrepresentedSequenceService;

import java.util.List;

import javax.validation.Validator;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class OverrepresentedSequenceServiceImpl extends CRUDServiceImpl<Long, OverrepresentedSequence> implements
		OverrepresentedSequenceService {
	/**
	 * Reference to {@link SequenceFileOverrepresentedSequenceJoinRepository}.
	 */
	private SequenceFileOverrepresentedSequenceJoinRepository sfosRepository;

	protected OverrepresentedSequenceServiceImpl() {
		super(null, null, OverrepresentedSequence.class);
	}

	public OverrepresentedSequenceServiceImpl(OverrepresentedSequenceRepository repository,
			SequenceFileOverrepresentedSequenceJoinRepository sfosRepository, Validator validator) {
		super(repository, validator, OverrepresentedSequence.class);
		this.sfosRepository = sfosRepository;
	}

	@Override
	public List<Join<SequenceFile, OverrepresentedSequence>> getOverrepresentedSequencesForSequenceFile(
			SequenceFile sequenceFile) {
		return sfosRepository.getOverrepresentedSequencesForSequenceFile(sequenceFile);
	}

}
