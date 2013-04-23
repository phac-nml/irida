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
package ca.corefacility.bioinformatics.irida.dao;

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class RDFFileLoader {
    TripleStore store;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RDFFileLoader.class);
    
    
    public RDFFileLoader(){}
    
    public RDFFileLoader(TripleStore store){
        this.store = store;
    }
    
    public void addRdfFile(File f){
        RepositoryConnection con = store.getRepoConnection();
        try {
            con.begin();
            
            con.add(f, null, RDFFormat.RDFXML);
            
            con.commit();
        } catch (RepositoryException | IOException | RDFParseException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't add file "+f+" to repository"); 
        }
        finally{
            try {
                con.close();
            } catch (RepositoryException ex) {
                logger.error(ex.getMessage());
                throw new StorageException("Couldn't close connection to repository"); 
            }
        }
        
    }
}
