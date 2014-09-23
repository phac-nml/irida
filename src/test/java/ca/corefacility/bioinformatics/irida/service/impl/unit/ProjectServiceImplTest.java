package ca.corefacility.bioinformatics.irida.service.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.RelatedProjectJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectReferenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.RemoteRelatedProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectReferenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.RelatedProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.impl.ProjectServiceImpl;
import ca.corefacility.bioinformatics.irida.service.util.SequenceFileUtilities;

import com.google.common.collect.Lists;

/**
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 * @author Franklin Bristow <franklin.bristow@phac-apsc.gc.ca>
 */
public class ProjectServiceImplTest {
	private ProjectService projectService;
	private ProjectRepository projectRepository;
	private SampleRepository sampleRepository;
	private UserRepository userRepository;
	private ProjectUserJoinRepository pujRepository;
	private ProjectSampleJoinRepository psjRepository;
	private RelatedProjectRepository relatedProjectRepository;
	private ReferenceFileRepository referenceFileRepository;
	private ProjectReferenceFileJoinRepository prfjRepository;
	private RemoteRelatedProjectRepository rrpRepository;
	private SequenceFileUtilities sequenceFileUtilities;
	private Validator validator;

	@Before
	public void setUp() {
		validator = mock(Validator.class);
		projectRepository = mock(ProjectRepository.class);
		sampleRepository = mock(SampleRepository.class);
		userRepository = mock(UserRepository.class);
		pujRepository = mock(ProjectUserJoinRepository.class);
		psjRepository = mock(ProjectSampleJoinRepository.class);
		relatedProjectRepository = mock(RelatedProjectRepository.class);
		referenceFileRepository = mock(ReferenceFileRepository.class);
		prfjRepository = mock(ProjectReferenceFileJoinRepository.class);
		sequenceFileUtilities = mock(SequenceFileUtilities.class);
		rrpRepository = mock(RemoteRelatedProjectRepository.class);
		projectService = new ProjectServiceImpl(projectRepository, sampleRepository, userRepository, pujRepository,
				psjRepository, relatedProjectRepository, referenceFileRepository, prfjRepository, rrpRepository,
				sequenceFileUtilities, validator);
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
		SecurityContextHolder.getContext().setAuthentication(null);
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
		s.setSequencerSampleId("external");
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
		s.setSequencerSampleId("external");
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
		s.setSequencerSampleId("external");
		s.setSampleName("name");
		Set<ConstraintViolation<Sample>> noViolations = new HashSet<>();

		when(validator.validate(s)).thenReturn(noViolations);
		when(sampleRepository.save(s)).thenReturn(s);
		when(psjRepository.save(any(ProjectSampleJoin.class))).thenThrow(
				new DataIntegrityViolationException("duplicate"));

		projectService.addSampleToProject(p, s);

		verify(sampleRepository).save(s);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUserHasProjectRole() {
		Project p = project();
		User u = new User();

		List<ProjectUserJoin> joins = new ArrayList<>();
		joins.add(new ProjectUserJoin(p, u, ProjectRole.PROJECT_OWNER));
		Page<ProjectUserJoin> page = new PageImpl<>(joins);

		when(pujRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

		assertTrue("User has ownership of project.", projectService.userHasProjectRole(u, p, ProjectRole.PROJECT_OWNER));
	}

	@Test
	public void testAddRelatedProject() {
		Project p1 = new Project("project 1");
		Project p2 = new Project("project 2");

		RelatedProjectJoin rp = new RelatedProjectJoin(p1, p2);

		when(relatedProjectRepository.save(any(RelatedProjectJoin.class))).thenReturn(rp);

		RelatedProjectJoin returned = projectService.addRelatedProject(p1, p2);

		assertNotNull(returned);
		assertEquals(rp, returned);

		verify(relatedProjectRepository).save(any(RelatedProjectJoin.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddSameRelatedProject() {
		Project p1 = new Project("project 1");

		projectService.addRelatedProject(p1, p1);
	}

	@Test(expected = EntityExistsException.class)
	public void testAlreadyRelatedProject() {
		Project p1 = new Project("project 1");
		Project p2 = new Project("project 2");

		when(relatedProjectRepository.save(any(RelatedProjectJoin.class))).thenThrow(
				new DataIntegrityViolationException("relation already exists"));

		projectService.addRelatedProject(p1, p2);
	}

	@Test
	public void testGetRelatedProjects() {
		Project p1 = new Project("project 1");
		Project p2 = new Project("project 2");
		Project p3 = new Project("project 3");

		List<RelatedProjectJoin> relatedProjectList = Lists.newArrayList(new RelatedProjectJoin(p1, p2),
				new RelatedProjectJoin(p1, p3));

		when(relatedProjectRepository.getRelatedProjectsForProject(p1)).thenReturn(relatedProjectList);

		List<RelatedProjectJoin> relatedProjects = projectService.getRelatedProjects(p1);
		assertFalse(relatedProjects.isEmpty());
		for (RelatedProjectJoin rp : relatedProjects) {
			assertEquals(p1, rp.getSubject());
		}

		verify(relatedProjectRepository).getRelatedProjectsForProject(p1);
	}

	@Test
	public void testUpdateProjectUserJoin() throws ProjectWithoutOwnerException {
		Project project = new Project("Project 1");
		User user = new User();
		User user2 = new User();
		ProjectRole projectRole = ProjectRole.PROJECT_USER;
		ProjectUserJoin oldJoin = new ProjectUserJoin(project, user, ProjectRole.PROJECT_OWNER);
		@SuppressWarnings("unchecked")
		List<Join<Project, User>> owners = Lists.newArrayList(new ProjectUserJoin(project, user,
				ProjectRole.PROJECT_OWNER), new ProjectUserJoin(project, user2, ProjectRole.PROJECT_OWNER));

		when(pujRepository.getProjectJoinForUser(project, user)).thenReturn(oldJoin);
		when(pujRepository.save(oldJoin)).thenReturn(oldJoin);
		when(pujRepository.getUsersForProjectByRole(project, ProjectRole.PROJECT_OWNER)).thenReturn(owners);

		Join<Project, User> updateUserProjectRole = projectService.updateUserProjectRole(project, user, projectRole);

		assertNotNull(updateUserProjectRole);
		ProjectUserJoin newJoin = (ProjectUserJoin) updateUserProjectRole;
		assertEquals(projectRole, newJoin.getProjectRole());

		verify(pujRepository).getProjectJoinForUser(project, user);
		verify(pujRepository).getUsersForProjectByRole(project, ProjectRole.PROJECT_OWNER);
		verify(pujRepository).save(oldJoin);
	}

	@Test(expected = EntityNotFoundException.class)
	public void testUpdateProjectUserJoinNotExists() throws ProjectWithoutOwnerException {
		Project project = new Project("Project 1");
		User user = new User();
		ProjectRole projectRole = ProjectRole.PROJECT_USER;

		when(pujRepository.getProjectJoinForUser(project, user)).thenReturn(null);

		projectService.updateUserProjectRole(project, user, projectRole);
	}

	@Test(expected = ProjectWithoutOwnerException.class)
	public void testUpdateProjectUserJoinIllegalChange() throws ProjectWithoutOwnerException {
		Project project = new Project("Project 1");
		User user = new User();
		ProjectRole projectRole = ProjectRole.PROJECT_USER;
		ProjectUserJoin oldJoin = new ProjectUserJoin(project, user, ProjectRole.PROJECT_OWNER);
		@SuppressWarnings("unchecked")
		List<Join<Project, User>> owners = Lists.newArrayList(new ProjectUserJoin(project, user,
				ProjectRole.PROJECT_OWNER));

		when(pujRepository.getProjectJoinForUser(project, user)).thenReturn(oldJoin);
		when(pujRepository.getUsersForProjectByRole(project, ProjectRole.PROJECT_OWNER)).thenReturn(owners);

		projectService.updateUserProjectRole(project, user, projectRole);

	}

	@Test
	public void testGetProjectsForSample() {
		Sample sample = new Sample("my sample");
		@SuppressWarnings("unchecked")
		List<Join<Project, Sample>> projects = Lists.newArrayList(new ProjectSampleJoin(new Project("p1"), sample),
				new ProjectSampleJoin(new Project("p2"), sample));

		when(psjRepository.getProjectForSample(sample)).thenReturn(projects);

		List<Join<Project, Sample>> projectsForSample = projectService.getProjectsForSample(sample);
		assertEquals(2, projectsForSample.size());

		verify(psjRepository).getProjectForSample(sample);
	}

	private Project project() {
		Project p = new Project("project");
		p.setId(new Long(2222));
		return p;
	}

	@Test
	public void testAddReferenceFileToProject() throws IOException {
		Project p = new Project();
		Path createTempFile = Files.createTempFile(null, null);
		ReferenceFile f = new ReferenceFile(createTempFile);

		when(referenceFileRepository.save(f)).thenReturn(f);
		when(sequenceFileUtilities.countSequenceFileLengthInBases(createTempFile)).thenReturn(1000l);

		projectService.addReferenceFileToProject(p, f);

		verify(referenceFileRepository).save(f);
		verify(sequenceFileUtilities).countSequenceFileLengthInBases(createTempFile);
		verify(prfjRepository).save(new ProjectReferenceFileJoin(p, f));
	}

	@Test
	public void testGetRemoteProjectsForProject() {
		Project p = new Project();
		List<RemoteRelatedProject> projects = Lists
				.newArrayList(new RemoteRelatedProject(), new RemoteRelatedProject());
		when(rrpRepository.getRelatedProjectsForProject(p)).thenReturn(projects);

		List<RemoteRelatedProject> remoteProjectsForProject = projectService.getRemoteProjectsForProject(p);

		assertEquals(projects, remoteProjectsForProject);
		verify(rrpRepository).getRelatedProjectsForProject(p);
	}

	@Test
	public void testAddRemoteRelatedProject() {
		Project p = new Project();
		RemoteAPI api = new RemoteAPI();
		Long remoteId = 2l;

		projectService.addRemoteRelatedProject(p, api, remoteId);

		ArgumentCaptor<RemoteRelatedProject> captor = ArgumentCaptor.forClass(RemoteRelatedProject.class);
		verify(rrpRepository).save(captor.capture());

		assertEquals(p, captor.getValue().getLocalProject());
		assertEquals(api, captor.getValue().getRemoteAPI());
		assertEquals(remoteId, captor.getValue().getRemoteProjectID());
	}
	
	@Test
	public void testRemoveRemoteRelatedProject(){
		RemoteRelatedProject p = new RemoteRelatedProject();
		projectService.removeRemoteRelatedProject(p);
		verify(rrpRepository).delete(p);
	}
}
