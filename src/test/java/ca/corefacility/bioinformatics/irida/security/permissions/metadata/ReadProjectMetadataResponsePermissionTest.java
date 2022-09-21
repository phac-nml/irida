package ca.corefacility.bioinformatics.irida.security.permissions.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataRestriction;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.ProjectMetadataResponse;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.UserGroupProjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sample.MetadataRestrictionRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class ReadProjectMetadataResponsePermissionTest {

	ReadProjectMetadataResponsePermission permission;

	@Mock
	UserRepository userRepository;
	@Mock
	ProjectUserJoinRepository projectUserJoinRepository;
	@Mock
	UserGroupProjectJoinRepository userGroupProjectJoinRepository;
	@Mock
	MetadataRestrictionRepository metadataRestrictionRepository;

	User user;
	User admin;
	Project project;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		permission = new ReadProjectMetadataResponsePermission(userRepository, projectUserJoinRepository,
				userGroupProjectJoinRepository, metadataRestrictionRepository);

		admin = new User();
		admin.setUsername("admin");
		admin.setSystemRole(Role.ROLE_ADMIN);
		admin.setId(1L);

		user = new User();
		user.setUsername("user");
		user.setSystemRole(Role.ROLE_USER);
		user.setId(2L);

		when(userRepository.loadUserByUsername(user.getUsername())).thenReturn(user);
		when(userRepository.loadUserByUsername(admin.getUsername())).thenReturn(admin);

		project = new Project("project");
		when(projectUserJoinRepository.getProjectJoinForUser(project, user)).thenReturn(
				new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1));

	}

	@Test
	public void testNoRestrictions() {
		Authentication authentication = new PreAuthenticatedAuthenticationToken(user, user.getSystemRole());

		MetadataTemplateField field = new MetadataTemplateField("name", "text");
		field.setId(1L);
		MetadataEntry entry = new MetadataEntry("test", "text");
		entry.setField(field);

		Map<Long, Set<MetadataEntry>> entries = new HashMap<>();
		entries.put(1L, Sets.newHashSet(entry));

		ProjectMetadataResponse metadataResponse = new ProjectMetadataResponse(project, entries);

		when(metadataRestrictionRepository.getRestrictionForProject(project)).thenReturn(Lists.newArrayList());

		assertTrue(permission.isAllowed(authentication, metadataResponse), "permission should be allowed");
	}

	@Test
	public void testFalseLevelRestrictions() {
		Authentication authentication = new PreAuthenticatedAuthenticationToken(user, user.getSystemRole());

		MetadataTemplateField field = new MetadataTemplateField("name", "text");
		field.setId(1L);
		MetadataEntry entry = new MetadataEntry("test", "text");
		entry.setField(field);

		Map<Long, Set<MetadataEntry>> entries = new HashMap<>();
		entries.put(1L, Sets.newHashSet(entry));

		ProjectMetadataResponse metadataResponse = new ProjectMetadataResponse(project, entries);

		when(metadataRestrictionRepository.getRestrictionForProject(project)).thenReturn(
				Lists.newArrayList(new MetadataRestriction(project, field, ProjectMetadataRole.LEVEL_4)));

		assertFalse(permission.isAllowed(authentication, metadataResponse), "permission should be denied");
	}

	@Test
	public void testValidLevelRestrictions() {
		Authentication authentication = new PreAuthenticatedAuthenticationToken(user, user.getSystemRole());

		MetadataTemplateField field = new MetadataTemplateField("name", "text");
		field.setId(1L);
		MetadataEntry entry = new MetadataEntry("test", "text");
		entry.setField(field);

		Map<Long, Set<MetadataEntry>> entries = new HashMap<>();
		entries.put(1L, Sets.newHashSet(entry));

		ProjectMetadataResponse metadataResponse = new ProjectMetadataResponse(project, entries);

		when(metadataRestrictionRepository.getRestrictionForProject(project)).thenReturn(
				Lists.newArrayList(new MetadataRestriction(project, field, ProjectMetadataRole.LEVEL_1)));

		assertTrue(permission.isAllowed(authentication, metadataResponse), "permission should be allowed");
	}

	@Test
	public void testMultipleLevelRestrictions() {
		Authentication authentication = new PreAuthenticatedAuthenticationToken(user, user.getSystemRole());

		MetadataTemplateField field = new MetadataTemplateField("name", "text");
		field.setId(1L);
		MetadataEntry entry = new MetadataEntry("test", "text");
		entry.setField(field);

		MetadataTemplateField field2 = new MetadataTemplateField("name2", "text");
		field2.setId(2L);
		MetadataEntry entry2 = new MetadataEntry("test2", "text");
		entry2.setField(field2);

		Map<Long, Set<MetadataEntry>> entries = new HashMap<>();
		entries.put(1L, Sets.newHashSet(entry, entry2));

		ProjectMetadataResponse metadataResponse = new ProjectMetadataResponse(project, entries);

		MetadataRestriction restriction1 = new MetadataRestriction(project, field, ProjectMetadataRole.LEVEL_1);
		MetadataRestriction restriction2 = new MetadataRestriction(project, field2, ProjectMetadataRole.LEVEL_4);

		when(metadataRestrictionRepository.getRestrictionForProject(project)).thenReturn(
				Lists.newArrayList(restriction1, restriction2));

		assertFalse(permission.isAllowed(authentication, metadataResponse), "permission should be denied");
	}

	@Test
	public void testAdminAllowed() {
		Authentication authentication = new PreAuthenticatedAuthenticationToken(admin, admin.getSystemRole());

		MetadataTemplateField field = new MetadataTemplateField("name", "text");
		field.setId(1L);
		MetadataEntry entry = new MetadataEntry("test", "text");
		entry.setField(field);

		Map<Long, Set<MetadataEntry>> entries = new HashMap<>();
		entries.put(1L, Sets.newHashSet(entry));

		ProjectMetadataResponse metadataResponse = new ProjectMetadataResponse(project, entries);

		when(metadataRestrictionRepository.getRestrictionForProject(project)).thenReturn(
				Lists.newArrayList(new MetadataRestriction(project, field, ProjectMetadataRole.LEVEL_4)));

		assertTrue(permission.isAllowed(authentication, metadataResponse), "permission should be allowed");
	}

	@Test
	public void testAdminOnProjectDisallowed() {
		Authentication authentication = new PreAuthenticatedAuthenticationToken(admin, admin.getSystemRole());

		when(projectUserJoinRepository.getProjectJoinForUser(project, admin)).thenReturn(
				new ProjectUserJoin(project, admin, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1));

		MetadataTemplateField field = new MetadataTemplateField("name", "text");
		field.setId(1L);
		MetadataEntry entry = new MetadataEntry("test", "text");
		entry.setField(field);

		MetadataTemplateField field2 = new MetadataTemplateField("name2", "text");
		field.setId(2L);
		MetadataEntry entry2 = new MetadataEntry("test2", "text");
		entry2.setField(field2);

		Map<Long, Set<MetadataEntry>> entries = new HashMap<>();
		entries.put(1L, Sets.newHashSet(entry, entry2));

		ProjectMetadataResponse metadataResponse = new ProjectMetadataResponse(project, entries);

		MetadataRestriction restriction1 = new MetadataRestriction(project, field, ProjectMetadataRole.LEVEL_1);
		MetadataRestriction restriction2 = new MetadataRestriction(project, field2, ProjectMetadataRole.LEVEL_4);

		when(metadataRestrictionRepository.getRestrictionForProject(project)).thenReturn(
				Lists.newArrayList(restriction1, restriction2));

		assertFalse(permission.isAllowed(authentication, metadataResponse), "permission should be denied");
	}
}
