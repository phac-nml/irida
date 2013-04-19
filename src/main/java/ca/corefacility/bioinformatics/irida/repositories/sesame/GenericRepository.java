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
import ca.corefacility.bioinformatics.irida.dao.SparqlQuery;
import ca.corefacility.bioinformatics.irida.dao.TripleStore;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.exceptions.user.UserNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.Identifiable;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.openrdf.model.Literal;
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
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class GenericRepository<Type extends Identifiable<Identifier>> extends SesameRepository implements CRUDRepository<Identifier, Type>  {

    public final static Map<String, String> userParams = new HashMap<>();
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserSesameRepository.class);
    Class<Type> objectType;
    PropertyMapper propertyMap;
    String stringType;

    public GenericRepository() {
    }
    
    public GenericRepository(TripleStore store, Class type) {
        super(store, type);
        this.objectType = type;
    }
    public GenericRepository(TripleStore store, Class type, PropertyMapper propertyMap) {
        super(store, type);
        this.objectType = type;
        
        setPropertyMap(propertyMap);
    }

    /**
     * Generate a UUID
     * 
     * @param t The object to build an ID for
     * @return A UUID for the object
     */
    public UUID generateId(Type t) {
        UUID id = UUID.randomUUID();
        
        return id;
    }
    
    /**
     * Build an identifier from the given binding set
     * 
     * @param bs The binding set to build from
     * @param subject The subject of the SPARQL query to build from
     * @return An Identifier object built form the given binding set
     */
    public Identifier buildIdentifier(BindingSet bs,String subject){
        Value s = bs.getValue(subject);
        Value resid = bs.getValue("resid");

        UUID uuid = UUID.fromString(resid.stringValue());
        Identifier objid = new Identifier(java.net.URI.create(s.stringValue()),uuid);
        
        return objid;
    }
    
    /**
     * Get the property mapper for the current repository
     * @return A property map
     */
    public PropertyMapper getPropertyMap(){
        return propertyMap;
    }
    
    /**
     * Build a URI from a given String ID
     * @param id The ID to build a URI for
     * @return The constructed URI
     */
    public java.net.URI buildURI(String id){
        java.net.URI uri = java.net.URI.create(URI + id);
        
        return uri;
    }    
    
    /**
     * Set the property mapper for this repository
     * @param propertyMap A property map that describes how to deconstruct and reconstruct the objects that will be stored in this repository
     */
    public void setPropertyMap(PropertyMapper propertyMap){
        this.propertyMap = propertyMap;
        stringType = propertyMap.prefix + ":" + propertyMap.type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type create(Type object) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException("User is null");
        }

        RepositoryConnection con = store.getRepoConnection();

        UUID id = generateId(object);
        java.net.URI objuri = buildURI(id.toString());
        Identifier objid = new Identifier(objuri,id);
        objid.setUri(objuri);
        
        String stringURI = objuri.toString();
        
        if(exists(objid)){
            throw new IllegalArgumentException("Object " + objid.getUri().toString() + " already exists in the database");
        }

        object.setIdentifier(objid);


        try {
            con.begin();

            ValueFactory fac = con.getValueFactory();

            URI uri = fac.createURI(stringURI);
            //add type
            URI oPred = fac.createURI(con.getNamespace("rdf"), "type");
            Value oName = fac.createURI(con.getNamespace(propertyMap.prefix), propertyMap.type);
            Statement oSt = fac.createStatement(uri, oPred, oName);
            con.add(oSt);
            
            oPred = fac.createURI(con.getNamespace("irida"), "identifier");
            Literal litId = fac.createLiteral(id.toString());
            oSt = fac.createStatement(uri, oPred, litId);
            con.add(oSt);

            List<PropertyMapper.Property> properties = propertyMap.getProperties();

            for (PropertyMapper.Property prop : properties) {
                URI pred = fac.createURI(con.getNamespace(prop.prefix), prop.predicate);
                Class type = prop.type;

                Literal l = null;
                try {
                    Method m = ValueFactory.class.getMethod("createLiteral", type);

                    l = (Literal) m.invoke(fac, type.cast(prop.getter.invoke(object)));

                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                    logger.error(ex.getMessage());
                    throw new StorageException("Couldn't invoke accessor methods to persist " + stringURI); 
                }

                Statement st = fac.createStatement(uri, pred, l);
                con.add(st);
            }

            con.commit();

        } catch (RepositoryException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to create resource");
        } finally {
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
    public Type read(Identifier id) throws UserNotFoundException {

        Type ret = null;

        java.net.URI netURI = buildURI(id.getUUID().toString());
        String uri = netURI.toString();

        if (!exists(id)) {
            throw new EntityNotFoundException("No such object with the given URI exists.");
        }

        RepositoryConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT * "
                    + "WHERE{ ?s a "+stringType+" . \n"
                    + buildParams("s",propertyMap)
                    + "}";

            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            ValueFactory fac = con.getValueFactory();
            URI u = fac.createURI(uri);
            tupleQuery.setBinding("s", u);

            TupleQueryResult result = tupleQuery.evaluate();
            BindingSet bindingSet = result.next();

            Identifier objid = buildIdentifier(bindingSet,"s");

            ret = extractData(objid, bindingSet);

        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
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

        RepositoryConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT * "
                    + "WHERE{ ?s a "+stringType+" . \n"
                    + buildParams("s",propertyMap)
                    + "}\n";

            qs += SparqlQuery.setOrderBy(sortProperty, order);
            qs += SparqlQuery.setLimitOffset(page, size);

            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);

            TupleQueryResult result = tupleQuery.evaluate();
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();

                Identifier objid = buildIdentifier(bindingSet,"s");
                Type ret = extractData(objid, bindingSet);

                users.add(ret);
            }
            result.close();
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Failed to list users");
        } finally {
            try {
                con.close();
            } catch (RepositoryException ex) {
                logger.error(ex.getMessage());
                throw new StorageException("Failed to close connection");
            }
        }

        return users;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Identifier id) throws IllegalArgumentException {
        if (exists(id)) {
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
            } finally {
                try {
                    con.close();
                } catch (RepositoryException ex) {
                    logger.error(ex.getMessage());
                    throw new StorageException("Failed to close connection");
                }
            }
        } else {
            throw new IllegalArgumentException("User does not exist in the database.");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean exists(Identifier id) {
        return super.exists(id, propertyMap.prefix, propertyMap.type);
    }

    /**
     * Build the SPARQL parameters for a given property type
     * @param subject The subject to use for this object
     * @param map The property map to construct from
     * @return A String of the parameters to construct an object
     */
    public String buildParams(String subject, PropertyMapper map) {
        List<PropertyMapper.Property> properties = map.getProperties();
        StringBuilder params = new StringBuilder("?").append(subject).append(" irida:identifier ?resid . \n");

        for (PropertyMapper.Property prop : properties) {
            params.append("?").append(subject).append(" "); //subject
            params.append(prop.prefix).append(":").append(prop.predicate).append(" "); //predicate
            params.append("?").append(prop.variable).append(" .\n"); //object
        }

        return params.toString();
    }

    /**
     * Build an object of <code>Type</code> based on the given binding set
     * @param id The identifier for the object to construct
     * @param bindingSet The binding set to construct from
     * @return A constructed object of the given type
     */
    public Type extractData(Identifier id, BindingSet bindingSet) {

        Type obj = null;
        
        try {
            obj = objectType.newInstance();
            obj.setIdentifier(id);
            obj = buildProperties(bindingSet, obj);
        } catch (InstantiationException | IllegalAccessException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't instantiate object "+id); 
        }

        return obj;
    }

    /**
     * Add the properties of an object of <code>Type</code> to the object based on the given binding set
     * @param bs The binding set to use for construction
     * @param obj The object to add the properties to
     * @return The object after adding all the properties
     */
    private Type buildProperties(BindingSet bs, Type obj) {
        List<PropertyMapper.Property> properties = propertyMap.getProperties();
        for (PropertyMapper.Property prop : properties) {

            try {
                String var = prop.variable;
                prop.setter.invoke(obj, bs.getValue(var).stringValue());
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                logger.error(ex.getMessage());
                throw new StorageException("Couldn't invoke methods to build object"); 
            }
        }

        return obj;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Type update(Type object) throws IllegalArgumentException {
        delete(object.getIdentifier());
        
        object = create(object);
        
        return object;    
    }
        
    /**
     * {@inheritDoc}
     */
    @Override
    public Integer count() {
        return super.count(propertyMap.prefix,propertyMap.type);
    }
}
