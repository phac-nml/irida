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
package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.*;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.RelationshipRepository;
import ca.corefacility.bioinformatics.irida.repositories.sesame.dao.RdfPredicate;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import org.junit.Before;
import org.junit.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class ProjectServiceImplTest {
    private ProjectService projectService;
    private ProjectRepository projectRepository;
    private RelationshipRepository relationshipRepository;
    private CRUDRepository<Identifier, Sample> sampleRepository;
    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        projectRepository = mock(ProjectRepository.class);
        relationshipRepository = mock(RelationshipRepository.class);
        sampleRepository = mock(CRUDRepository.class);
        projectService = new ProjectServiceImpl(projectRepository, relationshipRepository, sampleRepository, validator);
    }

    @Test
    public void testAddSampleToProject() {
        Sample s = new Sample();
        s.setSampleName("sample");
        s.setIdentifier(new Identifier());
        Project p = new Project();
        p.setName("project");
        p.setIdentifier(new Identifier());

        when(relationshipRepository.create(p, s))
                .thenReturn(new Relationship(p.getIdentifier(), new RdfPredicate("irida", "hasSample"), s.getIdentifier()));

        Relationship rel = projectService.addSampleToProject(p, s);

        verify(relationshipRepository).create(p, s);
        verifyZeroInteractions(sampleRepository);

        assertNotNull(rel);
        assertEquals(rel.getSubject(), p.getIdentifier());
        assertEquals(rel.getObject(), s.getIdentifier());

    }

    @Test
    public void testAddSampleToProjectNotPersisted() {
        Sample s = new Sample();
        s.setSampleName("sample");
        Sample withId = new Sample();
        withId.setSampleName("sample");
        withId.setIdentifier(new Identifier());
        Project p = new Project();
        p.setName("project");
        p.setIdentifier(new Identifier());

        when(relationshipRepository.create(p, s))
                .thenReturn(new Relationship(p.getIdentifier(), new RdfPredicate("irida", "hasSample"), withId.getIdentifier()));
        when(sampleRepository.create(s)).thenReturn(withId);

        Relationship rel = projectService.addSampleToProject(p, s);

        verify(relationshipRepository).create(p, s);
        verify(sampleRepository).create(s);

        assertNotNull(rel);
        assertEquals(rel.getSubject(), p.getIdentifier());
        assertEquals(rel.getObject(), withId.getIdentifier());
    }

    @Test
    public void testAddUserToProject() {
        User u = new User("test", "test@nowhere.com", "PASSWOD!1", "Test", "User", "1234");
        Project p = new Project("project");
        Role r = new Role("ROLE_USER");

        when(projectRepository.addUserToProject(p, u))
                .thenReturn(new Relationship(p.getIdentifier(), new RdfPredicate("irida", "hasUser"), u.getIdentifier()));

        Relationship rel = projectService.addUserToProject(p, u, r);
        assertNotNull(rel);
        assertEquals(rel.getSubject(), p.getIdentifier());
        assertEquals(rel.getObject(), u.getIdentifier());

        verify(projectRepository).addUserToProject(p, u);
    }

    /**
     * Test of removeSampleFromProject method, of class ProjectServiceImpl.
     */
    @Test
    public void testRemoveSampleFromProject() {
        Sample s = new Sample();
        s.setSampleName("sample");
        Project p = new Project("project");
        List<Relationship> rels = new ArrayList<>();
        rels.add(new Relationship(p.getIdentifier(), new RdfPredicate("irida", "hasSample"), s.getIdentifier()));
        when(relationshipRepository.getLinks(p.getIdentifier(), null, s.getIdentifier())).thenReturn(rels);

        projectService.removeSampleFromProject(p, s);
    }

    /**
     * Test of removeSequenceFileFromProject method, of class ProjectServiceImpl.

     @Test public void testRemoveSequenceFileFromProject() {
     System.out.println("removeSequenceFileFromProject");
     Project project = null;
     SequenceFile sf = null;
     ProjectServiceImpl instance = null;
     instance.removeSequenceFileFromProject(project, sf);
     // TODO review the generated test code and remove the default call to fail.
     fail("The test case is a prototype.");
     }*/

    /**
     * Test of getProjectsForUser method, of class ProjectServiceImpl.

     @Test public void testGetProjectsForUser() {
     System.out.println("getProjectsForUser");
     User user = null;
     ProjectServiceImpl instance = null;
     Collection expResult = null;
     Collection result = instance.getProjectsForUser(user);
     assertEquals(expResult, result);
     // TODO review the generated test code and remove the default call to fail.
     fail("The test case is a prototype.");
     }*/
}
