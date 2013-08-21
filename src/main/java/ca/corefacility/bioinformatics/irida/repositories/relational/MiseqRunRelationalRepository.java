
package ca.corefacility.bioinformatics.irida.repositories.relational;

import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.repositories.MiseqRunRepository;
import javax.sql.DataSource;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class MiseqRunRelationalRepository extends GenericRelationalRepository<MiseqRun> implements MiseqRunRepository{
    
    public MiseqRunRelationalRepository(){}
    
    public MiseqRunRelationalRepository(DataSource source){
        super(source, MiseqRun.class);
    }

    @Override
    public void addSequenceFileToMiseqRun(MiseqRun run, SequenceFile file) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
