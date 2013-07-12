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

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.TripleStore;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
//public class ProjectSesameRepository extends GenericRepository<Identifier, Project> implements ProjectRepository {
public class ProjectSesameRepository extends GenericRepository<Identifier, Project>{

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProjectSesameRepository.class);
    private static final RdfPredicate hasProject = new RdfPredicate("irida", "hasProject");

    public ProjectSesameRepository() {
    }

    public ProjectSesameRepository(TripleStore store, AuditRepository auditRepo, RelationshipSesameRepository linksRepo) {
        super(store, Project.class, Project.PREFIX, Project.TYPE, auditRepo, linksRepo);
    }

    /**
     * {@inheritDoc}
     */
    //@Override
    public Collection<Project> getProjectsForUser(User user) {
        List<Identifier> projIds = linksRepo.listObjects(user.getIdentifier(), hasProject);

        Collection<Project> projs = readMultiple(projIds);

        return projs;
    }

    public Collection<Identifier> listProjectsForUser(User user) {
        List<Identifier> projIds = linksRepo.listObjects(user.getIdentifier(), hasProject);
        return projIds;
    }

    /**
     * {@inheritDoc}
     */
    //@Override
    public Relationship addUserToProject(Project p, User user) {
        Relationship l = new Relationship();
        l.setSubject(user.getIdentifier());
        //l.setPredicate(hasProject);
        l.setObject(p.getIdentifier());
        Relationship create = linksRepo.create(l);

        return create;
    }

    /**
     * {@inheritDoc}
     */
    //@Override
    public void removeUserFromProject(Project project, User user) {
        //throw new UnsupportedOperationException("not implemented.");
        
        List<Relationship> links = linksRepo.getLinks(user.getIdentifier(), hasProject, project.getIdentifier());
        for(Relationship r : links){
            //TODO: actually delete links
            //linksRepo.delete(r.getIdentifier());
        }
    }
}
