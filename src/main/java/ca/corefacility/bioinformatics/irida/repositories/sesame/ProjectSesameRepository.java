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

import ca.corefacility.bioinformatics.irida.dao.PropertyMapper;
import ca.corefacility.bioinformatics.irida.dao.TripleStore;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class ProjectSesameRepository extends GenericRepository<Identifier, Project> implements ProjectRepository{
    
private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProjectSesameRepository.class);

    public ProjectSesameRepository(){}
    
    public ProjectSesameRepository(TripleStore store) {
        super(store,Project.class);

        PropertyMapper map = new PropertyMapper("irida", "Project");
        
        try{
            map.addProperty("rdfs","label","projectName", Project.class.getMethod("getName"), Project.class.getMethod("setName",String.class), String.class);
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(GenericRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setPropertyMap(map);
    }    

    /**
     * {@inheritDoc}
     */    
    @Override
    public Collection<Project> getProjectsForUser(User user) {
        List<Project> projects = new ArrayList<>();
        
        String uri = user.getIdentifier().getUri().toString();
        
        RepositoryConnection con = store.getRepoConnection();
        try {
            String qs = store.getPrefixes()
                    + "SELECT * "
                    + "WHERE{ ?u a foaf:Person . \n"
                    + "?s irida:hasUser ?u . \n"
                    + buildParams("s",propertyMap)
                    + "}\n";
            
            
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, qs);
            URI puri = con.getValueFactory().createURI(uri);
            tupleQuery.setBinding("u", puri);

            TupleQueryResult result = tupleQuery.evaluate();
            while(result.hasNext()){
                BindingSet bindingSet = result.next();
                Identifier objid = buildIdentifier(bindingSet,"s");
                
                Project ret = extractData(objid, bindingSet);
                
                projects.add(ret);
            }
            result.close();
    
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException ex) {
            logger.error(ex.getMessage());
            throw new StorageException("Couldn't list projects for user "+user.getUsername()); 
        }       
        finally{
            try {
                con.close();
            } catch (RepositoryException ex) {
                logger.error(ex.getMessage());
                throw new StorageException("Couldn't close connection"); 
            }
        }
        
        return projects;
    }    
    

}
