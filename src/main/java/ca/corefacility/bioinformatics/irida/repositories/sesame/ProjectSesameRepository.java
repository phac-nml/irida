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

import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.TripleStore;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openrdf.model.URI;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.result.Result;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
//public class ProjectSesameRepository extends GenericAlibabaRepository<Identifier, ProjectIF> implements ProjectRepository{
public class ProjectSesameRepository extends GenericRepository<Identifier, Project> implements ProjectRepository{
    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProjectSesameRepository.class);
    private static final RdfPredicate hasProject = new RdfPredicate("irida", "hasProject");
    
    public ProjectSesameRepository(){}
    
    public ProjectSesameRepository(TripleStore store,AuditRepository auditRepo,RelationshipSesameRepository linksRepo) {
        super(store,Project.class,Project.PREFIX,Project.TYPE,auditRepo,linksRepo);
    }    
    
    //@Override
    public Project buildObject(Project base,Identifier i){
        Project p = new Project();
        
        p.setName(base.getName());
        p.setIdentifier(i);
        
        return p;
    }
    
    /**
     * {@inheritDoc}
     */    
    @Override
    public Collection<Project> getProjectsForUser(User user) {
        List<Identifier> projIds = linksRepo.listObjects(user.getIdentifier(), hasProject);
        
        List<Project> projs = readMultiple(projIds);
        
        return projs;
    }
    
    public Collection<Identifier> listProjectsForUser(User user){
        List<Identifier> projIds = linksRepo.listObjects(user.getIdentifier(), hasProject);
        return projIds;
    }
    
    public Relationship addUserToProject(Project p, User user){
        Relationship l = new Relationship();
        l.setSubject(user.getIdentifier());
        l.setPredicate(hasProject);
        l.setObject(p.getIdentifier());
        Relationship create = linksRepo.create(l);
        
        return create;
    }


}
