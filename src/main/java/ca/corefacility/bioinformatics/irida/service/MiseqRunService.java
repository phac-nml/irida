
package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.impl.MiseqRunSequenceFileJoin;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface MiseqRunService extends CRUDService<Long, MiseqRun> {
    public MiseqRunSequenceFileJoin addSequenceFileToMiseqRun(MiseqRun run, SequenceFile file);
    public MiseqRunSequenceFileJoin getMiseqRunForSequenceFile(SequenceFile file);
}
