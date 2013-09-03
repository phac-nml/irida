package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.*;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.After;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class ProjectServiceImplTest {
	private ProjectService projectService;
	private ProjectRepository projectRepository;
	private CRUDRepository<Long, Sample> sampleRepository;
	private UserRepository userRepository;
	private Validator validator;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		projectRepository = mock(ProjectRepository.class);
		sampleRepository = (CRUDRepository<Long, Sample>) mock(CRUDRepository.class);
		userRepository = mock(UserRepository.class);
		projectService = new ProjectServiceImpl(projectRepository,
				sampleRepository, userRepository, validator);
	}
        
        @After
        public void tearDown(){
            SecurityContextHolder.getContext().setAuthentication(null);
        }
	
	@Test
	public void testCreateProject() {
		// The currently logged-in user should be added to the project when it's created.
		Project p = new Project();
		p.setName("Project");
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);
		
		Authentication auth = new UsernamePasswordAuthenticationToken(u, null);
		SecurityContextHolder.getContext().setAuthentication(auth);
		
		when(projectRepository.create(p)).thenReturn(p);
		when(userRepository.getUserByUsername(username)).thenReturn(u);
		
		projectService.create(p);
		
		verify(projectRepository).create(p);
		verify(userRepository).getUserByUsername(username);
	}

	@Test
	public void testAddSampleToProject() {
		Sample s = new Sample();
		s.setSampleName("sample");
		s.setId(new Long(2222));
		Project p = new Project();
		p.setName("project");
		p.setId(new Long(1111));

		when(projectRepository.addSampleToProject(p, s)).thenReturn(
				new ProjectSampleJoin(p, s));

		Join<Project, Sample> rel = projectService.addSampleToProject(p, s);

		verify(projectRepository).addSampleToProject(p, s);
		verifyZeroInteractions(sampleRepository);

		assertNotNull(rel);
		assertEquals(rel.getSubject(), p);
		assertEquals(rel.getObject(), s);

	}

	@Test
	public void testAddSampleToProjectNotPersisted() {
		Sample s = new Sample();
		s.setSampleName("sample");
                s.setExternalSampleId("newsample");
		Sample withId = new Sample();
		withId.setSampleName("sample");
		withId.setId(new Long(1111));

		Project p = new Project();
		p.setName("project");
		p.setId(new Long(2222));

		when(projectRepository.addSampleToProject(p, s)).thenReturn(
				new ProjectSampleJoin(p, withId));
		when(sampleRepository.create(s)).thenReturn(withId);

		Join<Project, Sample> rel = projectService.addSampleToProject(p, s);

		verify(projectRepository).addSampleToProject(p, s);
		verify(sampleRepository).create(s);

		assertNotNull(rel);
		assertEquals(rel.getSubject(), p);
		assertEquals(rel.getObject(), withId);
	}

	@Test
	public void testAddUserToProject() {
		User u = new User("test", "test@nowhere.com", "PASSWOD!1", "Test",
				"User", "1234");
		u.setId(new Long(1111));
		Project p = new Project("project");
		p.setId(new Long(2222));
		Role r = new Role("ROLE_USER");

		when(projectRepository.addUserToProject(p, u)).thenReturn(
				new ProjectUserJoin(p, u));

		Join<Project, User> rel = projectService.addUserToProject(p, u, r);

		assertNotNull(rel);
		assertEquals(rel.getSubject(), p);
		assertEquals(rel.getObject(), u);

		verify(projectRepository).addUserToProject(p, u);
	}
}
