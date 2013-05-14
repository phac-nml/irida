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

import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.SparqlQuery;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.TripleStore;
import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.result.Result;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public abstract class GenericRepository<IDType extends Identifier, TypeIF extends IridaThing, Type extends IridaThing> extends SesameRepository implements CRUDRepository<IDType, Type> {
    
    //TripleStore store;
    //String URI; //The base URI for objects of this type 
    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GenericRepository.class);
    
    Class<Type> objectType; //The class object type being stored by this repo
    private String prefix; //String representation of that type
    private String sType;
    
    protected AuditRepository auditRepo;
    protected RelationshipSesameRepository linksRepo;

    public GenericRepository() {
    }

    public GenericRepository(TripleStore store, Class type,String prefix, String sType,AuditRepository auditRepo, RelationshipSesameRepository linksRepo) {
        super(store, sType);

        this.prefix = prefix;
        this.sType = sType;
        this.auditRepo = auditRepo;
        this.linksRepo = linksRepo;
                
        this.objectType = type;
    }
    
    public void setLinksRepository(RelationshipSesameRepository linksRepo){
        this.linksRepo = linksRepo;
    }

    /**
     * Generate an identifier for an object of type
     * <code>Type</code>.
     *
     * @param t the object to generate the identifier for.
     * @return and identifier for the object.
     */
    public Identifier generateNewIdentifier(Type t) {
        return super.generateNewIdentifier();
    }

    /**
     * Build an identifier from the given binding set
     *
     * @param bs The binding set to build from
     * @param subject The subject of the SPARQL query to build from
     * @return An Identifier object built form the given binding set
     */
    public Identifier buildIdentifier(TypeIF obj, String identifiedBy) {
        Identifier objid = new Identifier();
        objid.setUri(java.net.URI.create(obj.toString()));
        objid.setUUID(UUID.fromString(identifiedBy));
        objid.setLabel(obj.getLabel());

        return objid;
    }
     
    
      
    /**
     * Build a basic object from the given interface type
     * @param base The base object to construct from
     * @param i The identifier to add to that object
     * @return A reconstructed object of <code>Type</code>
     */
    public abstract Type buildObject(TypeIF base,IDType i);
    
    @Override
    public Type create(Type object) throws IllegalArgumentException, EntityExistsException {
        if (object == null) {
            throw new IllegalArgumentException("Object is null");
        }
        
        Audit audit = (Audit) object.getAuditInformation();
                 
        Identifier objid = generateNewIdentifier(object);
        
        if (exists(objid)) {
            throw new EntityExistsException("Object " + objid.getUri().toString() + " already exists in the database");
        }
        
        if(identifierExists(objid.getIdentifier())){
            throw new EntityExistsException("An object with this identifier already exists in the database");            
        }

        object.setIdentifier((IDType) objid);

        storeObject(object);
        auditRepo.audit(audit, objid.getUri().toString());
        
        return object;
    }
    
    public Type storeObject(Type object){
        ObjectConnection con = store.getRepoConnection();
        
        Identifier objid = (Identifier) object.getIdentifier();
        
        try{
            con.begin();
            ValueFactory fac = con.getValueFactory();

            URI uri = fac.createURI(objid.getUri().toString());
            con.addObject(uri, object);
            
            setIdentifiedBy(con,uri, objid.getIdentifier());
                        
            con.commit();            
        }            
        catch (RepositoryException ex) {
            Logger.getLogger(GenericRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            try {
                con.close();
            } catch (RepositoryException ex) {
                logger.error(ex.getMessage());
                throw new StorageException("Failed to close connection");
            }            
        }
        
        return object;        
    }
    

    
    
    /**
     * Build an object of <code>Type</code> from the interface of that type.
     * Will also add the identifier and audit information to the object.
     * Calls the abstract method <code>buildObject(TypeIF o, IDType objid)</code> of the implementing database to instantiate the object
     * 
     * @param o The Alibaba interface to construct the object from
     * @param u The URI of the object to construct
     * @param con An active objectconnection
     * @return A reconstructed object of <code>Type</code>
     * @throws MalformedQueryException
     * @throws RepositoryException
     * @throws QueryEvaluationException
     */
    public Type buildObjectFromResult(TypeIF o,URI u,ObjectConnection con) throws MalformedQueryException, RepositoryException, QueryEvaluationException{
        String identifiedBy = getIdentifiedBy(con,u);
        Identifier objid = buildIdentifier(o,identifiedBy);
        Type ret = buildObject(o,(IDType)objid);
        ret.setAuditInformation(auditRepo.getAudit(u));                
        
        return ret;
    }

    @Override
    public Type read(Identifier id) throws EntityNotFoundException {
        Type ret = null;
        
        java.net.URI netURI = buildURI(id.getIdentifier());
        String uri = netURI.toString();
        
        if (!exists(id)) {
            throw new EntityNotFoundException("No such object with the given URI exists.");
        }
        
        ObjectConnection con = store.getRepoConnection();
        
        try {
            String qs = store.getPrefixes()
                    + "SELECT ?s "
                    + "WHERE{ ?s a ?type . \n"
                    + "}";

            //ObjectQuery tupleQuery = con.prepareObjectQuery(QueryLanguage.SPARQL, qs);
            ValueFactory fac = con.getValueFactory();
            URI u = fac.createURI(uri);
            //tupleQuery.setBinding("s", u);
            //tupleQuery.setType("type", objectType);
            
            //Result<TypeIF> result = (Result<TypeIF>) tupleQuery.evaluate(objectType);
            
            //TypeIF o = result.next();
            TypeIF o = (TypeIF) con.getObject(objectType, u);
            
            ret = buildObjectFromResult(o, u, con);

        } catch (RepositoryException | QueryEvaluationException | MalformedQueryException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to read resource");
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
    
    public List<Type> readMultiple(List<Identifier> idents){
        List<Type> projects = new ArrayList<>();
        ObjectConnection con = store.getRepoConnection();
        
        try{
            //compile a string list of the string URIs
            List<String> uris = new ArrayList<>();
            for(Identifier i :idents){
                java.net.URI uri = getUriFromIdentifier(i);
                uris.add(uri.toString());
            }
            String[] strArr = new String[uris.size()];
            uris.toArray(strArr);

            Result<TypeIF> result = (Result<TypeIF>) con.getObjects(objectType, strArr);
            while(result.hasNext()){
                TypeIF o = result.next();
            
                URI u = con.getValueFactory().createURI(o.toString());
                
                Type ret = buildObjectFromResult(o, u, con);
                
                projects.add(ret);               
            }
            result.close();            
        }
        catch (RepositoryException | QueryEvaluationException | MalformedQueryException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to read multiple objects"); 
        }        
        finally{
            store.closeRepoConnection(con);
        }
        
        return projects;        
    }

    @Override
    public Type update(Type object) throws IllegalArgumentException {
        
        Identifier id = (Identifier) object.getIdentifier();
        
        delete(id);
        Audit a = (Audit) object.getAuditInformation();
        a.setUpdated(new Date());
        object.setAuditInformation(a);
        String u = id.getUri().toString();
        auditRepo.audit(a, u);
        object = storeObject(object);

        return object;        
    }

    @Override
    public void delete(Identifier id) throws EntityNotFoundException {
        if (!exists(id)) {
            throw new EntityNotFoundException("Object does not exist in the database.");            
        }
        
        ObjectConnection con = store.getRepoConnection();

        java.net.URI netURI = buildURI(id.getIdentifier());
        String uri = netURI.toString();

        ValueFactory vf = con.getValueFactory();
        URI objecturi = vf.createURI(uri);

        try {
            con.remove(objecturi, null, null);
            con.close();

        } catch (RepositoryException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to remove object" + id);
        } finally {
            try {
                con.close();
            } catch (RepositoryException ex) {
                logger.error(ex.getMessage());
                throw new StorageException("Failed to close connection");
            }
        }
              
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Type> list() {
        return list(0, 0, null, null);
    }

    @Override
    public List<Type> list(int page, int size, String sortProperty, Order order) {
        List<Type> users = new ArrayList<>();
        ObjectConnection con = store.getRepoConnection();
        try{
            String qs = store.getPrefixes()
                    + "SELECT ?s "
                    + "WHERE{ ?s a ?type . \n"
                    + "?a irida:forResource ?s \n."
                    + "?a irida:createdDate ?createdDate .\n"
                    + "}";
            
            qs += SparqlQuery.setOrderBy(sortProperty, order);
            qs += SparqlQuery.setLimitOffset(page, size);
            
            
            ObjectQuery query = con.prepareObjectQuery(QueryLanguage.SPARQL, qs);
            query.setType("type", objectType);
            
            Result<TypeIF> result = (Result<TypeIF>) query.evaluate(objectType);
            //Set<TypeIF> resSet = result.asSet();
            //for(TypeIF o : resSet){
            while(result.hasNext()){
                TypeIF o = result.next();
            
                URI u = con.getValueFactory().createURI(o.toString());
                        
                Type ret = buildObjectFromResult(o, u, con);
                users.add(ret);                
            }
            
            
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to list objects"); 
        }
        finally{
            try {
                con.close();
            } catch (RepositoryException ex) {
                logger.error(ex.getMessage());
                throw new StorageException("Failed to close connection");
            }            
        }
        return users;       
    }

    @Override
    public Boolean exists(Identifier id) {
        
        boolean exists = false;
        ObjectConnection con = store.getRepoConnection();

        try {
            java.net.URI netURI = buildURI(id.getIdentifier());
            String uri = netURI.toString();
            
            String querystring = store.getPrefixes()
                    + "ASK\n"
                    + "{?uri a ?type}";
            
            BooleanQuery existsQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL, querystring);

            ValueFactory vf = con.getValueFactory();
            URI objecturi = vf.createURI(uri);
            existsQuery.setBinding("uri", objecturi);

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
    
    @Override
    public Integer count() {
        int count = 0;
        
        ObjectConnection con = store.getRepoConnection();
        
        try {
            String qs = store.getPrefixes()
                    + "SELECT (count(?s) as ?c) \n"
                    + "WHERE{ ?s a ?type . \n"
                    + "}\n";
            
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            
            URI vtype = con.getValueFactory().createURI(prefix, sType);
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

    
}
