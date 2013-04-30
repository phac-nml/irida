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

import ca.corefacility.bioinformatics.irida.dao.TripleStore;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.alibaba.UserIF;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.result.Result;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class UserSesameRepository extends GenericRepository<UserIdentifier,UserIF, User> implements UserRepository {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserSesameRepository.class);

    public UserSesameRepository() {
    }

    public UserSesameRepository(TripleStore store) {
        super(store, UserIF.class,User.PREFIX,User.TYPE);

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
    public Identifier buildIdentifier(UserIF obj, String identifiedBy) {
        UserIdentifier objid = new UserIdentifier();
        
        objid.setUri(java.net.URI.create(obj.toString()));
        objid.setIdentifier(obj.getUsername());
        
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

        ObjectConnection con = store.getRepoConnection();
        try {

            String qs = store.getPrefixes()
                    + "SELECT * "
                    + "WHERE{ ?s a foaf:Person . \n"
                    + "?s foaf:nick ?username . \n"
                    + "}";
            

            ObjectQuery tupleQuery = con.prepareObjectQuery(QueryLanguage.SPARQL, qs);
            ValueFactory fac = con.getValueFactory();

            Literal u = fac.createLiteral(username);
            tupleQuery.setBinding("username", u);
            
            Result<UserIF> result = tupleQuery.evaluate(UserIF.class);
            UserIF o = result.next();
            
            URI uri = fac.createURI(o.toString());
                        
            String identifiedBy = getIdentifiedBy(con,uri);
            Identifier objid = buildIdentifier(o,identifiedBy);
            ret = buildObject(o,(UserIdentifier)objid);

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

    @Override
    public User buildObject(UserIF base, UserIdentifier i) {
        User u = new User();
        u.setIdentifier(i);
        u.setEmail(base.getEmail());
        u.setFirstName(base.getFirstName());
        u.setLastName(base.getLastName());
        u.setPassword(base.getPassword());
        u.setPhoneNumber(base.getPhoneNumber());
        u.setUsername(base.getUsername());
        
        return u;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<User> getUsersForProject(Project project) {
        List<User> users = new ArrayList<>();

        String uri = project.getIdentifier().getUri().toString();

        ObjectConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT ?s "
                    + "WHERE{ ?p a irida:Project . \n"
                    + "?p irida:hasUser ?s . \n"
                    + "}\n";

            ObjectQuery query = con.prepareObjectQuery(QueryLanguage.SPARQL, qs);
            URI puri = con.getValueFactory().createURI(uri);
            query.setBinding("p", puri);

            Result<UserIF> result = query.evaluate(UserIF.class);
            while (result.hasNext()) {
                UserIF o = result.next();
                URI u = con.getValueFactory().createURI(o.toString());

                User ret = buildObjectFromResult(o, u, con);
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
