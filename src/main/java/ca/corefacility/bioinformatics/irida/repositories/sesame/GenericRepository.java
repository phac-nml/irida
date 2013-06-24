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

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Audit;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.SparqlQuery;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.TripleStore;
import org.openrdf.annotations.Iri;
import org.openrdf.model.*;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.result.Result;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repository for storing objects of a type that extend {@link IridaThing}
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class GenericRepository<IDType extends Identifier, Type extends IridaThing> extends SesameRepository
        implements CRUDRepository<IDType, Type> {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(
            GenericRepository.class); //Logger to use for this repository
    protected AuditRepository auditRepo; //The auditing repository to use for auditing purposes in this repository
    protected RelationshipSesameRepository linksRepo; //The relationship repository to use for adding and querying relationships
    private Class objectType; //The class object type being stored by this repo
    private String prefix;  //The RDF prefix of the object type in this repository
    private String sType; //The RDF local name of the object type in this repository
    
    public GenericRepository() {
    }

    /**
     * @param store      A {@link TripleStore} to use for storing data in this repository
     * @param objectType The class of objects to store in this repository
     * @param prefix     The RDF prefix of the object type in this repository
     * @param sType      The RDF local name of the object type in this repository
     * @param auditRepo  The audit repository to use for this repository
     * @param linksRepo  The links repository to use for this repository
     */
    public GenericRepository(TripleStore store, Class objectType, String prefix, String sType, AuditRepository auditRepo, RelationshipSesameRepository linksRepo) {
        super(store, sType);

        this.prefix = prefix;
        this.sType = sType;
        this.auditRepo = auditRepo;
        this.linksRepo = linksRepo;

        this.objectType = objectType;        
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Type create(Type object) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException("Object is null");
        }

        Audit audit = (Audit) object.getAuditInformation();

        Identifier objid = idGen.generateNewIdentifier(object,URI);

        if (exists(objid)) {
            throw new EntityExistsException("Object " + objid.getUri().toString() + " already exists in the database");
        }

        if (identifierExists(objid.getIdentifier())) {
            throw new EntityExistsException("An object with this identifier already exists in the database");
        }

        object.setIdentifier(objid);

        storeObject(object);
        Map<String, Value> predicateValues = getPredicateValues(object);
        auditRepo.audit(audit, objid.getUri().toString(),predicateValues);

        return object;
    }


    /**
     * Store the given object in the RDF triplestore
     *
     * @param object The object to store in the triplestore
     * @return The object that was just stored
     */
    private Type storeObject(Type object) {
        ObjectConnection con = store.getRepoConnection();

        Identifier objid = (Identifier) object.getIdentifier();

        try {
            logger.trace("Adding new object to the database.");
            con.begin();
            ValueFactory fac = con.getValueFactory();

            URI uri = fac.createURI(objid.getUri().toString());
            con.addObject(uri, object);

            setIdentifiedBy(con, uri, objid.getIdentifier());

            con.commit();
        } catch (RepositoryException ex) {
            Logger.getLogger(GenericRepository.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            store.closeRepoConnection(con);
        }

        return object;
    }

    /**
     * Build a concrete Java object with an {@link Identifier} and {@link Audit} of <code>Type</code>. The passed object
     * may be an EntityProxied class that needs to be rebuilt
     *
     * @param object The object to reconstruct
     * @param uri    The URI to use to construct this object's {@link Identifier}
     * @param con    An object connection to use to construct this object
     * @return A reconstructed object of the repository's set type
     * @throws MalformedQueryException
     * @throws RepositoryException
     * @throws QueryEvaluationException
     */
    protected Type buildObjectFromResult(Type object, URI uri, ObjectConnection con)
            throws MalformedQueryException, RepositoryException, QueryEvaluationException {
        Type ret = (Type) object.copy();

        String identifiedBy = getIdentifiedBy(con, uri);
        Identifier objid = idGen.buildIdentifier(ret, uri, identifiedBy);
        ret.setIdentifier(objid);

        ret.setAuditInformation(auditRepo.getAudit(uri.toString()));

        return ret;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Type read(Identifier id) throws EntityNotFoundException {
        Type ret = null;

        java.net.URI netURI = idGen.buildURIFromIdentifier(id,URI);
        String uri = netURI.toString();

        logger.trace("Looking up uri: [" + uri + "]");

        if (!exists(id)) {
            throw new EntityNotFoundException("No such object with the given URI exists.");
        }

        ObjectConnection con = store.getRepoConnection();

        try {
            String qs = store.getPrefixes()
                    + "SELECT ?s "
                    + "WHERE{ ?s a ?type . \n"
                    + "}";

            ValueFactory fac = con.getValueFactory();
            URI u = fac.createURI(uri);

            Type o = (Type) con.getObject(objectType, u);


            ret = buildObjectFromResult(o, u, con);

        } catch (RepositoryException | QueryEvaluationException | MalformedQueryException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to read resource");
        } finally {
            store.closeRepoConnection(con);
        }

        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Type> readMultiple(Collection<Identifier> idents) {
        List<Type> projects = new ArrayList<>();
        ObjectConnection con = store.getRepoConnection();

        try {
            //compile a string list of the string URIs
            List<String> uris = new ArrayList<>();
            for (Identifier i : idents) {
                java.net.URI uri = getUriFromIdentifier(i);
                uris.add(uri.toString());
            }
            String[] strArr = new String[uris.size()];
            uris.toArray(strArr);

            Result<Type> result = (Result<Type>) con.getObjects(objectType, strArr);
            while (result.hasNext()) {
                Type o = result.next();

                URI u = con.getValueFactory().createURI(o.toString());

                Type ret = buildObjectFromResult(o, u, con);

                projects.add(ret);
            }
            result.close();
        } catch (RepositoryException | QueryEvaluationException | MalformedQueryException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to read multiple objects");
        } finally {
            store.closeRepoConnection(con);
        }

        return projects;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean exists(Identifier id) {
        boolean exists = false;
        ObjectConnection con = store.getRepoConnection();

        try {
            java.net.URI netURI = idGen.buildURIFromIdentifier(id,URI);
            String uri = netURI.toString();

            logger.trace("Checking for the existence of [" + uri + "]");

            String querystring = store.getPrefixes()
                    + "ASK\n"
                    + "{?uri a ?type}";

            BooleanQuery existsQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL, querystring);

            ValueFactory vf = con.getValueFactory();
            URI objecturi = vf.createURI(uri);
            existsQuery.setBinding("uri", objecturi);

            exists = existsQuery.evaluate();

            logger.trace("[" + uri + "] exists? " + exists);

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't run exists query");
        } finally {
            store.closeRepoConnection(con);
        }

        return exists;
    }

    /**
     * {@inheritDoc}
     */
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
        } finally {
            store.closeRepoConnection(con);
        }

        return count;
    }

    @Override
    public Type update(IDType id, Map<String, Object> updatedFields) throws InvalidPropertyException {

        java.net.URI netURI = idGen.buildURIFromIdentifier(id,URI);
        Audit audit = auditRepo.getAudit(netURI.toString());

        Map<String,Value> originals = new HashMap<>();
        if (exists(id)) {
            for (Entry<String, Object> field : updatedFields.entrySet()) {
                try {
                    Field declaredField = objectType.getDeclaredField(field.getKey());

                    Iri annotation = declaredField.getAnnotation(Iri.class);
                    if(annotation == null){
                        throw new InvalidPropertyException("The field " + field.getKey() + " does not have an @Iri annotation");
                    }

                    logger.trace("Updating " + field.getKey() + " -- " + annotation.value());

                    Value original = updateField(id, annotation.value(), field.getValue());
                    originals.put(annotation.value(), original);
                } catch (NoSuchFieldException ex) {
                    logger.error("No field " + field.getKey() + " exists.  Cannot update object.");
                    throw new InvalidPropertyException(
                            "No field named " + field.getKey() + " exists for this object type");
                }
            }
            audit.setUpdated(new Date());
            auditRepo.audit(audit, netURI.toString(),originals);
        }
        else{
            throw new StorageException("Trying to update an object that doesn't exist: " + id.toString());
        }
        
        Type obj = read(id);
        updateLabel(id,obj.getLabel());

        return obj;
    }
    
    private void updateLabel(IDType id, String label){
        ObjectConnection con = store.getRepoConnection();
        java.net.URI netURI = idGen.buildURIFromIdentifier(id,URI);

        String uri = netURI.toString();

        try {
            con.begin();

            ValueFactory fac = con.getValueFactory();
            URI subURI = fac.createURI(uri);
            URI predURI = fac.createURI(con.getNamespace("rdfs"),"label");
            Literal labelLiteral = fac.createLiteral(label);

            con.remove(subURI, predURI, null);

            Statement added = fac.createStatement(subURI, predURI, labelLiteral);
            con.add(added);

            con.commit();
        } catch (RepositoryException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to update label");
        } finally {
            store.closeRepoConnection(con);
        }        
    }    

    protected Literal createLiteral(ValueFactory fac, String predicate, Object obj) {
        Literal lit = null;// = fac.createLiteral(obj);
        try {
            Method method = ValueFactory.class.getMethod("createLiteral", obj.getClass());
            lit = (Literal) method.invoke(fac, obj);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            logger.error("Couldn't create literal for object type: "+obj.getClass().getName());
            throw new StorageException("Cannot create literal for object type: "+obj.getClass().getName());
        }
        return lit;
    }

    protected Value updateField(IDType id, String predicate, Object value) {
        ObjectConnection con = store.getRepoConnection();
        java.net.URI netURI = idGen.buildURIFromIdentifier(id,URI);
        String uri = netURI.toString();
        Value updatedValue = null;

        try {
            con.begin();

            ValueFactory fac = con.getValueFactory();
            URI subURI = fac.createURI(uri);
            URI predURI = fac.createURI(predicate);
            Literal objValue = createLiteral(fac, predicate, value);
            updatedValue = objValue;

            RepositoryResult<Statement> curvalues = con.getStatements(subURI, predURI, null);
            while (curvalues.hasNext()) {
                Statement next = curvalues.next();
                logger.trace("current value: " + next.getObject().stringValue());
            }
            con.remove(subURI, predURI, null);

            Statement added = fac.createStatement(subURI, predURI, objValue);
            con.add(added);

            con.commit();
        } catch (RepositoryException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to update field");
        } finally {
            store.closeRepoConnection(con);
        }
        
        return updatedValue;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(IDType id) throws EntityNotFoundException {
        if (!exists(id)) {
            throw new EntityNotFoundException("Object does not exist in the database.");
        }

        ObjectConnection con = store.getRepoConnection();

        java.net.URI netURI = idGen.buildURIFromIdentifier(id,URI);
        String uri = netURI.toString();

        ValueFactory vf = con.getValueFactory();
        URI objecturi = vf.createURI(uri);

        try {
            //Remove all things linking to this resource
            List<Relationship> links = linksRepo.getLinks(id, null, (Identifier) null);
            for(Relationship r : links){
                linksRepo.delete(r.getIdentifier());
            }
            
            con.remove(objecturi, null, null);
            
            con.close();

        } catch (RepositoryException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to remove object" + id);
        } finally {
            store.closeRepoConnection(con);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Type> list() {
        return list(0, 0, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Type> list(int page, int size, String sortProperty, Order order) {
        List<Type> users = new ArrayList<>();
        ObjectConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT ?s "
                    + "WHERE{ ?s a ?type . \n"
                    + "?a irida:auditForResource ?s \n."
                    + "?a irida:createdDate ?createdDate .\n"
                    + "}";

            qs += SparqlQuery.setOrderBy(sortProperty, order);
            qs += SparqlQuery.setLimitOffset(page, size);


            ObjectQuery query = con.prepareObjectQuery(QueryLanguage.SPARQL, qs);
            query.setType("type", objectType);

            Result<Type> result = (Result<Type>) query.evaluate(objectType);
            //Set<TypeIF> resSet = result.asSet();
            //for(TypeIF o : resSet){
            Set<Type> asSet = result.asSet();
            Iterator<Type> iterator = asSet.iterator();

            //while(result.hasNext()){
            while (iterator.hasNext()) {
                Type o = iterator.next();

                URI u = con.getValueFactory().createURI(o.toString());

                Type ret = buildObjectFromResult(o, u, con);
                users.add(ret);
            }


        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to list objects");
        } finally {
            store.closeRepoConnection(con);
        }
        return users;
    }
    
    /**
     * List objects of this type that have the given fields
     * @param fields The fields we want to select from the database
     * @return A Map<Identifier, Map<String,Object>> of object identifiers and key/value pairs of the selected fields
     */
    public Map<Identifier,Map<String,Object>> listFields(List<String> fields){
        Map<Identifier,Map<String,Object>> objResults = new HashMap<>();
        Map<String, String> fieldPredicates = getFieldPredicates(objectType);
        
        ObjectConnection con = store.getRepoConnection();
                
        int numPreds = fields.size();
        try{
            String qs = store.getPrefixes() + //get the prefixes
                    "SELECT * WHERE {" +
                    "?s a ?type .";
            
            //create a statement for the values we want to list
            for(int i=0;i<numPreds;i++){
                qs += "OPTIONAL{ ?s ?pred"+i + "?val"+i + " } .";
            }
            
            qs += "}"; //close the query
            
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            ValueFactory fac = con.getValueFactory();
            
            //set the type binding
            URI type = fac.createURI(prefix, sType);
            query.setBinding("type", type);
            
            //set the rest of the bindings for the object
            for(int i=0;i<numPreds;i++){
                setListBinding(fields.get(i), fieldPredicates, "pred"+i, query, fac);
            }
                     
            TupleQueryResult evaluate = query.evaluate();     
            while(evaluate.hasNext()){
                BindingSet bs = evaluate.next();
                Binding subjectBinding = bs.getBinding("s");
                String subString = subjectBinding.getValue().toString();
                
                Identifier identiferForURI = idGen.getIdentiferForURI(fac.createURI(subString));
                Map<String,String> values = new HashMap<>();
                Map<String,Object> objValues = new HashMap<>();
                for(int i=0;i<numPreds;i++){
                    String fieldName = fields.get(i);
                    String bindingName = "val"+i;
                    if(bs.hasBinding(bindingName))
                    {
                        Binding binding = bs.getBinding("val"+i);
                        String stringValue = binding.getValue().stringValue();

                        //We have to get the value in its original format.  For this we need to use some reflection
                        //first get the class of the field
                        Class pClass = null;
                        try{
                            pClass = getFieldType(objectType, fieldName);
                        }
                        catch(NoSuchFieldException ex){
                            throw new StorageException("Cannot read the field \"" + fieldName + "\" from class "+objectType.getCanonicalName());
                        }

                        Object value = null;

                        //if the field is a string, we can add it as-is
                        if(pClass.equals(String.class)){
                            value = stringValue;
                        }
                        else //if it's not, we need to run valueOf on the string value
                        {
                            value = convertFromString(stringValue, pClass);
                        }
                        values.put(fields.get(i), stringValue);
                        objValues.put(fields.get(i), value);
                    }
                }
                objResults.put(identiferForURI, objValues);
            }
            
            evaluate.close();
                        
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't list fields for type " + objectType.getCanonicalName());
        }finally{
            store.closeRepoConnection(con);
        }
        
        return objResults;
                
    }
    
    /**
     * Convert to the given class type from a string.
     * This method will just call valueOf for basic types.  It could be overridden for other types.
     * @param stringValue The string value to convert
     * @param pClass The class to convert to
     * @return A new instance of an object of type pClass
     */
    protected Object convertFromString(String stringValue, Class pClass){
        Object value = null;
        try {
            Method valueOf = pClass.getMethod("valueOf", String.class);
            value = valueOf.invoke(pClass, stringValue);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            logger.debug("Could not run valueOf(String) on class " + pClass.getCanonicalName() + " : " + ex.getMessage());
            throw new StorageException("Could not run valueOf(String) on class " + pClass.getCanonicalName());
        }        
        return value;
    }
    
    /**
     * Set a predicate binding for a {@link Query} based on a map of predicates
     * @param fieldName The name of the field to set a binding for
     * @param fieldPredicates A Map<String,String> of predicate URIs for the fields
     * @param bindingName The name of the binding to set the predicate for 
     * @param query The query to set the binding for
     * @param fac A ValueVactory to use to create the URI
     */
    protected void setListBinding(String fieldName, Map<String, String> fieldPredicates, String bindingName, Query query, ValueFactory fac){
        if(fieldPredicates.containsKey(fieldName)){
            String predStr = fieldPredicates.get(fieldName);
            URI pred = fac.createURI(predStr);
            query.setBinding(bindingName, pred);
        }
        else{
            throw new IllegalArgumentException("The object doesn't contain the field '" + fieldName + "'");
        }        
    }
    
    /**
     * Get the type of the field for the given class
     * @param c The class to check
     * @param field The field to check
     * @return The Class of the field in the given class
     * @throws NoSuchFieldException
     */
    protected Class getFieldType(Class c, String field) throws NoSuchFieldException{
        Field declaredField = c.getDeclaredField(field);
        
        return declaredField.getType();
    }
    
    private Map<String,String> getFieldPredicates(Class c){
        Map<String,String> predicates = new HashMap<>();
        if(c.getSuperclass() != null){
            Map<String,String> added = getFieldPredicates(c.getSuperclass());
            predicates.putAll(added);
        }
        
        if(c.getInterfaces() != null){
            for(Class inf : c.getInterfaces()){
                Map<String,String> added = getFieldPredicates(inf);
                predicates.putAll(added);
            }
        }
        
        for(Field f : c.getDeclaredFields()){
            Iri iri = f.getAnnotation(Iri.class);
            if(iri != null){
                predicates.put(f.getName(), iri.value());
            }
        }
        
        for(Method m : c.getDeclaredMethods()){
            Iri iri = m.getAnnotation(Iri.class);
            if(iri != null){
                predicates.put(m.getName(), iri.value()); 
            }
        }
        
        return predicates;
    }
    
    private Map<String,Value> getPredicateValues(Type obj){
        java.net.URI uriFromIdentifier = getUriFromIdentifier((Identifier) obj.getIdentifier());
        Map<String,Value> values = new HashMap<>();
        Map<String,String> preds = getFieldPredicates(obj.getClass());
        
        ObjectConnection con = store.getRepoConnection();
        ValueFactory vf = con.getValueFactory();
        
        try{
            URI sub = vf.createURI(uriFromIdentifier.toString());
            Set<String> keySet = preds.keySet();
            for(String fieldName : keySet){
                String predStr = preds.get(fieldName);
                URI pred = vf.createURI(predStr);
                RepositoryResult<Statement> statements = con.getStatements(sub, pred, null);
                if(statements.hasNext()){
                    Statement next = statements.next();
                    Value object = next.getObject();
                    values.put(predStr, object);
                }
                statements.close();
            }
        }
        catch (RepositoryException ex) {
            logger.error("Couldn't get predicate values for this object: "+ex.getMessage());
            throw new StorageException("Repository failed to get predicate values for this object");
        }        
        finally{
            store.closeRepoConnection(con);
        }
        
        return values;
    }
}
