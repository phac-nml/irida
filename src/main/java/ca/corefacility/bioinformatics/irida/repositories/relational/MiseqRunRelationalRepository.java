
package ca.corefacility.bioinformatics.irida.repositories.relational;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.impl.MiseqRunSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.repositories.MiseqRunRepository;
import java.util.List;
import javax.sql.DataSource;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class MiseqRunRelationalRepository extends GenericRelationalRepository<MiseqRun> implements MiseqRunRepository{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MiseqRunRelationalRepository.class); 

    public MiseqRunRelationalRepository(){}
    
    public MiseqRunRelationalRepository(DataSource source){
        super(source, MiseqRun.class);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MiseqRunSequenceFileJoin addSequenceFileToMiseqRun(MiseqRun run, SequenceFile file) throws EntityExistsException{
        Session session = sessionFactory.getCurrentSession();
        
        Criteria query = session.createCriteria(MiseqRunSequenceFileJoin.class).add(Restrictions.eq("sequenceFile", file));
        
        List<MiseqRunSequenceFileJoin> list = query.list();
        if (!list.isEmpty()) {
            logger.error("Cannot add sequence file to miseq run because it already exists in a run");
            throw new EntityExistsException("This sequence file already belongs to a miseq run");
        }

        MiseqRunSequenceFileJoin miseqRunSequenceFileJoin = new MiseqRunSequenceFileJoin(run, file);

        session.persist(miseqRunSequenceFileJoin);

        return miseqRunSequenceFileJoin;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public MiseqRunSequenceFileJoin getMiseqRunForSequenceFile(SequenceFile file){
        Session session = sessionFactory.getCurrentSession();
        Criteria query = session.createCriteria(MiseqRunSequenceFileJoin.class).add(Restrictions.eq("sequenceFile", file));
        
        List<MiseqRunSequenceFileJoin> list = query.list();
        
        if(list.isEmpty()){
            logger.error("No miseq run found for sequence file" + file.getId());
            throw new EntityNotFoundException("No miseq run found for sequence file " + file.getId());
        }
        else if(list.size() > 1){
            logger.error("Multiple miseq runs found for sequence file.");
            throw new StorageException("Multiple miseq runs found for sequence file " + file.getId());
        }
        
        return list.get(0);
    }

}
