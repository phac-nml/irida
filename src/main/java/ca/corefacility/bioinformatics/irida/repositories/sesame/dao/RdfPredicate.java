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
package ca.corefacility.bioinformatics.irida.repositories.sesame.dao;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class RdfPredicate {

    public static final RdfPredicate ANY = new RdfPredicate("any", "any");
    private String prefix;
    private String name;

    public RdfPredicate() {
    }

    public RdfPredicate(String prefix, String name) {
        this.prefix = prefix;
        this.name = name;
    }

    /**
     * Get the notation of this rdfPredicate for use in a SPARQL query
     *
     * @return A String of this RDF predicate
     */
    public String getSparqlNotation() {
        return prefix + ":" + name;
    }

    /**
     * Get the prefix of this RDF predicate
     *
     * @return The string prefix of this predicate
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Get the local name of this rdf predicate
     *
     * @return The string local name of this predicate
     */
    public String getName() {
        return name;
    }

    /**
     * Build a Sesame URI for this RDFPredicate
     *
     * @param con An object connection to construct this predicate for
     * @return A URI of the predicate
     * @throws RepositoryException
     */
    public URI getPredicateURI(RepositoryConnection con) throws RepositoryException {
        ValueFactory vf = con.getValueFactory();
        URI pred = vf.createURI(con.getNamespace(this.prefix), this.name);
        return pred;
    }
}
