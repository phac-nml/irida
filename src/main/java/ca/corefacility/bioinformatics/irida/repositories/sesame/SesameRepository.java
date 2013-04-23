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
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import java.util.Map;
import java.util.Set;
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
public abstract class SesameRepository {
    
    TripleStore store;
    String URI;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SesameRepository.class);

    public SesameRepository(){};
    
    public SesameRepository(TripleStore store,Class type){
        this.store = store;
        URI = store.getURI() + type.getSimpleName() + "/";
    }      
    
    public static String getParameters(String subject,Map<String,String> pmap){
        subject = "?" + subject;
        
        StringBuilder params = new StringBuilder();
        
        Set<String> keys = pmap.keySet();
        for(String key : keys){
            String val = pmap.get(key);
            params.append(subject).append(" ").append(key).append(" ").append(val).append(". \n");
            //params += subject + " " + key + " " + val + ". \n";
        }
        
        return params.toString();
    }
    
    public int count(String ns, String type){
        int count = 0;
        
        RepositoryConnection con = store.getRepoConnection();
        
        try {
            String qs = store.getPrefixes()
                    + "SELECT (count(?s) as ?c) \n"
                    + "WHERE{ ?s a ?type . \n"
                    + "}\n";
            
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            
            URI vtype = con.getValueFactory().createURI(con.getNamespace(ns), type);
            tupleQuery.setBinding("type", vtype);
            
            TupleQueryResult result = tupleQuery.evaluate();
            
            BindingSet bindingSet = result.next();
            Value countval = bindingSet.getValue("c");
            count = Integer.parseInt(countval.stringValue());
                      
            result.close();
    
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't run count query");
        }
        finally{
            try {
                con.close();
            } catch (RepositoryException ex) {
                logger.error(ex.getMessage());
                throw new StorageException("Couldn't close connection");
            }
        }
            
        return count;
    }
    
    public Boolean exists(Identifier id,String ns, String type) {        
        boolean exists = false;
        RepositoryConnection con = store.getRepoConnection();

        try {
            String uri = id.getUri().toString();
            
            
            String querystring = store.getPrefixes()
                    + "ASK\n"
                    + "{?uri a ?type}";
            
            BooleanQuery existsQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL, querystring);

            ValueFactory vf = con.getValueFactory();
            URI objecturi = vf.createURI(uri);
            existsQuery.setBinding("uri", objecturi);

            URI typeuri = vf.createURI(con.getNamespace(ns),type);
            existsQuery.setBinding("type", typeuri);

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
    
    public void close() {
        //maybe eventually do stuff
    }    
}
