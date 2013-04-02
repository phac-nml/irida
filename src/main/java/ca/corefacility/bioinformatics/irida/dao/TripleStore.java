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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.Namespace;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.config.RepositoryFactory;
import org.openrdf.repository.http.HTTPRepository;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class TripleStore {
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
    protected Repository repo;
    private RepositoryFactory orf;
    public String URI = "http://bobloblaw/IRIDA/";
    String serverUrl;
    String repoName;

    public TripleStore(){
    }
    
    public TripleStore(String serverUrl, String repoName){
        this.setServer(serverUrl, repoName);
    }
    
    public String getURI(){
        return URI;
    }    

    public void setServer(String serverUrl, String repoName) {
        this.serverUrl = serverUrl;
        this.repoName = repoName;
        
        repo = new HTTPRepository(serverUrl,repoName);
        try {
            repo.initialize();
            RepositoryConnection con = repo.getConnection();

        } catch (RepositoryException ex) {
            Logger.getLogger(TripleStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public RepositoryConnection getRepoConnection() {
        RepositoryConnection con = null;
        
        try {
            con = repo.getConnection();
        } catch (RepositoryException ex) {
            Logger.getLogger(TripleStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return con;
    }

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
            Logger.getLogger(TripleStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return prefixes;
    }

    public void close() {
        try {
            repo.shutDown();
        } catch (RepositoryException ex) {
            Logger.getLogger(TripleStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
    
