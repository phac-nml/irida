package ca.corefacility.bioinformatics.irida.util;

import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;

import java.io.File;

/**
 * A utility class for exporting all triples in a Sail repository.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class SailExporter {

    public static void main(String[] args) {
        File directory = new File("/tmp/sail");

        NativeStore nativeStore = new NativeStore(directory);
        try {
            SailRepository repository = new SailRepository(nativeStore);
            repository.initialize();
            ObjectRepositoryFactory objectRepositoryFactory = new ObjectRepositoryFactory();
            ObjectRepository objectRepository = objectRepositoryFactory.createRepository(repository);
            ObjectConnection connection = objectRepository.getConnection();
            TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL,
                    "SELECT * WHERE {?subject ?predicate ?object}");
            TupleQueryResult result = query.evaluate();
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                System.out.print("Subject: [" + bindingSet.getValue("subject") + "], ");
                System.out.print("Predicate: [" + bindingSet.getValue("predicate") + "], ");
                System.out.println("Object: [" + bindingSet.getValue("object") + "]");
            }
        } catch (RepositoryConfigException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (RepositoryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (MalformedQueryException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (QueryEvaluationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
