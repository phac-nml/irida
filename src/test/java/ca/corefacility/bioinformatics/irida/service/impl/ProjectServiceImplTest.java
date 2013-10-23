package ca.corefacility.bioinformatics.irida.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

/**
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public class ProjectServiceImplTest {
	private ProjectService projectService;
	private ProjectRepository projectRepository;
	private SampleRepository sampleRepository;
	private UserRepository userRepository;
	private ProjectUserJoinRepository pujRepository;
	private ProjectSampleJoinRepository psjRepository;
	private Validator validator;

	@Before
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		projectRepository = mock(ProjectRepository.class);
		sampleRepository = mock(SampleRepository.class);
		userRepository = mock(UserRepository.class);
		pujRepository = mock(ProjectUserJoinRepository.class);
		psjRepository = mock(ProjectSampleJoinRepository.class);
		projectService = new ProjectServiceImpl(projectRepository, sampleRepository, userRepository, pujRepository,
				psjRepository, validator);
	}

	@After
	public void tearDown() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	@Test
	public void testCreateProject() {
		// The currently logged-in user should be added to the project when it's
		// created.
		Project p = new Project();
		p.setName("Project");
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);

		Authentication auth = new UsernamePasswordAuthenticationToken(u, null);
		SecurityContextHolder.getContext().setAuthentication(auth);

		when(projectRepository.save(p)).thenReturn(p);
		when(userRepository.loadUserByUsername(username)).thenReturn(u);

		projectService.create(p);

		verify(projectRepository).save(p);
		verify(userRepository).loadUserByUsername(username);
	}

	@Test
	public void testCreateProjectInvalidURL() {
		Project p = new Project();
		p.setName("Project");
		p.setRemoteURL("this is not a URL");
		String username = "fbristow";
		User u = new User();
		u.setUsername(username);

		Authentication auth = new UsernamePasswordAuthenticationToken(u, null);
		SecurityContextHolder.getContext().setAuthentication(auth);

		try {
			projectService.create(p);
			fail();
		} catch (ConstraintViolationException ex) {
		}

	}

	@Test
	public void testAddSampleToProject() {
		Sample s = new Sample();
		s.setSampleName("sample");
		s.setId(new Long(2222));
		Project p = new Project();
		p.setName("project");
		p.setId(new Long(1111));
		
		ProjectSampleJoin join = new ProjectSampleJoin(p, s);

		when(psjRepository.save(join)).thenReturn(join);

		Join<Project, Sample> rel = projectService.addSampleToProject(p, s);

		verify(psjRepository).save(join);
		verifyZeroInteractions(sampleRepository);

		assertNotNull(rel);
		assertEquals(rel.getSubject(), p);
		assertEquals(rel.getObject(), s);

	}

	@Test
	public void testAddUserToProject() {
		User u = new User("test", "test@nowhere.com", "PASSWOD!1", "Test", "User", "1234");
		u.setId(new Long(1111));
		Project p = new Project("project");
		p.setId(new Long(2222));
		ProjectRole r = ProjectRole.PROJECT_USER;
		ProjectUserJoin join = new ProjectUserJoin(p, u, r);

		when(pujRepository.save(join)).thenReturn(join);

		projectService.addUserToProject(p, u, r);

		verify(pujRepository).save(join);
	}
}
