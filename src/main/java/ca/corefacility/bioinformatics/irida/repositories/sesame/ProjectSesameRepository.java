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

import ca.corefacility.bioinformatics.irida.dao.SparqlQuery;
import ca.corefacility.bioinformatics.irida.dao.TripleStore;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
public class ProjectSesameRepository extends SesameRepository implements ProjectRepository{
    
    public final static HashMap<String,String> projectParams = new HashMap<>();
    
    static{
        projectParams.put("rdfs:label", "?name");
    }
    
    public ProjectSesameRepository(){}
    
    public ProjectSesameRepository(TripleStore store){
        super(store,"Project");
    } 
    
    @Override
    public Project create(Project object) throws IllegalArgumentException {        
        if(object == null){
            throw new IllegalArgumentException("Project is null");
        }
        
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

        if(id.getUri() == null){
            throw new IllegalArgumentException("URI for object is null");
        }
        
        if(exists(id)){
            String uri = id.getUri().toString();

            RepositoryConnection con = store.getRepoConnection();
            try {
                String qs = store.getPrefixes()
                        + "SELECT * "
                        + "WHERE{ ?s a irida:Project . \n"
                        + ProjectSesameRepository.getParameters("s",projectParams)
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
                
                con.close();

            } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
                System.out.println(ex.getMessage());
            }
        }
        else{
            throw new IllegalArgumentException("No such object with the given URI exists.");
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
                con.close();
                
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
        return list(0,0,null,null);        
    }

    @Override
    public List<Project> list(int page, int size, String sortProperty, Order order) {
        List<Project> projects = new ArrayList<>();
        
        RepositoryConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT * "
                    + "WHERE{ ?s a irida:Project . \n"
                    + ProjectSesameRepository.getParameters("s",projectParams)
                    + "}\n";

            qs += SparqlQuery.setOrderBy(sortProperty, order);
            qs += SparqlQuery.setLimitOffset(page, size);
            
            
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);

            TupleQueryResult result = tupleQuery.evaluate();
            while(result.hasNext()){
                BindingSet bindingSet = result.next();
                Value s = bindingSet.getValue("s");
                
                Project ret = new Project();

                Identifier objid = new Identifier(java.net.URI.create(s.stringValue()));

                ret.setIdentifier(objid);                
                ProjectSesameRepository.buildProjectProperties(bindingSet, ret);
                
                projects.add(ret);
            }
            result.close();
            con.close();
    
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            System.out.println(ex.getMessage());
        }         
        
        return projects;           
    }    

    @Override
    public Boolean exists(Identifier id) {       
        return super.exists(id, "irida", "Project");
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
        proj.setName(bs.getValue("name").stringValue());

    }   

    @Override
    public Collection<User> getUsersForProject(Project project) {
        List<User> users = new ArrayList<>();
        
        String uri = project.getIdentifier().getUri().toString();
        
        RepositoryConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT * "
                    + "WHERE{ ?p a irida:Project . \n"
                    + "?p irida:hasUser ?s . \n"
                    + UserSesameRepository.getParameters("s",UserSesameRepository.userParams)
                    + "}\n";
            
            
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            URI puri = con.getValueFactory().createURI(uri);
            tupleQuery.setBinding("p", puri);

            TupleQueryResult result = tupleQuery.evaluate();
            while(result.hasNext()){
                BindingSet bindingSet = result.next();
                Value s = bindingSet.getValue("s");
                
                User ret = new User();

                Identifier objid = new Identifier(java.net.URI.create(s.stringValue()));

                ret.setIdentifier(objid);                
                UserSesameRepository.buildUserProperties(bindingSet, ret);
                
                users.add(ret);
            }
            result.close();
            con.close();
    
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            System.out.println(ex.getMessage());
        }         
        
        return users;        
    }

    @Override
    public Integer count() {
        return super.count("irida","Project");
    }
}
