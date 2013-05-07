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
            try {
                con.close();
            } catch (RepositoryException ex) {
                logger.error(ex.getMessage());
                throw new StorageException("Couldn't close connection");
            }
        }   
        
        return exists;     
    }
    
    /**
     * Generate an identifier for an object of type
     * <code>Type</code>.
     *
     * @param t the object to generate the identifier for.
     * @return and identifier for the object.
     */
    public Identifier generateIdentifier() {
        UUID uuid = UUID.randomUUID();
        java.net.URI objuri = buildURI(uuid.toString());
        return new Identifier(objuri, uuid);
    }  
    
    /**
     * Build a URI from a given String ID
     *
     * @param id The ID to build a URI for
     * @return The constructed URI
     */
    public java.net.URI buildURI(String id) {
        java.net.URI uri = java.net.URI.create(URI + id);

        return uri;
    }    
}
