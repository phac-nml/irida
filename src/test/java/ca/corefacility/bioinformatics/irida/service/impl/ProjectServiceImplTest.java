package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.roles.impl.UserIdentifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.memory.CRUDMemoryRepository;
import ca.corefacility.bioinformatics.irida.repositories.memory.ProjectMemoryRepository;
import ca.corefacility.bioinformatics.irida.repositories.memory.UserMemoryRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import java.util.Collection;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test the business logic for the {@link ProjectServiceImpl}.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class ProjectServiceImplTest {

//    private ProjectService projectService;
//    private CRUDRepository<UserIdentifier, User> userRepository;
//    private CRUDRepository<Identifier, Project> projectRepository;
//    private CRUDRepository<Identifier, Sample> sampleRepository;
//    private Validator validator;
//    Logger log = LoggerFactory.getLogger(ProjectServiceImplTest.class);
//
//    @Before
//    public void setUp() {
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
//        userRepository = new UserMemoryRepository();
//        projectRepository = new ProjectMemoryRepository();
//        sampleRepository = new CRUDMemoryRepository<>(Sample.class);
//        projectService = new ProjectServiceImpl(projectRepository, userRepository, sampleRepository, validator);
//    }
//
//    /**
//     * When a user is added to a project, they should be added by role. The user
//     * should also have the project added to their projects collection.
//     */
//    @Test
//    public void testAddUserToProject() {
//        Project p = new Project();
//        User u = new User();
//        Role r = new Role();
//
//        u.setUsername("super-user");
//        r.setName("ROLE_MANAGER");
//
//        // create the user and project
//        p = projectRepository.create(p);
//        u = userRepository.create(u);
//
//        // add the user to the project
//        projectService.addUserToProject(p, u, r);
//
//        // get the new versions of the files out of the database
//        p = projectRepository.read(p.getIdentifier());
//        u = userRepository.read(u.getIdentifier());
//
//        // assert that the changes were correctly made
//        assertEquals(1, p.getUsersByRole(r).size());
//        assertTrue(u.getProjects().containsKey(p));
//    }
//
//    @Test
//    public void testAddSampleToProject() {
//        Project p = new Project();
//        Sample s = new Sample();
//
//        p = projectRepository.create(p);
//        s = sampleRepository.create(s);
//
//        projectService.addSampleToProject(p, s);
//
//        p = projectRepository.read(p.getIdentifier());
//        s = sampleRepository.read(s.getIdentifier());
//
//        assertEquals(1, p.getSamples().size());
//        assertTrue(p.getSamples().contains(s));
//        assertTrue(s.getProjects().contains(p));
//    }
//
//    @Test
//    public void testGetProjectsForUser() {
//        Project p = new Project();
//        User u = new User();
//        Role r = new Role();
//
//        p.addUserToProject(u, r);
//        u.addProject(p, r);
//
//        u = userRepository.create(u);
//        p = projectRepository.create(p);
//
//        Collection<Project> projects = projectService.getProjectsForUser(u);
//
//        assertEquals(1, projects.size());
//        assertTrue(projects.contains(p));
//    }
}
