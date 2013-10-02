
package ca.corefacility.bioinformatics.irida.repositories.relational;

import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SequenceFileOverrepresentedSequenceJoin;
import ca.corefacility.bioinformatics.irida.repositories.OverrepresentedSequenceRepository;
import java.util.List;
import javax.sql.DataSource;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class OverrepresentedSequenceRelationalRepository extends GenericRelationalRepository<OverrepresentedSequence> implements OverrepresentedSequenceRepository{
	public OverrepresentedSequenceRelationalRepository() {
	}

	public OverrepresentedSequenceRelationalRepository(DataSource source, SessionFactory sessionFactory) {
		super(source, sessionFactory, OverrepresentedSequence.class);
	}
	
	public List<Join<SequenceFile,OverrepresentedSequence>> getOverrepresentedSequencesForSequenceFile(SequenceFile sequenceFile){
		Session session = sessionFactory.getCurrentSession();
		Criteria crit = session.createCriteria(SequenceFileOverrepresentedSequenceJoin.class);
		crit.add(Restrictions.eq("sequenceFile", sequenceFile));
		
		List<Join<SequenceFile,OverrepresentedSequence>> list = crit.list();
		return list;
				
	}

}
