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

import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.SailStore;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Relationship;
import ca.corefacility.bioinformatics.irida.model.User;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;

/**
 *
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class ProjectSesameRepositoryTest {
    
    SailStore store;
    private ProjectSesameRepository repo;
    private UserSesameRepository urepo;
    
    @Before
    public void setUp() {
        store = new SailStore();
        store.initialize();
        AuditRepository auditRepo = new AuditRepository(store);
        RelationshipSesameRepository linksRepo = new RelationshipSesameRepository(store, auditRepo);
        repo = new ProjectSesameRepository(store,auditRepo,linksRepo);
        urepo = new UserSesameRepository(store, auditRepo, linksRepo);
        Project p = new Project();
        p.setName("p1");
        repo.create(p);
        p = new Project();
        p.setName("p2");
        repo.create(p);
        p = new Project();
        p.setName("p3");
        repo.create(p);

    }
    
    @Test
    public void testAddUserToProject() throws MalformedQueryException, RepositoryException, QueryEvaluationException{
        Project project = repo.create(new Project("a new project"));
        User user = urepo.create(new User("anon", "anon@nowhere.com", "PASSWOD!1", "Anon", "Guy", "1234"));
        
        Relationship addUserToProject = repo.addUserToProject(project, user);
        assertNotNull(addUserToProject);
        ObjectConnection con = store.getRepoConnection();
        String qs = "ASK {?sub ?pred ?obj}";
        ValueFactory vf = con.getValueFactory();
        BooleanQuery query = con.prepareBooleanQuery(QueryLanguage.SPARQL, qs);
        URI sub = vf.createURI(user.getIdentifier().getUri().toString());
        URI pred = addUserToProject.getPredicate().getPredicateURI(con);
        URI obj = vf.createURI(project.getIdentifier().getUri().toString());
        query.setBinding("sub", sub);
        query.setBinding("pred", pred);
        query.setBinding("obj", obj);
        boolean evaluate = query.evaluate();
        assertTrue(evaluate);
        con.close();
    }
    
    /*
     * Doesn't work as expected right now because we can't list relationships without inference from OWLIM right now.  hopefully updated soon
     * @Test
    public void testRemoveUserFromProject() throws MalformedQueryException, RepositoryException, QueryEvaluationException{
        Project project = repo.create(new Project("a new project"));
        User user = urepo.create(new User("anon", "anon@nowhere.com", "PASSWOD!1", "Anon", "Guy", "1234"));
        
        Relationship addUserToProject = repo.addUserToProject(project, user);
        assertNotNull(addUserToProject);
        repo.removeUserFromProject(project, user);
        
        ObjectConnection con = store.getRepoConnection();
        String qs = "ASK {?sub ?pred ?obj}";
        ValueFactory vf = con.getValueFactory();
        BooleanQuery query = con.prepareBooleanQuery(QueryLanguage.SPARQL, qs);
        URI sub = vf.createURI(user.getIdentifier().getUri().toString());
        URI pred = addUserToProject.getPredicate().getPredicateURI(con);
        URI obj = vf.createURI(project.getIdentifier().getUri().toString());
        query.setBinding("sub", sub);
        query.setBinding("pred", pred);
        query.setBinding("obj", obj);
        boolean evaluate = query.evaluate();
        assertFalse(evaluate);
        con.close();        
    }*/

}