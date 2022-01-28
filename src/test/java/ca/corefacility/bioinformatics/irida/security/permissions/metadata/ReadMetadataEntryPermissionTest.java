package ca.corefacility.bioinformatics.irida.security.permissions.metadata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataRestriction;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.UserGroupProjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataEntryRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataRestrictionRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class ReadMetadataEntryPermissionTest {

	ReadMetadataEntryPermission permission;

	@Mock
	UserRepository userRepository;
	@Mock
	ProjectUserJoinRepository projectUserJoinRepository;
	@Mock
	MetadataRestrictionRepository metadataRestrictionRepository;
	@Mock
	ProjectSampleJoinRepository projectSampleJoinRepository;
	@Mock
	MetadataEntryRepository metadataEntryRepository;
	@Mock
	UserGroupProjectJoinRepository userGroupProjectJoinRepository;

	Project project;
	User user;
	User manager;

	Sample sample;

	MetadataTemplateField field;
	MetadataEntry entry;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		permission = new ReadMetadataEntryPermission(metadataEntryRepository, projectSampleJoinRepository,
				metadataRestrictionRepository, projectUserJoinRepository, userRepository, userGroupProjectJoinRepository);

		user = new User();
		user.setUsername("user");
		user.setSystemRole(Role.ROLE_USER);
		user.setId(1L);

		manager = new User();
		manager.setUsername("manager");
		manager.setSystemRole(Role.ROLE_USER);
		manager.setId(2L);

		sample = new Sample("sample");

		field = new MetadataTemplateField("name", "text");
		entry = new MetadataEntry("test", "text");
		entry.setField(field);
		entry.setSample(sample);

		project = new Project("project");

		when(userRepository.loadUserByUsername(user.getUsername())).thenReturn(user);
		when(userRepository.loadUserByUsername(manager.getUsername())).thenReturn(manager);

		when(metadataEntryRepository.getMetadataForSample(sample)).thenReturn(Sets.newHashSet(entry));
	}

	@Test
	public void testAllowed() {
		Authentication authentication = new PreAuthenticatedAuthenticationToken(manager, manager.getSystemRole());

		when(metadataRestrictionRepository.getRestrictionForFieldAndProject(project, field)).thenReturn(
				new MetadataRestriction(project, field, ProjectMetadataRole.LEVEL_4));
		when(projectSampleJoinRepository.getProjectForSample(sample)).thenReturn(
				Lists.newArrayList(new ProjectSampleJoin(project, sample, true)));

		when(projectUserJoinRepository.getProjectJoinForUser(project, manager)).thenReturn(
				new ProjectUserJoin(project, user, ProjectRole.PROJECT_OWNER, ProjectMetadataRole.LEVEL_4));

		assertTrue(permission.isAllowed(authentication, entry), "should be allowed to read");
	}

	@Test
	public void testUserNotOnProject() {
		Authentication authentication = new PreAuthenticatedAuthenticationToken(manager, manager.getSystemRole());

		when(metadataRestrictionRepository.getRestrictionForFieldAndProject(project, field)).thenReturn(
				new MetadataRestriction(project, field, ProjectMetadataRole.LEVEL_4));

		when(projectSampleJoinRepository.getProjectForSample(sample)).thenReturn(
				Lists.newArrayList(new ProjectSampleJoin(project, sample, true)));

		assertFalse(permission.isAllowed(authentication, entry), "should not be allowed to read");
	}

	@Test
	public void testInsufficientRole() {
		Authentication authentication = new PreAuthenticatedAuthenticationToken(manager, manager.getSystemRole());

		when(metadataRestrictionRepository.getRestrictionForFieldAndProject(project, field)).thenReturn(
				new MetadataRestriction(project, field, ProjectMetadataRole.LEVEL_4));
		when(projectSampleJoinRepository.getProjectForSample(sample)).thenReturn(
				Lists.newArrayList(new ProjectSampleJoin(project, sample, true)));

		when(projectUserJoinRepository.getProjectJoinForUser(project, manager)).thenReturn(
				new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1));

		assertFalse(permission.isAllowed(authentication, entry), "should not be allowed read");
	}

	@Test
	public void testMultipleProjects() {
		Authentication authentication = new PreAuthenticatedAuthenticationToken(manager, manager.getSystemRole());

		Project project2 = new Project("project2");

		//2 projects with different restrictions
		when(metadataRestrictionRepository.getRestrictionForFieldAndProject(project, field)).thenReturn(
				new MetadataRestriction(project, field, ProjectMetadataRole.LEVEL_4));
		when(metadataRestrictionRepository.getRestrictionForFieldAndProject(project2, field)).thenReturn(
				new MetadataRestriction(project2, field, ProjectMetadataRole.LEVEL_1));

		when(projectSampleJoinRepository.getProjectForSample(sample)).thenReturn(
				Lists.newArrayList(new ProjectSampleJoin(project, sample, true),
						new ProjectSampleJoin(project2, sample, true)));

		when(projectUserJoinRepository.getProjectJoinForUser(project, manager)).thenReturn(
				new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1));

		when(projectUserJoinRepository.getProjectJoinForUser(project2, manager)).thenReturn(
				new ProjectUserJoin(project2, user, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1));

		assertTrue(permission.isAllowed(authentication, entry), "should be allowed to read");
	}

	@Test
	public void testMultipleProjectsUserNotOnOneDisallow() {
		Authentication authentication = new PreAuthenticatedAuthenticationToken(manager, manager.getSystemRole());

		Project project2 = new Project("project2");

		//2 projects with different restrictions
		when(metadataRestrictionRepository.getRestrictionForFieldAndProject(project, field)).thenReturn(
				new MetadataRestriction(project, field, ProjectMetadataRole.LEVEL_4));
		when(metadataRestrictionRepository.getRestrictionForFieldAndProject(project2, field)).thenReturn(
				new MetadataRestriction(project2, field, ProjectMetadataRole.LEVEL_1));

		when(projectSampleJoinRepository.getProjectForSample(sample)).thenReturn(
				Lists.newArrayList(new ProjectSampleJoin(project2, sample, true),
						new ProjectSampleJoin(project, sample, true)));

		when(projectUserJoinRepository.getProjectJoinForUser(project, manager)).thenReturn(
				new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1));

		assertFalse(permission.isAllowed(authentication, entry), "should not be allowed to read");
	}

	@Test
	public void testMultipleProjectsUserNotOnOneAllow() {
		Authentication authentication = new PreAuthenticatedAuthenticationToken(manager, manager.getSystemRole());

		Project project2 = new Project("project2");

		//2 projects with different restrictions
		when(metadataRestrictionRepository.getRestrictionForFieldAndProject(project, field)).thenReturn(
				new MetadataRestriction(project, field, ProjectMetadataRole.LEVEL_4));
		when(metadataRestrictionRepository.getRestrictionForFieldAndProject(project2, field)).thenReturn(
				new MetadataRestriction(project2, field, ProjectMetadataRole.LEVEL_1));

		when(projectSampleJoinRepository.getProjectForSample(sample)).thenReturn(
				Lists.newArrayList(new ProjectSampleJoin(project2, sample, true),
						new ProjectSampleJoin(project, sample, true)));

		when(projectUserJoinRepository.getProjectJoinForUser(project, manager)).thenReturn(
				new ProjectUserJoin(project, user, ProjectRole.PROJECT_OWNER, ProjectMetadataRole.LEVEL_4));

		assertTrue(permission.isAllowed(authentication, entry), "should be allowed to read");
	}

}
