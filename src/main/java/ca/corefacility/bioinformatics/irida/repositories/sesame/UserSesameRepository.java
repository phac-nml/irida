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

import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.TripleStore;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.openrdf.annotations.Iri;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.result.Result;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class UserSesameRepository extends GenericRepository<UserIdentifier, User> implements UserRepository {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserSesameRepository.class);
    private static final RdfPredicate hasUser = new RdfPredicate("irida", "hasUser");
    private static final RdfPredicate hasProject = new RdfPredicate("irida", "hasProject");



    public UserSesameRepository() {
    }

    public UserSesameRepository(TripleStore store,AuditRepository auditRepo,RelationshipSesameRepository linksRepo) {
        super(store, User.class,User.PREFIX,User.TYPE,auditRepo,linksRepo);

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
     * Update a {@link User} object.
     * This override method exists to update the roles of a user, then passes to the superclass
     * @param id The {@link UserIdentifier} of the user to update
     * @param updatedFields The fields to update
     * @return A {@link User} containing the updated fields
     * @throws InvalidPropertyException 
     */
    @Override
    public User update(UserIdentifier id, Map<String, Object> updatedFields) throws InvalidPropertyException {

        if(updatedFields.containsKey("roles")){
            Collection<Role> roles = (Collection<Role>) updatedFields.get("roles");
            
            Method declaredMethod;
            try {
                declaredMethod = User.class.getDeclaredMethod("getStringRoles");
            } catch (NoSuchMethodException | SecurityException ex) {
                logger.error("No field roles exists.  Cannot update object.");
                throw new InvalidPropertyException("No field named roles exists for this object type");            
            }

            Iri annotation = declaredMethod.getAnnotation(Iri.class);

            logger.trace("Updating roles -- " + annotation.value());
            
            updateRoleField(id, annotation.value(), roles);
            
            updatedFields.remove("roles");
        }
        
        return super.update(id, updatedFields);
   
    }
    
    /**
     * Update the role field for a User
     * @param id The {@link UserIdentifier} of the user to update
     * @param predicate The predicate to update from the @Iri annotation on the User class
     * @param roles The roles to update this user with
     */
    protected void updateRoleField(UserIdentifier id, String predicate, Collection<Role> roles) {
        ObjectConnection con = store.getRepoConnection();
        java.net.URI netURI = idGen.buildURIFromIdentifier(id,URI);
        String uri = netURI.toString();

        try {
            con.begin();
            ValueFactory fac = con.getValueFactory();
            URI subURI = fac.createURI(uri);
            URI predURI = fac.createURI(predicate);
            
            RepositoryResult<Statement> curvalues = con.getStatements(subURI, predURI, null);
            while (curvalues.hasNext()) {
                Statement next = curvalues.next();
                logger.trace("current value: " + next.getObject().stringValue());
            }
            con.remove(subURI, predURI, null);            

            for(Role r : roles){
                Literal objValue = fac.createLiteral(r.getName());
                Statement added = fac.createStatement(subURI, predURI, objValue);
                con.add(added);                
            }

            con.commit();
        } catch (RepositoryException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to update field");
        } finally {
            store.closeRepoConnection(con);
        }
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
            
            Result<User> result = tupleQuery.evaluate(User.class);
            User o = result.next();
            
            URI uri = fac.createURI(o.toString());
            
            ret = buildObjectFromResult(o, uri, con);
            
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to get user " + username);
        } finally {
            store.closeRepoConnection(con);
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
        ObjectConnection con = store.getRepoConnection();

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
            store.closeRepoConnection(con);
        }

        return exists;
    }

    /**
     * {@inheritDoc}
     */
    
    public Collection<User> getUsersForProject(Project  project) {
        List<Identifier> userIds = linksRepo.listSubjects(project.getIdentifier(), hasProject);
        
        Collection<User> users = readMultiple(userIds);
        
        return users;
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public Collection<Relationship> getUsersForProject(Identifier project){
        return linksRepo.getLinks(project, hasUser, null);
    }
    
    public Collection<Identifier> listProjectsForUser(Project project){
        List<Identifier> projIds = linksRepo.listSubjects(project.getIdentifier(), hasProject);
        return projIds;
    }    
}
