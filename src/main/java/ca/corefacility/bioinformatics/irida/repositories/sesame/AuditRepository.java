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
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.alibaba.AuditIF;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.result.Result;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class AuditRepository {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuditRepository.class);
    
    private TripleStore store;
    private String URI;
    
    public AuditRepository(){}
    
    public AuditRepository(TripleStore store){
        this.store = store;
        this.URI = store.getURI() + "Audit/";
    }
    
    public String getAuditURI(String uri) throws RepositoryException{
        String aURI = null;
                
        ObjectConnection con = store.getRepoConnection();

        try {
            
            String querystring = store.getPrefixes()
                    + "SELECT ?auri\n"
                    + "{?auri a irida:Audit .\n"
                    + "?auri irida:forResource ?ouri ."
                    + "}";
            
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, querystring);

            ValueFactory vf = con.getValueFactory();
            URI ouri = vf.createURI(uri);
            
            query.setBinding("ouri",ouri);
            
            TupleQueryResult result = query.evaluate();
            
            if(result.hasNext()){
                BindingSet ret = result.next();
                Value val = ret.getValue("auri");
                aURI = val.stringValue();
            }
            else{
                aURI = URI + UUID.randomUUID().toString();

                ValueFactory fac = con.getValueFactory();
                URI pred = fac.createURI(con.getNamespace("irida"), "forResource");
                URI aURIo = fac.createURI(aURI);
                Statement st = fac.createStatement(aURIo, pred, ouri);

                con.add(st);
            }
            
            result.close();
            
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
        
        
        return aURI;
    }
    
    
    public void audit(AuditIF audit,String objectURI){
        ObjectConnection con = store.getRepoConnection();
        
        try {
            String aURI = getAuditURI(objectURI);
            con.addObject(aURI, audit);
            
        } catch (RepositoryException ex) {
            Logger.getLogger(AuditRepository.class.getName()).log(Level.SEVERE, null, ex);
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
    
    public Audit getAudit(URI uri){
        Audit ret = null;
        
        ObjectConnection con = store.getRepoConnection();

        try {
            
            String querystring = store.getPrefixes()
                    + "SELECT ?auri\n"
                    + "{?auri a irida:Audit .\n"
                    + "?auri irida:forResource ?ouri ."
                    + "}";
            
            ObjectQuery query = con.prepareObjectQuery(QueryLanguage.SPARQL, querystring);

            ValueFactory vf = con.getValueFactory();
            query.setBinding("ouri",uri);
            Result<AuditIF> result = query.evaluate(AuditIF.class);
            
            if(result.hasNext()){
                AuditIF next = result.next();
                
                ret = new Audit();
                ret.setCreated(next.getCreated());
                ret.setUpdated(next.getUpdated());
            }
            
            result.close();
            
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
        
        return ret;
        
    }
}
