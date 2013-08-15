
package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.service.MiseqRunService;
import javax.validation.Validator;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class MiseqRunServiceImpl extends CRUDServiceImpl<Long, MiseqRun> implements MiseqRunService {

    public MiseqRunServiceImpl(CRUDRepository<Long,MiseqRun> repository, Validator validator){
        super(repository, validator, MiseqRun.class);
    }
    
}
