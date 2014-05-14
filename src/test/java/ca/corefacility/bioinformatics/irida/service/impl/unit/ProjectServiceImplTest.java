package ca.corefacility.bioinformatics.irida.service.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.impl.ProjectServiceImpl;

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
		validator = mock(Validator.class);
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
		Project p = project();
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
	public void testAddSampleToProject() {
		Sample s = new Sample();
		s.setSampleName("sample");
		s.setId(new Long(2222));
		Project p = project();

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
		Project p = project();
		ProjectRole r = ProjectRole.PROJECT_USER;
		ProjectUserJoin join = new ProjectUserJoin(p, u, r);

		when(pujRepository.save(join)).thenReturn(join);

		projectService.addUserToProject(p, u, r);

		verify(pujRepository).save(join);
	}

	@Test(expected = EntityExistsException.class)
	public void testAddUserToProjectTwice() {
		User u = new User("test", "test@nowhere.com", "PASSWOD!1", "Test", "User", "1234");
		u.setId(new Long(1111));
		Project p = project();
		ProjectRole r = ProjectRole.PROJECT_USER;
		ProjectUserJoin join = new ProjectUserJoin(p, u, r);

		when(pujRepository.save(join)).thenThrow(new DataIntegrityViolationException("Duplicates."));

		projectService.addUserToProject(p, u, r);
	}

	@Test
	public void testAddSampleToProjectNoSamplePersisted() {
		Project p = project();
		Sample s = new Sample();
		s.setExternalSampleId("external");
		s.setSampleName("name");
		Set<ConstraintViolation<Sample>> noViolations = new HashSet<>();

		when(validator.validate(s)).thenReturn(noViolations);
		when(sampleRepository.save(s)).thenReturn(s);

		projectService.addSampleToProject(p, s);

		verify(sampleRepository).save(s);
		verify(psjRepository).save(new ProjectSampleJoin(p, s));
	}

	@Test(expected = ConstraintViolationException.class)
	public void testAddSampleToProjectNoSamplePersistedInvalidSample() {
		Project p = project();
		Sample s = new Sample();
		s.setExternalSampleId("external");
		s.setSampleName("name");
		Set<ConstraintViolation<Sample>> violations = new HashSet<>();
		violations.add(ConstraintViolationImpl.forBeanValidation(null, null, Sample.class, null, null, null, null,
				null, null));

		when(validator.validate(s)).thenReturn(violations);

		projectService.addSampleToProject(p, s);

		verifyZeroInteractions(sampleRepository, psjRepository);
	}

	@Test(expected = EntityExistsException.class)
	public void testAddSampleToProjectAlreadyAdded() {
		Project p = project();
		Sample s = new Sample();
		s.setExternalSampleId("external");
		s.setSampleName("name");
		Set<ConstraintViolation<Sample>> noViolations = new HashSet<>();

		when(validator.validate(s)).thenReturn(noViolations);
		when(sampleRepository.save(s)).thenReturn(s);
		when(psjRepository.save(any(ProjectSampleJoin.class))).thenThrow(
				new DataIntegrityViolationException("duplicate"));

		projectService.addSampleToProject(p, s);

		verify(sampleRepository).save(s);
	}

	@Test
	public void testUserHasProjectRole() {
		Project p = project();
		User u = new User();

		List<Join<Project, User>> joins = new ArrayList<>();
		joins.add(new ProjectUserJoin(p, u,ProjectRole.PROJECT_OWNER));

		when(pujRepository.getProjectsForUserWithRole(u, ProjectRole.PROJECT_OWNER)).thenReturn(joins);

		assertTrue("User has ownership of project.", projectService.userHasProjectRole(u, p, ProjectRole.PROJECT_OWNER));
	}

	private Project project() {
		Project p = new Project("project");
		p.setId(new Long(2222));
		return p;
	}
}
