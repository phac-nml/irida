package ca.corefacility.bioinformatics.irida.repositories.relational;

import java.util.List;

import javax.sql.DataSource;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.repositories.relational.auditing.UserRevEntity;
 
/**
 * A repository for accessing previous versions of audited entities.
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Repository
@Transactional
public class AuditRepository {
    
    private SessionFactory sessionFactory;
    public AuditRepository(){}
    
    public AuditRepository(DataSource dataSource){
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
        
    public <Type extends IridaThing> Type getVersion(Long id, Integer revision, Class<Type> classType){
        Session ses = sessionFactory.getCurrentSession();

        AuditReader get = AuditReaderFactory.get(ses);
        
        Type find = get.find(classType, id, revision);
        
        return find;
    }
    
    public List<UserRevEntity> getRevisions(Long id, Class<?> classType){
        Session ses = sessionFactory.getCurrentSession();
        AuditReader get = AuditReaderFactory.get(ses);
        
        List<Number> revisions = get.getRevisions(classType, id);
        
        Criteria crit = ses.createCriteria(UserRevEntity.class).add(Restrictions.in("id", revisions));
        @SuppressWarnings("unchecked")
		List<UserRevEntity> list = crit.list();
                
        return list;
    }
    
}
