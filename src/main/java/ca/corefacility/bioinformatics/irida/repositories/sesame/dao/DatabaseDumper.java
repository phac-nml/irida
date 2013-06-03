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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

/**
 * A class to dump the RDF repository into a flat rdf/xml file.
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class DatabaseDumper {
    TripleStore store;
    
    /**
     * @param store The triplestore we want to dump
     */
    public DatabaseDumper(TripleStore store){
        this.store = store;
    }
    
    /**
     * Dump all the triples of a particular context into a file
     * @param file The file to dump to
     * @param context The context we want to dump
     * @throws FileNotFoundException
     */
    public void dump(String file,String context) throws FileNotFoundException{
        ObjectConnection con = store.getRepoConnection();
        
        try{
            FileOutputStream out = new FileOutputStream(file);
            RDFWriter writer = Rio.createWriter(RDFFormat.RDFXML, out);
            URI cont = new URIImpl(context);
            writer.startRDF();
            RepositoryResult<Statement> statements = con.getStatements(null, null, null, cont);
            while(statements.hasNext()){
                Statement next = statements.next();
                writer.handleStatement(next);
            }
            writer.endRDF();
        } catch (RDFHandlerException | RepositoryException ex) {
            Logger.getLogger(DatabaseDumper.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
           store.closeRepoConnection(con);
        } 
    }
    
    /**
     * Dump all data from the triplestore's data context into an RDF file
     * @param file The file to write to
     * @throws FileNotFoundException
     */
    public void dumpData(String file) throws FileNotFoundException{
        dump(file,store.URI);
    }
}
