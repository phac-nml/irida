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
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.exceptions.user.UserNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import static ca.corefacility.bioinformatics.irida.repositories.sesame.GenericRepository.userParams;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
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
public class UserSesameRepository extends GenericRepository<User> implements UserRepository{
    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserSesameRepository.class);

    public UserSesameRepository(){}
    
    public UserSesameRepository(TripleStore store) {
        super(store,User.class);

        PropertyMapper map = new PropertyMapper("foaf", "Person");
        
        try{
            map.addProperty("foaf","nick","username", User.class.getMethod("getUsername"), User.class.getMethod("setUsername",String.class), String.class);
            map.addProperty("foaf","mbox","mbox", User.class.getMethod("getEmail"), User.class.getMethod("setEmail",String.class), String.class);
            map.addProperty("foaf","firstName","firstName", User.class.getMethod("getFirstName"), User.class.getMethod("setFirstName",String.class), String.class);
            map.addProperty("foaf","lastName","lastName", User.class.getMethod("getLastName"), User.class.getMethod("setLastName",String.class), String.class);
            map.addProperty("foaf","phone","phoneNumber", User.class.getMethod("getPhoneNumber"), User.class.getMethod("setPhoneNumber",String.class), String.class);
        } catch (NoSuchMethodException | SecurityException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't build parameters for \"User\""); 
        }
        
        setPropertyMap(map);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public User create(User u){
        if(u == null){
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if(checkUsernameExists(u.getUsername())){
            throw new IllegalArgumentException("Username " + u.getUsername() + " already exists");
        }
        
        return super.create(u);
        
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public User getUserByUsername(String username) throws UserNotFoundException {
        
        User ret = null;
        if(!checkUsernameExists(username)){
            throw new UserNotFoundException("No user with username [" + username + "] exists.");
        }
        
        RepositoryConnection con = store.getRepoConnection();
        try {
            HashMap<String,String> mySet = new HashMap<>(userParams);
            mySet.remove("foaf:nick");
            
            String qs = store.getPrefixes()
                    + "SELECT * "
                    + "WHERE{ ?s a foaf:Person . \n"
                    + "?s foaf:nick ?username . \n"
                    + buildParams("s",propertyMap)
                    + "}";
            
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            ValueFactory fac = con.getValueFactory();
            
            Literal u = fac.createLiteral(username);
            tupleQuery.setBinding("username", u);
            
            TupleQueryResult result = tupleQuery.evaluate();
            BindingSet bindingSet = result.next();

            Identifier objid = buildIdentifier(bindingSet,"s");
            
            ret = extractData(objid, bindingSet);
                            
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
                logger.error(ex.getMessage());
                throw new StorageException("Failed to get user " + username);          
        }
        finally{
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

            URI typeuri = vf.createURI(con.getNamespace("foaf"),"Person");
            existsQuery.setBinding("type", typeuri);
            
            Literal nick = vf.createLiteral(username);
            existsQuery.setBinding("nick", nick);

            exists = existsQuery.evaluate();
            
        } catch (RepositoryException |MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed execute ASK query");          
        }
        finally{
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
                    + buildParams("s",propertyMap)
                    + "}\n";
            
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            URI puri = con.getValueFactory().createURI(uri);
            tupleQuery.setBinding("p", puri);

            TupleQueryResult result = tupleQuery.evaluate();
            while(result.hasNext()){
                BindingSet bindingSet = result.next();
                
                Identifier objid = buildIdentifier(bindingSet,"s");
                
                User ret = extractData(objid, bindingSet);
                
                users.add(ret);
            }
            result.close();
    
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't list project users"); 
        }       
        finally{
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
