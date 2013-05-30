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
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
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
public class AuditRepository extends SesameRepository{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AuditRepository.class);
    
    public AuditRepository(){}
    
    public AuditRepository(TripleStore store){
        super(store, "Audit");
    }
    
    /**
     * Get the audit URI for a given object
     * @param uri The URI of the object to find an audit object for
     * @return The String URI of the audit object
     * @throws RepositoryException
     */
    public String getAuditURI(ObjectConnection con,String uri) throws RepositoryException{
        /*String aURI = null;
                
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
        
        
        return aURI;*/
        String aURI = null;
                
        //ObjectConnection con = store.getRepoConnection();

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
                String parentURI = val.stringValue();
                
                ValueFactory fac = con.getValueFactory();
                URI pred = fac.createURI(con.getNamespace("irida"), "subAudit");
                URI parentURIo = fac.createURI(parentURI);
                aURI = URI + UUID.randomUUID().toString();
                URI aURIo = fac.createURI(aURI);
                Statement st = fac.createStatement(parentURIo, pred, aURIo);
                con.add(st);
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

        }
        
        
        return aURI;        
    }
    
    public void audit(Audit audit,Identifier identifier,Map<String,Value> originalFields){
        java.net.URI uriFromIdentifier = getUriFromIdentifier(identifier);
        audit(audit,uriFromIdentifier.toString(),originalFields);
    }
    
    public void audit(Audit audit,String objectURI,Map<String,Value> originalFields){
        ObjectConnection con = store.getRepoConnection();
        
        try {
            con.begin();
            String aURI = getAuditURI(con,objectURI);
            con.addObject(aURI, audit);
            if(originalFields != null){
                createVersion(con,aURI,objectURI,originalFields);
            }

            con.commit();
            
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
    
    public Audit getAudit(Identifier identifier){
        java.net.URI uriFromIdentifier = getUriFromIdentifier(identifier);
        return getAudit(uriFromIdentifier.toString());
    }
    
    public Audit getAudit(String strURI){
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
            URI uri = vf.createURI(strURI);
            query.setBinding("ouri",uri);
            Result<Audit> result = query.evaluate(Audit.class);
            
            if(result.hasNext()){
                Audit next = result.next();
                URI auri = vf.createURI(next.toString());
                ret = next.copy();
                ret.setUpdates(getUpdates(auri));
            }
            
            result.close();
            
        } catch (RepositoryException |MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't run exists query"); 
        }
        finally{
            store.closeRepoConnection(con);
        }
        
        return ret;   
    }
    
    public List<Audit> getUpdates(URI uri){
        List<Audit> subAudits = new ArrayList<>();
        
        ObjectConnection con = store.getRepoConnection();

        try {
            
            String querystring = store.getPrefixes()
                    + "SELECT ?suburi\n"
                    + "{?auri a irida:Audit .\n"
                    + "?auri irida:subAudit ?suburi ."
                    + "}";
            
            ObjectQuery query = con.prepareObjectQuery(QueryLanguage.SPARQL, querystring);

            query.setBinding("auri",uri);
            Result<Audit> result = query.evaluate(Audit.class);
            
            
            while(result.hasNext()){
                Audit next = result.next();
                
                Audit ret = next.copy();
                subAudits.add(ret);
            }
            
            result.close();
            
        } catch (RepositoryException |MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't run exists query"); 
        }
        finally{
            store.closeRepoConnection(con);
        }
        
        return subAudits;        
    }
    
    public void createVersion(ObjectConnection con, String auditURI, String objectURI, Map<String,Value> oldValues){
        
        try{

            ValueFactory fac = con.getValueFactory();
            URI audit = fac.createURI(auditURI);
            URI pred = fac.createURI(con.getNamespace("irida"), "originalValues");
            BNode versionURI = fac.createBNode();
            Statement st = fac.createStatement(audit, pred, versionURI);

            con.add(st);

            for(String key : oldValues.keySet()){
                URI versionPred = fac.createURI(key);
                Value get = oldValues.get(key);
                Statement vSt = fac.createStatement(versionURI, versionPred, get);
                con.add(vSt);
            }
        
        }
        catch (RepositoryException ex) {
            logger.error("Couldn't create original values for object: " + ex.getMessage());
            throw new StorageException("Couldn't create original values for object: " + ex.getMessage());
        }
    }    
}
