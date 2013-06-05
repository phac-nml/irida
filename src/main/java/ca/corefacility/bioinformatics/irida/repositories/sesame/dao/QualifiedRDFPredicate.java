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
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class QualifiedRDFPredicate extends RdfPredicate{
    private String uriString;

    public QualifiedRDFPredicate(String uriString) {
        this.uriString = uriString;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getSparqlNotation() {
        return "<"+uriString+">";
    }

    @Override
    public String getPrefix() {
        throw new UnsupportedOperationException("Cannot return the prefix of a QualifiedRDFPredicate.  Use getPredicateURI instead");
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Cannot return the name of a QualifiedRDFPredicate.  Use getPredicateURI instead");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getPredicateURI(RepositoryConnection con) throws RepositoryException {
        ValueFactory vf = con.getValueFactory();
        URI uri = vf.createURI(uriString);
        return uri;
    }
    
    
}
