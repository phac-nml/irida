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
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public GenericRepository(TripleStore store, Class type, PropertyMapper propertyMap) {
        super(store, type);
        this.propertyMap = propertyMap;
        this.objectType = type;
        stringType = propertyMap.prefix + ":" + propertyMap.type;
    }

    public String generateId() {
        String id = UUID.randomUUID().toString();
        return id;
    }

    @Override
    public Type create(Type object) throws IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException("User is null");
        }

        RepositoryConnection con = store.getRepoConnection();

        String stringID = generateId();
        String stringURI = URI + stringID;
        java.net.URI objuri = java.net.URI.create(stringURI);

        Identifier objid = new Identifier(objuri, stringID);
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

            List<PropertyMapper.Property> properties = propertyMap.getProperties();

            for (PropertyMapper.Property prop : properties) {
                URI pred = fac.createURI(con.getNamespace(prop.prefix), prop.predicate);
                Class type = prop.type;

                Literal l = null;
                try {
                    Method m = ValueFactory.class.getMethod("createLiteral", type);

                    l = (Literal) m.invoke(fac, type.cast(prop.getter.invoke(object)));

                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
                    Logger.getLogger(GenericRepository.class.getName()).log(Level.SEVERE, null, ex);
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

    @Override
    public Type read(Identifier id) throws UserNotFoundException {

        Type ret = null;

        String uri = id.getUri().toString();

        if (!exists(id)) {
            throw new UserNotFoundException("No such user with the given URI exists.");
        }

        RepositoryConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT * "
                    + "WHERE{ ?s a "+stringType+" . \n"
                    + buildParams("s")
                    + "}";

            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            ValueFactory fac = con.getValueFactory();
            URI u = fac.createURI(uri);
            tupleQuery.setBinding("s", u);

            TupleQueryResult result = tupleQuery.evaluate();
            BindingSet bindingSet = result.next();

            Value s = bindingSet.getValue("s");

            Identifier objid = new Identifier(java.net.URI.create(s.stringValue()));

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

    @Override
    public List<Type> list() {
        return list(0, 0, null, null);
    }

    @Override
    public List<Type> list(int page, int size, String sortProperty, Order order) {
        List<Type> users = new ArrayList<>();

        RepositoryConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT * "
                    + "WHERE{ ?s a "+stringType+" . \n"
                    + buildParams("s")
                    + "}\n";

            qs += SparqlQuery.setOrderBy(sortProperty, order);
            qs += SparqlQuery.setLimitOffset(page, size);

            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);

            TupleQueryResult result = tupleQuery.evaluate();
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                Value s = bindingSet.getValue("s");

                Identifier objid = new Identifier(java.net.URI.create(s.stringValue()));
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

    @Override
    public Boolean exists(Identifier id) {
        return super.exists(id, propertyMap.prefix, propertyMap.type);
    }

    public String buildParams(String subject) {
        List<PropertyMapper.Property> properties = propertyMap.getProperties();
        StringBuilder params = new StringBuilder();

        for (PropertyMapper.Property prop : properties) {
            params.append("?").append(subject).append(" "); //subject
            params.append(prop.prefix).append(":").append(prop.predicate).append(" "); //predicate
            params.append("?").append(prop.variable).append(" .\n"); //object
        }

        return params.toString();
    }

    public Type extractData(Identifier id, BindingSet bindingSet) {

        Type usr = null;
        
        try {
            usr = objectType.newInstance();
            usr.setIdentifier(id);
            usr = buildUserProperties(bindingSet, usr);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(GenericRepository.class.getName()).log(Level.SEVERE, null, ex);
        }

        return usr;
    }

    private Type buildUserProperties(BindingSet bs, Type usr) {
        List<PropertyMapper.Property> properties = propertyMap.getProperties();
        for (PropertyMapper.Property prop : properties) {

            try {
                String var = prop.variable;
                prop.setter.invoke(usr, bs.getValue(var).stringValue());
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(GenericRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return usr;
    }

    @Override
    public Type update(Type object) throws IllegalArgumentException {
        delete(object.getIdentifier());
        
        object = create(object);
        
        return object;    
    }

    @Override
    public Integer count() {
        return super.count(propertyMap.prefix,propertyMap.type);
    }
}
