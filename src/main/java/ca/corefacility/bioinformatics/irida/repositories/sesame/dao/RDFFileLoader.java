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

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class RDFFileLoader {
    TripleStore store;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RDFFileLoader.class);
    
    List<Resource> resources;
    public RDFFileLoader(){}
    
    public RDFFileLoader(TripleStore store){
        this.store = store;
    }

    public RDFFileLoader(TripleStore store, List<Resource> resources) {
        this.store = store;
        this.resources = resources;
    }
    
    public void addDataWithoutClear(){
        ObjectConnection con = store.getRepoConnection();
        URI context = con.getValueFactory().createURI(store.URI);
        
        try {
            con.begin();
            for(Resource res : resources){
                InputStream str = res.getInputStream();

                con.add(str, store.URI, RDFFormat.RDFXML, context);
            }
            con.commit();
        } catch (RepositoryException | RDFParseException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't add RDF resource to repository: " + ex.getMessage()); 
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't read RDF file: " + ex.getMessage());
        }
        finally{
            store.closeRepoConnection(con);
        }        
    }
    
    public void addResourceList(){
        ObjectConnection con = store.getRepoConnection();
        URI context = con.getValueFactory().createURI(store.URI);
        
        try {
            con.begin();
            con.clear(context);
            for(Resource res : resources){
                InputStream str = res.getInputStream();

                con.add(str, store.URI, RDFFormat.RDFXML, context);
            }
            con.commit();
        } catch (RepositoryException | RDFParseException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't add RDF resource to repository: " + ex.getMessage()); 
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't read RDF file: "+ ex.getMessage());
        }
        finally{
            store.closeRepoConnection(con);
        }
        
    }    
}
