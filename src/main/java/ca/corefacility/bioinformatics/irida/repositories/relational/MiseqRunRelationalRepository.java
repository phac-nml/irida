
package ca.corefacility.bioinformatics.irida.repositories.relational;

import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.impl.MiseqRunSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.repositories.MiseqRunRepository;
import javax.sql.DataSource;
import org.hibernate.Session;

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
    public MiseqRunSequenceFileJoin addSequenceFileToMiseqRun(MiseqRun run, SequenceFile file) {
        Session session = sessionFactory.getCurrentSession();

        MiseqRunSequenceFileJoin miseqRunSequenceFileJoin = new MiseqRunSequenceFileJoin(run, file);

        session.persist(miseqRunSequenceFileJoin);

        return miseqRunSequenceFileJoin;
    }

}
