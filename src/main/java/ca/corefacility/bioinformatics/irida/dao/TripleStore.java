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
import org.openrdf.model.Namespace;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.http.HTTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class TripleStore {
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
    private Repository repo;
    
    public String URI;
    
    String serverUrl;
    String repoName;
    
    private static final Logger logger = LoggerFactory.getLogger(TripleStore.class);

    public TripleStore(){}
    
    public TripleStore(String serverUrl, String repoName, String uri){
        this.serverUrl = serverUrl;
        this.repoName = repoName;
        this.URI = uri;
        
        repo = new HTTPRepository(serverUrl,repoName);
    }
    
    public TripleStore(Repository repo,String uri){
        this.URI = uri;
        this.repo = repo;        
    }
    
    /**
     * Return the base URI for this TripleStore
     * @return The base URI for this TripleStore instance
     */
    public String getURI(){
        return URI;
    }
    
    /**
     * Initialize the connection to the triplestore
     */
    @PostConstruct
    public void initialize(){
        try {
            repo.initialize();

        } catch (RepositoryException ex) {
            logger.error(ex.getMessage());
        }        
    }

    /**
     * Get the repository connection for this triplestore
     * @return The repository connection
     * @throws StorageException
     */
    public RepositoryConnection getRepoConnection() throws StorageException{
        RepositoryConnection con = null;
        
        try {
            con = repo.getConnection();
        } catch (RepositoryException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Could not retrieve repository connection");
        }
        
        return con;
    }

    /**
     * Get the prefixes defined by this repository for use in queries
     * @return A String of the prefixes used
     */
    public String getPrefixes() {
        String prefixes = "";
                
        try {
            RepositoryConnection con = repo.getConnection();
            RepositoryResult<Namespace> namespaces = con.getNamespaces();
            while(namespaces.hasNext()){
                Namespace ns = namespaces.next();
                String cur = "PREFIX "+ns.getPrefix()+": <"+ns.getName()+">\n";
                prefixes += cur;
            }
        } catch (RepositoryException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Could not retrieve namespace prefixes");
        }
                
        return prefixes;
    }

    /**
     * Close the connection to the triplestore
     */
    public void close() {
        try {
            repo.shutDown();
        } catch (RepositoryException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Could not shut down repository");
        }
    }
    
    
}
    
