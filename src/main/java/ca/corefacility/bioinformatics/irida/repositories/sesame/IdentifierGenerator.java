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
import ca.corefacility.bioinformatics.irida.model.alibaba.IridaThing;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.roles.impl.StringIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.TripleStore;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.UUID;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
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
public class IdentifierGenerator<Type extends IridaThing> {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(IdentifierGenerator.class); //Logger to use for this repository
    TripleStore store;

    public IdentifierGenerator(TripleStore store) {
        this.store = store;
    }
    
    /**
     * Generate a new identifier for the given object.  
     * This implementation is independent of the object but it may be overridden for specific class implementations
     *
     * @param t The object to generate an identifier for
     * @return A newly generated identifier for the given object
     */
    public Identifier generateNewIdentifier(Type obj, String baseURI) {
        UUID uuid = UUID.randomUUID();
        java.net.URI objuri = buildURIFromIdentifiedBy(uuid.toString(), baseURI);
        Identifier id = new Identifier(objuri, uuid);
        if(obj != null){
            id.setLabel(obj.getLabel());
        }
        return id;
    }
    
    /**
     * Build a URI from a given String ID
     *
     * @param id The ID to build a URI for
     * @return The constructed URI
     */
    protected java.net.URI buildURIFromIdentifiedBy(String id, String baseURI) {
        java.net.URI uri = java.net.URI.create(baseURI + id);

        return uri;
    }    
    
    /**
     * Queries the triplestore for an identifier object for the given URI
     *
     * @param uri The URI to retrieve and build an identifier for
     * @return A new Identifier instance
     */
    public Identifier getIdentiferForURI(URI uri) {
        Identifier id = null;
        ObjectConnection con = store.getRepoConnection();
        logger.trace("Going to get identifier for URI: [" + uri + "]");
        try {
            String qs = store.getPrefixes()
                    + "SELECT ?object ?identifier ?label "
                    + "WHERE{ ?object irida:identifier ?identifier ;"
                    + " rdfs:label ?label .\n"
                    + "}";
            TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);

            query.setBinding("object", uri);
            TupleQueryResult results = query.evaluate();
            BindingSet bs = results.next();
            id = buildIdentiferFromBindingSet(bs, "object");
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            logger.error("A RepositoryException | MalformedQueryException | " +
                    "QueryEvaluationException occurred at [" + new Date() + "]", ex);
            throw new StorageException("Failed to get identifier for URI: [" + uri + "]");
        } finally {
            store.closeRepoConnection(con);
        }

        return id;
    }
    
    /**
     * Build an identifier object from a link binding set
     *
     * @param bs          The <type>BindingSet</type> to construct the identifier from
     * @param bindingName The binding name of the subject from this binding set
     * @return A <type>StringIdentifier</type> for this binding set
     */
    private StringIdentifier buildIdentiferFromBindingSet(BindingSet bs, String bindingName) {
        StringIdentifier id = null;
        try {
            Value uri = bs.getValue(bindingName);
            Value ident = bs.getValue("identifier");
            Value label = bs.getValue("label");
            id = new StringIdentifier();
            id.setIdentifier(ident.stringValue());
            id.setUri(new java.net.URI(uri.stringValue()));
            id.setLabel(label.stringValue());
        } catch (URISyntaxException ex) {
            logger.error("A URISyntaxException occurred at [" + new Date() + "]", ex);
            throw new StorageException(
                    "Failed to build identifier from binding set: [" + bs + "], bindingName: [" + bindingName + "]");
        }

        return id;
    }
    
    /**
     * Build an {@link Identifier} for the given object
     *
     * @param object       The object to build an identifier for
     * @param uri          The URI of the object to build an identifier for
     * @param identifiedBy The string identifier for this object
     * @return An {@link Identifier} for the given object
     */
    public Identifier buildIdentifier(Type object, URI uri, String identifiedBy) {
        Identifier objid = new Identifier();
        objid.setUri(java.net.URI.create(uri.toString()));
        objid.setUUID(UUID.fromString(identifiedBy));
        objid.setLabel(object.getLabel());

        return objid;
    }

    /**
     * Build a java.net.URI object for the given identifier
     *
     * @param identifier The identifier to build the URI for
     * @return The constructed URI for this identifier
     */
    public java.net.URI buildURIFromIdentifier(Identifier identifier,String baseURI) {
        java.net.URI uri;
        if (identifier.getUri() != null) {
            uri = identifier.getUri();
        } else {
            uri = buildURIFromIdentifiedBy(identifier.getIdentifier(),baseURI);
        }

        return uri;
    }    
}
