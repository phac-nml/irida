/*
 * Copyright 2013 Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.corefacility.bioinformatics.irida.repositories.sesame;

import ca.corefacility.bioinformatics.irida.dao.PropertyMapper;
import ca.corefacility.bioinformatics.irida.dao.TripleStore;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import static ca.corefacility.bioinformatics.irida.repositories.sesame.GenericRepository.userParams;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class UserSesameRepository extends GenericRepository<UserIdentifier, User> implements UserRepository {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserSesameRepository.class);

    public UserSesameRepository() {
    }

    public UserSesameRepository(TripleStore store) {
        super(store, User.class);

        PropertyMapper map = new PropertyMapper(User.class, "foaf", "Person");

        try {
            map.addProperty("foaf", "nick", "username", "getUsername", "setUsername", String.class);
            map.addProperty("foaf", "mbox", "mbox", "getEmail", "setEmail", String.class);
            map.addProperty("foaf", "firstName", "firstName", "getFirstName", "setFirstName", String.class);
            map.addProperty("foaf", "lastName", "lastName", "getLastName", "setLastName", String.class);
            map.addProperty("foaf", "phone", "phoneNumber", "getPhoneNumber", "setPhoneNumber", String.class);
            map.addProperty("foaf", "password", "password", "getPassword", "setPassword", String.class);

        } catch (NoSuchMethodException | SecurityException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't build parameters for \"User\"");
        }

        setPropertyMap(map);
    }

    /**
     * Generate an identifier for an object of type
     * <code>Type</code>.
     *
     * @param t the object to generate the identifier for.
     * @return and identifier for the object.
     */
    @Override
    public Identifier generateIdentifier(User t) {
        UUID uuid = UUID.randomUUID();
        java.net.URI objuri = buildURI(t.getUsername());
        UserIdentifier ui = new UserIdentifier(t.getUsername());
        //ui.setUUID(uuid);
        ui.setUri(objuri);
        return ui;
    }

    /**
     * Build an identifier from the given binding set
     *
     * @param bs The binding set to build from
     * @param subject The subject of the SPARQL query to build from
     * @return An Identifier object built form the given binding set
     */
    @Override
    public Identifier buildIdentifier(BindingSet bs, String subject) {
        Value s = bs.getValue(subject);
        Value resid = bs.getValue("resid");
        String id = resid.stringValue();
        Identifier objid = new UserIdentifier(id);
        objid.setUri(java.net.URI.create(s.stringValue()));

        return objid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User create(User u) {
        if (u == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (checkUsernameExists(u.getUsername())) {
            throw new EntityExistsException("Username " + u.getUsername() + " already exists");
        }

        return super.create(u);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserByUsername(String username) throws EntityNotFoundException {

        User ret = null;
        if (!checkUsernameExists(username)) {
            throw new EntityNotFoundException("No user with username [" + username + "] exists.");
        }

        RepositoryConnection con = store.getRepoConnection();
        try {
            HashMap<String, String> mySet = new HashMap<>(userParams);
            mySet.remove("foaf:nick");

            String qs = store.getPrefixes()
                    + "SELECT * "
                    + "WHERE{ ?s a foaf:Person . \n"
                    + "?s foaf:nick ?username . \n"
                    + buildSparqlParams("s", propertyMap)
                    + "}";

            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            ValueFactory fac = con.getValueFactory();

            Literal u = fac.createLiteral(username);
            tupleQuery.setBinding("username", u);

            TupleQueryResult result = tupleQuery.evaluate();
            BindingSet bindingSet = result.next();

            Identifier objid = buildIdentifier(bindingSet, "s");

            ret = extractData(objid, bindingSet);

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to get user " + username);
        } finally {
            try {
                con.close();
            } catch (RepositoryException ex) {
                logger.error(ex.getMessage());
                throw new StorageException("Failed to close connection");
            }
        }

        return ret;
    }

    /**
     * Check whether a username has been used in the system
     *
     * @param username The username to look up
     * @return Whether the username has been used in the system
     */
    public boolean checkUsernameExists(String username) {
        boolean exists = false;
        RepositoryConnection con = store.getRepoConnection();

        try {

            String querystring = store.getPrefixes()
                    + "ASK\n"
                    + "{?s a ?type ."
                    + "?s foaf:nick ?nick}";

            BooleanQuery existsQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL, querystring);

            ValueFactory vf = con.getValueFactory();

            URI typeuri = vf.createURI(con.getNamespace("foaf"), "Person");
            existsQuery.setBinding("type", typeuri);

            Literal nick = vf.createLiteral(username);
            existsQuery.setBinding("nick", nick);

            exists = existsQuery.evaluate();

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed execute ASK query");
        } finally {
            try {
                con.close();
            } catch (RepositoryException ex) {
                logger.error(ex.getMessage());
                throw new StorageException("Failed to close connection");
            }
        }

        return exists;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<User> getUsersForProject(Project project) {
        List<User> users = new ArrayList<>();

        String uri = project.getIdentifier().getUri().toString();

        RepositoryConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT * "
                    + "WHERE{ ?p a irida:Project . \n"
                    + "?p irida:hasUser ?s . \n"
                    + buildSparqlParams("s", propertyMap)
                    + "}\n";

            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            URI puri = con.getValueFactory().createURI(uri);
            tupleQuery.setBinding("p", puri);

            TupleQueryResult result = tupleQuery.evaluate();
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();

                Identifier objid = buildIdentifier(bindingSet, "s");

                User ret = extractData(objid, bindingSet);

                users.add(ret);
            }
            result.close();

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't list project users");
        } finally {
            try {
                con.close();
            } catch (RepositoryException ex) {
                logger.error(ex.getMessage());
                throw new StorageException("Couldn't close connection");
            }
        }

        return users;
    }
}
