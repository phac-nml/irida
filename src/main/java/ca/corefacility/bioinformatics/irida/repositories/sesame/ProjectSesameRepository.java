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

import ca.corefacility.bioinformatics.irida.dao.TripleStore;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class ProjectSesameRepository implements CRUDRepository<Identifier, Project>{
    TripleStore store;
    String URI;
    
    public ProjectSesameRepository(){}
    
    public ProjectSesameRepository(TripleStore store){
        this.store = store;
        URI = store.getURI() + "Project/";
    } 
    
    @Override
    public Project create(Project object) throws IllegalArgumentException {        
        RepositoryConnection con = store.getRepoConnection();

        String id;
        Identifier objid;
        do{
            id = URI + UUID.randomUUID().toString();
            java.net.URI objuri = java.net.URI.create(id);

            objid = new Identifier(objuri);
        }while(exists(objid)); //I know it's a UUID.  Just being safe
        
        object.setIdentifier(objid); 
        
        try {
            con.begin();

            ValueFactory fac = con.getValueFactory();
            URI uri = fac.createURI(id);
            
            //add type
            URI pred = fac.createURI(con.getNamespace("rdf"), "type");
            Value name = fac.createURI(con.getNamespace("irida"),"Project");
            Statement st = fac.createStatement(uri, pred, name);
            con.add(st);            
            
            addProjectProperties(object,uri,con);

            con.commit();
            
            con.close();

        } catch (RepositoryException ex) {
            System.out.println(ex.getMessage());
        }
        
        return object;     
    }

    @Override
    public Project read(Identifier id) throws IllegalArgumentException {        
        Project ret = null;

        String uri = id.getUri().toString();
        
        RepositoryConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT * "
                    + "WHERE{ ?s a irida:Project . \n"
                    + ProjectSesameRepository.getProjectParameters("s")
                    + "}";
            
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            ValueFactory fac = con.getValueFactory();
            URI u = fac.createURI(uri);
            tupleQuery.setBinding("s", u);
            
            TupleQueryResult result = tupleQuery.evaluate();
            BindingSet bindingSet = result.singleResult();

            Value s = bindingSet.getValue("s");

            ret = new Project();
            
            Identifier objid = new Identifier(java.net.URI.create(s.stringValue()));
            
            ret.setIdentifier(objid);
            
            ProjectSesameRepository.buildProjectProperties(bindingSet, ret);
                
    
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            System.out.println(ex.getMessage());
        }                 
        
        return ret;
    }

    @Override
    public Project update(Project object) throws IllegalArgumentException {
        delete(object.getIdentifier());
        object = create(object);
        
        return object;
    }

    @Override
    public void delete(Identifier id) throws IllegalArgumentException {        
        if(exists(id)){
            RepositoryConnection con = store.getRepoConnection();
            
            String uri = id.getUri().toString();

            ValueFactory vf = con.getValueFactory();
            URI objecturi = vf.createURI(uri);
            
            try {
                con.remove(objecturi, null, null);
                
            } catch (RepositoryException ex) {
                Logger.getLogger(UserSesameRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else{
            throw new IllegalArgumentException("User does not exist in the database.");
        }        
    }

    @Override
    public List<Project> list() {        
        List<Project> users = new ArrayList<>();
        
        RepositoryConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT * "
                    + "WHERE{ ?s a irida:Project . \n"
                    + ProjectSesameRepository.getProjectParameters("s")
                    + "}\n"
                    + "ORDER BY ?nick";
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);

            TupleQueryResult result = tupleQuery.evaluate();
            while(result.hasNext()){
                BindingSet bindingSet = result.next();
                Value s = bindingSet.getValue("s");
                
                Project ret = new Project();

                Identifier objid = new Identifier(java.net.URI.create(s.stringValue()));

                ret.setIdentifier(objid);                
                ProjectSesameRepository.buildProjectProperties(bindingSet, ret);
                
                users.add(ret);
            }
            result.close();
    
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            System.out.println(ex.getMessage());
        }         
        
        return users;        
    }

    @Override
    public Boolean exists(Identifier id) {        
        boolean exists = false;

        try {
            String uri = id.getUri().toString();
            
            RepositoryConnection con = store.getRepoConnection();
            
            String querystring = store.getPrefixes()
                    + "ASK\n"
                    + "{?uri a ?type}";
            
            BooleanQuery existsQuery = con.prepareBooleanQuery(QueryLanguage.SPARQL, querystring);

            ValueFactory vf = con.getValueFactory();
            URI objecturi = vf.createURI(uri);
            existsQuery.setBinding("uri", objecturi);

            URI typeuri = vf.createURI(con.getNamespace("irida"),"Project");
            existsQuery.setBinding("type", typeuri);

            exists = existsQuery.evaluate();
            
            return exists;
        } catch (RepositoryException |MalformedQueryException | QueryEvaluationException ex) {
            Logger.getLogger(UserSesameRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return exists;        
    }

    private void addProjectProperties(Project proj, URI uri, RepositoryConnection con) throws RepositoryException{
        ValueFactory fac = con.getValueFactory(); 

        //add project name rdfs:label
        URI pred = fac.createURI(con.getNamespace("rdfs"),"label");
        Value name = fac.createLiteral(proj.getName());
        Statement st = fac.createStatement(uri, pred, name);
        con.add(st);
    }

    
    public static void buildProjectProperties(BindingSet bs, Project proj){      
        proj.setName(bs.getValue("label").stringValue());

    }
    
    
    public static String getProjectParameters(String subject){
        subject = "?" + subject;
        
        String params = subject + " rdfs:label ?label .\n";
        
        return params;
    }    
    
    public void close() {
        store.close();
    }    
}
