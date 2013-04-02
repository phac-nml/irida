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
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import java.util.List;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class UserSesameRepository implements CRUDRepository<Identifier, User> {
    
    TripleStore store;
    String URI;
    
    public UserSesameRepository(){}
    
    public UserSesameRepository(TripleStore store){
        this.store = store;
        URI = store.getURI() + "User/";
    } 
    
    @Override
    public User create(User object) throws IllegalArgumentException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        RepositoryConnection con = store.getRepoConnection();

        String id;
        
        if(!checkUsernameExists(object.getUsername())){
            Identifier objid = new Identifier();
            
            id = URI + objid.getUUID().toString();
            
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
            
            addUser(object,uri,con);

            con.commit();
            
            con.close();

        } catch (RepositoryException ex) {
            System.out.println(ex.getMessage());
        }
        
        return object;     
    }

    @Override
    public User read(Identifier id) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public User update(User object) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Identifier id) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<User> list() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean exists(Identifier id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void addUser(User user, URI uri, RepositoryConnection con) throws RepositoryException{
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

        try {
            String uri = getURI(username);
            
            RepositoryConnection con = store.getRepoConnection();
            
            String querystring = store.getPrefixes()
                    + "ASK\n"
                    + "{?uri a ?type}";
            
            BooleanQuery existsQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL, querystring);

            ValueFactory vf = con.getValueFactory();
            URI objecturi = vf.createURI(uri);
            existsQuery.setBinding("uri", objecturi);

            URI typeuri = vf.createURI(con.getNamespace("foaf"),"Person");
            existsQuery.setBinding("type", typeuri);

            exists = existsQuery.evaluate();
            
            return exists;
        } catch (RepositoryException |MalformedQueryException | QueryEvaluationException ex) {
            System.err.println(ex.getMessage());
        }
        
        return exists;
    }    
    
    private String getURI(String username) {
        String uri = URI+username;
        return uri;
    }    
}
