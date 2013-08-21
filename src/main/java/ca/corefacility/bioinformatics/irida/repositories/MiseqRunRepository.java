
package ca.corefacility.bioinformatics.irida.repositories;

import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface MiseqRunRepository extends CRUDRepository<Long, MiseqRun>{
    
    public void addSequenceFileToMiseqRun(MiseqRun run, SequenceFile file);
}
