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
package ca.corefacility.bioinformatics.irida.repositories.relational;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Transactional
@Repository
public class ProjectRelationalRepository extends GenericRelationalRepository<Project> implements ProjectRepository{    
    
    public ProjectRelationalRepository(){}
    
    public ProjectRelationalRepository(DataSource source){
        super(source,Project.class);
    }
    
    @Transactional
    @Override
    public Collection<ProjectUserJoin> getProjectsForUser(User user) {
        Session session = sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(ProjectUserJoin.class);
        crit.add(Restrictions.eq("user", user));
        List<ProjectUserJoin> list = crit.list();
        
        return list;
    }

    @Transactional
    @Override
    public ProjectUserJoin addUserToProject(Project project, User user) {
        Session session = sessionFactory.getCurrentSession();

        ProjectUserJoin ujoin = new ProjectUserJoin(project, user);
        session.save(ujoin);
        
        
        return ujoin;
    }

    @Transactional
    @Override
    public void removeUserFromProject(Project project, User user) {        
        Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(ProjectUserJoin.class);
        crit.add(Restrictions.eq("project", project));
        crit.add(Restrictions.eq("user", user));
        
        ProjectUserJoin join = (ProjectUserJoin) crit.uniqueResult();
        if(join == null){
            throw new EntityNotFoundException("A join between this user and project was not found");
        }
        session.delete(join);
    }

    @Override
    public ProjectSampleJoin addSampleToProject(Project project, Sample sample) {
        Session session = sessionFactory.getCurrentSession();

        ProjectSampleJoin ujoin = new ProjectSampleJoin(project, sample);
        session.save(ujoin);
        
        return ujoin;        
    }

    @Override
    public Collection<ProjectSampleJoin> getProjectForSample(Sample sample) {
        Session session = sessionFactory.getCurrentSession();

        Criteria crit = session.createCriteria(ProjectUserJoin.class);
        crit.add(Restrictions.eq("sample", sample));
        List<ProjectSampleJoin> list = crit.list();
        
        return list;    
    }
    
    
}
