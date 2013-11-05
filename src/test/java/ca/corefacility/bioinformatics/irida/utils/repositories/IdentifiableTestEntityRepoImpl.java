package ca.corefacility.bioinformatics.irida.utils.repositories;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;

import ca.corefacility.bioinformatics.irida.repositories.relational.GenericRelationalRepository;
import ca.corefacility.bioinformatics.irida.utils.model.IdentifiableTestEntity;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class IdentifiableTestEntityRepoImpl extends GenericRelationalRepository<IdentifiableTestEntity> implements IdentifiableTestEntityRepo{
    
    public IdentifiableTestEntityRepoImpl(DataSource source, SessionFactory sessionFactory){
        super(source, sessionFactory, IdentifiableTestEntity.class);
    }
}
