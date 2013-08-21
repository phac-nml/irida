
package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.impl.MiseqRunSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.MiseqRunRepository;
import ca.corefacility.bioinformatics.irida.service.MiseqRunService;
import javax.validation.Validator;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class MiseqRunServiceImpl extends CRUDServiceImpl<Long, MiseqRun> implements MiseqRunService {

    private MiseqRunRepository miseqRepo;
    
    public MiseqRunServiceImpl(MiseqRunRepository repository, Validator validator){
        super(repository, validator, MiseqRun.class);
        this.miseqRepo = repository;
    }

    @Override
    public MiseqRunSequenceFileJoin addSequenceFileToMiseqRun(MiseqRun run, SequenceFile file) {
        return miseqRepo.addSequenceFileToMiseqRun(run, file);
    }

    @Override
    public MiseqRunSequenceFileJoin getMiseqRunForSequenceFile(SequenceFile file) {
        return miseqRepo.getMiseqRunForSequenceFile(file);
    }
    
}
