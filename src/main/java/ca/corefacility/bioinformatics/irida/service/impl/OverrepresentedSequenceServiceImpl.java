
package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.repositories.OverrepresentedSequenceRepository;
import ca.corefacility.bioinformatics.irida.service.OverrepresentedSequenceService;
import java.util.List;
import javax.validation.Validator;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class OverrepresentedSequenceServiceImpl extends CRUDServiceImpl<Long, OverrepresentedSequence>implements OverrepresentedSequenceService{
    OverrepresentedSequenceRepository orsRepo;
	
	public OverrepresentedSequenceServiceImpl(OverrepresentedSequenceRepository repository, Validator validator){
        super(repository, validator, OverrepresentedSequence.class);
        this.orsRepo = repository;
    }
	
	@Override
	public List<Join<SequenceFile, OverrepresentedSequence>> getOverrepresentedSequencesForSequenceFile(SequenceFile sequenceFile) {
		return orsRepo.getOverrepresentedSequencesForSequenceFile(sequenceFile);
	}

}
