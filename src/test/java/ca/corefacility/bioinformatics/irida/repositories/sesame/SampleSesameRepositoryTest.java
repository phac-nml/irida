package ca.corefacility.bioinformatics.irida.repositories.sesame;

import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.SailStore;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link SampleSesameRepository}.
 */
public class SampleSesameRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(SampleSesameRepositoryTest.class);
    private SampleSesameRepository repository;
    private SailStore store;

    @Before
    public void setUp() {
        store = new SailStore();
        store.initialize();
        IdentifierGenerator<Sample> idGen = new IdentifierGenerator<>(store);

        AuditRepository auditRepo = new AuditRepository(store);
        RelationshipSesameRepository linksRepo = new RelationshipSesameRepository(store, auditRepo);

        repository = new SampleSesameRepository(store, auditRepo, linksRepo);
        repository.setIdGen(idGen);
    }

    /**
     * When a {@link Sample} is persisted to the database it should have a type associated with it.
     *
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    @Test
    public void testPersistSample() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        String sampleName = "sampleName";
        Sample s = new Sample();
        s.setSampleName(sampleName);

        s = repository.create(s);
        Identifier id = s.getIdentifier();
        logger.debug("Persisted identifier is: [" + id + "]");

        /** verify that the identifier exists in the database */
        ObjectConnection connection = store.getRepoConnection();
        TupleQuery query = connection.prepareTupleQuery(
                "select * where {?subject <http://corefacility.ca/irida/identifier> ?identifier}");
        ValueFactory valueFactory = connection.getValueFactory();
        query.setBinding("identifier", valueFactory.createLiteral(id.getIdentifier()));
        TupleQueryResult result = query.evaluate();
        assertTrue(result.hasNext());
        BindingSet bindingSet = result.next();
        assertEquals(id.getIdentifier(), bindingSet.getValue("identifier").stringValue());
        /******************************************************/

        /** verify that the entity exists in the database as a type of something */
        query = connection.prepareTupleQuery(
                "select * where {?identifier a ?type}");
        query.setBinding("identifier", valueFactory.createURI(id.getUri().toString()));
        result = query.evaluate();
        assertTrue(result.hasNext());
        bindingSet = result.next();
        assertEquals(Sample.PREFIX + Sample.TYPE, bindingSet.getValue("type").stringValue());
        /*************************************************************************/
    }

    /**
     * When a {@link Sample} is persisted to the database it must have a label associated with it.
     *
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    @Test
    public void testPersistSampleWithLabel()
            throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        String sampleName = "sampleName";
        Sample s = new Sample();
        s.setSampleName(sampleName);

        s = repository.create(s);
        Identifier id = s.getIdentifier();

        /** verify that the persisted sample has a label */
        ObjectConnection connection = store.getRepoConnection();
        TupleQuery query = connection.prepareTupleQuery(
                "select * where {?identifier <http://www.w3.org/2000/01/rdf-schema#label> ?label}");
        ValueFactory valueFactory = connection.getValueFactory();
        query.setBinding("identifier", valueFactory.createURI(id.getUri().toString()));
        TupleQueryResult result = query.evaluate();
        assertTrue(result.hasNext());
        BindingSet bindingSet = result.next();
        assertEquals(sampleName, bindingSet.getValue("label").stringValue());
        /*************************************************/
    }
}
