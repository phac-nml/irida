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

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.TripleStore;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class SesameRepository {
private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuditRepository.class);
    
    protected TripleStore store;
    protected String URI;
    
    public SesameRepository(){}
    
    public SesameRepository(TripleStore store,String uriType){
        this.store = store;
        this.URI = store.getURI() + uriType + "/";
    }

    public java.net.URI getUriFromIdentifier(Identifier identifier){
        
        java.net.URI uri = null;
        if(identifier.getUri() != null){
            uri = identifier.getUri();
        }
        else{
            try {
                String uriStr = getUriFromIdentifiedBy(identifier.getIdentifier());
                uri = new java.net.URI(uriStr);
            } catch (    RepositoryException | QueryEvaluationException | MalformedQueryException | URISyntaxException ex) {
                Logger.getLogger(GenericRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return uri;
    }
    
    public String getUriFromIdentifiedBy(String id) throws RepositoryException, QueryEvaluationException, MalformedQueryException{
        String uri;
        
        ObjectConnection con = store.getRepoConnection();
        
        String qs = store.getPrefixes()
                + "SELECT ?s "
                + "WHERE{ ?s irida:identifier ?id . \n"
                + "}";
        
        TupleQuery query = con.prepareTupleQuery(qs);
        ValueFactory fac = con.getValueFactory();
        Literal idLit = fac.createLiteral(id);
        
        query.setBinding("id", idLit);
        TupleQueryResult result = query.evaluate();
        BindingSet bs = result.next();
        
        uri = bs.getBinding("s").getValue().stringValue();
        
        result.close();
        
        con.close();
        
        return uri;
    }
    
    public Boolean identifierExists(String id) {
        
        boolean exists = false;
        ObjectConnection con = store.getRepoConnection();

        try {
            
            String querystring = store.getPrefixes()
                    + "ASK\n"
                    + "{?uri irida:identifier ?id}";
            
            BooleanQuery existsQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL, querystring);

            ValueFactory vf = con.getValueFactory();
            Literal idLit = vf.createLiteral(id);
            existsQuery.setBinding("id", idLit);

            exists = existsQuery.evaluate();
            
            
        } catch (RepositoryException |MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't run exists query"); 
        }
        finally{
            store.closeRepoConnection(con);
        }   
        
        return exists;     
    }
    
    /**
     * Retrieve the String that uniquely identifies this object
     * @param con The object connection to retrieve with
     * @param uri The subject whose identifier to retrieve
     * @return The string that uniqely identifies this object in the database
     * @throws MalformedQueryException
     * @throws RepositoryException
     * @throws QueryEvaluationException
     */
    public String getIdentifiedBy(ObjectConnection con, URI uri) throws MalformedQueryException, RepositoryException, QueryEvaluationException{
        String id;
        
        String qs = store.getPrefixes()
                + "SELECT ?id "
                + "WHERE{ ?s irida:identifier ?id . \n"
                + "}";
        
        TupleQuery query = con.prepareTupleQuery(qs);
        query.setBinding("s", uri);
        TupleQueryResult result = query.evaluate();
        BindingSet bs = result.next();
        
        id = bs.getBinding("id").getValue().stringValue();
        result.close();
        
        return id;
    }
    
    public String getLabel(ObjectConnection con, URI uri) throws RepositoryException, MalformedQueryException, QueryEvaluationException{
        String label;
        
        String qs = store.getPrefixes()
                + "SELECT ?label "
                + "WHERE{ ?s rdfs:label ?label . }";
        
        TupleQuery query = con.prepareTupleQuery(qs);
        query.setBinding("s", uri);
        TupleQueryResult result = query.evaluate();
        BindingSet bs = result.next();
        
        label = bs.getBinding("label").getValue().stringValue();
        result.close();
        
        return label;        
    }
    
    /**
     * Generate an identifier for an object of type
     * <code>Type</code>.
     *
     * @param t the object to generate the identifier for.
     * @return and identifier for the object.
     */
    public Identifier generateNewIdentifier() {
        UUID uuid = UUID.randomUUID();
        java.net.URI objuri = buildURIFromIdentifiedBy(uuid.toString());
        return new Identifier(objuri, uuid);
    }   
    
    /**
     * Set a unique identifier for this object in the database
     * @param con The <code>ObjectConnection</code> to use to add
     * @param uri The URI to add this identifier to
     * @param id The string identifier to add to the object
     * @throws RepositoryException
     */
    public void setIdentifiedBy(ObjectConnection con, URI uri, String id) throws RepositoryException{
        ValueFactory vf = con.getValueFactory();
        Literal litId = vf.createLiteral(id);
        URI pred = vf.createURI(con.getNamespace("irida"), "identifier");
        Statement stmt = vf.createStatement(uri, pred, litId);
        con.add(stmt);     
    }    
    
    /**
     * Build a URI from a given String ID
     *
     * @param id The ID to build a URI for
     * @return The constructed URI
     */
    public java.net.URI buildURIFromIdentifiedBy(String id) {
        java.net.URI uri = java.net.URI.create(URI + id);

        return uri;
    }
    
    public java.net.URI buildURIFromIdentifier(Identifier identifier){
        java.net.URI uri;
        if(identifier.getUri() != null){
            uri = identifier.getUri();
        }
        else{
            uri = buildURIFromIdentifiedBy(identifier.getIdentifier());
        }
        
        return uri;        
    }
}
