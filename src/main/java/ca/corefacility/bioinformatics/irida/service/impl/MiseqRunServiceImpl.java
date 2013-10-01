
package ca.corefacility.bioinformatics.irida.service.impl;

import javax.validation.Validator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.repositories.MiseqRunRepository;
import ca.corefacility.bioinformatics.irida.service.MiseqRunService;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Service
public class MiseqRunServiceImpl extends CRUDServiceImpl<Long, MiseqRun> implements MiseqRunService {

    private MiseqRunRepository miseqRepo;
    
    public MiseqRunServiceImpl(MiseqRunRepository repository, Validator validator){
        super(repository, validator, MiseqRun.class);
        this.miseqRepo = repository;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @Transactional
    public Join<MiseqRun, SequenceFile> addSequenceFileToMiseqRun(MiseqRun run, SequenceFile file) {
        return miseqRepo.addSequenceFileToMiseqRun(run, file);
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    @Transactional(readOnly = true)
    public Join<MiseqRun, SequenceFile> getMiseqRunForSequenceFile(SequenceFile file) {
        return miseqRepo.getMiseqRunForSequenceFile(file);
    }
    
}
