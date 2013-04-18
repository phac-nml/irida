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

import ca.corefacility.bioinformatics.irida.dao.SparqlQuery;
import ca.corefacility.bioinformatics.irida.dao.TripleStore;
import ca.corefacility.bioinformatics.irida.dao.UserResultExtractor;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.exceptions.user.UserNotFoundException;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
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
public class UserSesameRepository extends SesameRepository implements UserRepository{
    
    public final static HashMap<String,String> userParams = new HashMap<>();
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserSesameRepository.class);
    
    public UserSesameRepository(){}
    
    static{
        userParams.put("foaf:nick", "?username");
        userParams.put("foaf:mbox","?email");
        userParams.put("foaf:firstName","?firstName");
        userParams.put("foaf:lastName","?lastName");
        userParams.put("foaf:phone","?phoneNumber");
    }
    
    public UserSesameRepository(TripleStore store){
        super(store,User.class);
    } 
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public User create(User object) throws IllegalArgumentException {        
        if(object == null){
            throw new IllegalArgumentException("User is null");
        }

           
        
        RepositoryConnection con = store.getRepoConnection();

        String id = URI + object.getUsername();
        java.net.URI objuri = java.net.URI.create(id);
            
        Identifier objid = new Identifier(objuri);        
        
        if(!exists(objid)){            
            object.setIdentifier(objid);            
        }
        else{
            throw new IllegalArgumentException("User with this username already exists.");
        }        
        
        try {
            con.begin();

            ValueFactory fac = con.getValueFactory();
            URI uri = fac.createURI(id);
            
            //add type
            URI pred = fac.createURI(con.getNamespace("rdf"), "type");
            Value name = fac.createURI(con.getNamespace("foaf"),"Person");
            Statement st = fac.createStatement(uri, pred, name);
            con.add(st);            
            
            addUserProperties(object,uri,con);

            con.commit();
            
        } catch (RepositoryException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to create resource");
        }
        finally{
            try {
                con.close();
            } catch (RepositoryException ex) {
                logger.error(ex.getMessage());
                throw new StorageException("Couldn't close connection");
            }
        }
        
        return object;     
    }   
    
    /**
     * {@inheritDoc}
     */
    @Override
    public User read(Identifier id) throws UserNotFoundException {        
        User ret = null;

        String uri = id.getUri().toString();
        
        if(!exists(id)){
            throw new UserNotFoundException("No such user with the given URI exists.");
        }
        
        RepositoryConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT * "
                    + "WHERE{ ?s a foaf:Person . \n"
                    + UserSesameRepository.getParameters("s",userParams)
                    + "}";

            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            ValueFactory fac = con.getValueFactory();
            URI u = fac.createURI(uri);
            tupleQuery.setBinding("s", u);
            
            TupleQueryResult result = tupleQuery.evaluate();
            BindingSet bindingSet = result.next();

            Value s = bindingSet.getValue("s");
            
            Identifier objid = new Identifier(java.net.URI.create(s.stringValue()));
            ret = UserResultExtractor.extractData(objid, bindingSet);
                            
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to read resource");        
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
     * {@inheritDoc}
     */
    @Override
    public User getUserByUsername(String username) throws UserNotFoundException{
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
                    + UserSesameRepository.getParameters("s",mySet)
                    + "}";
            
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            ValueFactory fac = con.getValueFactory();
            
            Literal u = fac.createLiteral(username);
            tupleQuery.setBinding("username", u);
            
            TupleQueryResult result = tupleQuery.evaluate();
            BindingSet bindingSet = result.next();

            Value s = bindingSet.getValue("s");
            
            Identifier objid = new Identifier(java.net.URI.create(s.stringValue()));
            ret = UserResultExtractor.extractData(objid, bindingSet);
                            
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
     * {@inheritDoc}
     */
    @Override
    public User update(User object) throws IllegalArgumentException {
        //User u = read(object.getIdentifier());
        //backupUser(u);
        
        delete(object.getIdentifier());
        
        object = create(object);
        
        return object;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Identifier id) throws IllegalArgumentException {        
        if(exists(id)){
            RepositoryConnection con = store.getRepoConnection();
            
            String uri = id.getUri().toString();

            ValueFactory vf = con.getValueFactory();
            URI objecturi = vf.createURI(uri);
            
            try {
                con.remove(objecturi, null, null);
                con.close();
                
            } catch (RepositoryException ex) {
                logger.error(ex.getMessage());
                throw new StorageException("Failed to remove object" + id);             
            }
            finally{
                try {
                    con.close();
                } catch (RepositoryException ex) {
                    logger.error(ex.getMessage());
                    throw new StorageException("Failed to close connection");                 
                }
            }
        }
        else{
            throw new IllegalArgumentException("User does not exist in the database.");
        }        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> list() {
        return list(0,0,null,null);
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public List<User> list(int page, int size, String sortProperty, Order order) {
        List<User> users = new ArrayList<>();
        
        RepositoryConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT * "
                    + "WHERE{ ?s a foaf:Person . \n"
                    + UserSesameRepository.getParameters("s",userParams)
                    + "}\n";
            
            qs += SparqlQuery.setOrderBy(sortProperty, order);
            qs += SparqlQuery.setLimitOffset(page, size);
            
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);

            TupleQueryResult result = tupleQuery.evaluate();
            while(result.hasNext()){
                BindingSet bindingSet = result.next();
                Value s = bindingSet.getValue("s");

                Identifier objid = new Identifier(java.net.URI.create(s.stringValue()));
                User ret = UserResultExtractor.extractData(objid, bindingSet);
                
                users.add(ret);
            }
            result.close();    
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
                logger.error(ex.getMessage());
                throw new StorageException("Failed to list users");         
        }
        finally{
            try {
                con.close();
            } catch (RepositoryException ex) {
                logger.error(ex.getMessage());
                throw new StorageException("Failed to close connection");             
            }
        }
        
        return users;    }    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean exists(Identifier id) {        
        return super.exists(id, "foaf", "Person");        
    }
    
    /**
     * Add the properties of a project to the database
     * 
     * @param user The user whose properties we want to add
     * @param uri The URI of the user resource to add to
     * @param con The repository connection to use
     * @throws RepositoryException 
     */
    private void addUserProperties(User user, URI uri, RepositoryConnection con) throws RepositoryException{
        ValueFactory fac = con.getValueFactory(); 

        //add username foaf:nick
        URI pred = fac.createURI(con.getNamespace("foaf"),"nick");
        Value name = fac.createLiteral(user.getUsername());
        Statement st = fac.createStatement(uri, pred, name);
        con.add(st);

        //add foaf:mbox
        pred = fac.createURI(con.getNamespace("foaf"),"mbox");
        name = fac.createLiteral(user.getEmail());
        st = fac.createStatement(uri, pred, name);
        con.add(st);

        //add foaf:firstName
        pred = fac.createURI(con.getNamespace("foaf"),"firstName");
        name = fac.createLiteral(user.getFirstName());
        st = fac.createStatement(uri, pred, name);
        con.add(st);

        //add foaf:lastName
        pred = fac.createURI(con.getNamespace("foaf"),"lastName");
        name = fac.createLiteral(user.getLastName());
        st = fac.createStatement(uri, pred, name);
        con.add(st);
            
        //add foaf:phone
        pred = fac.createURI(con.getNamespace("foaf"),"phone");
        name = fac.createLiteral(user.getPhoneNumber());
        st = fac.createStatement(uri, pred, name);
        con.add(st);            
    }

    /**
     * Check if a user with the given username exists in the database
     * @param username Username to check
     * @return boolean for existence
     */
    public boolean checkUsernameExists(String username){
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
    public Integer count() {
        return super.count("foaf","Person");
    }
 
}
