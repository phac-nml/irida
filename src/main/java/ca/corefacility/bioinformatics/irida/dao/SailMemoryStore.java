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
import javax.annotation.PostConstruct;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class SailMemoryStore extends TripleStore{
    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SailMemoryStore.class);

    public SailMemoryStore(){
        super(new SailRepository(new MemoryStore()),"http://nowhere/");

    }
    
    @PostConstruct
    @Override
    public void initialize(){
        super.initialize();
        RepositoryConnection con = super.getRepoConnection();
        try {
            con.setNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
            con.setNamespace("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
            con.setNamespace("irida", "http://corefacility.ca/irida/");
            con.setNamespace("foaf", "http://xmlns.com/foaf/0.1/");
        } catch (RepositoryException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Could not initialize connection");
        }        
    }
    
}
