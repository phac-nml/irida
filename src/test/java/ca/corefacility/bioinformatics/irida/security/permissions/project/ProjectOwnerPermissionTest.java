package ca.corefacility.bioinformatics.irida.security.permissions.project;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.UserGroupProjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserGroupJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.security.ProjectSynchronizationAuthenticationToken;

public class ProjectOwnerPermissionTest {

	ProjectOwnerPermission permission;

	@Mock
	ProjectRepository projectRepository;
	@Mock
	UserRepository userRepository;
	@Mock
	ProjectUserJoinRepository pujRepository;
	@Mock
	UserGroupProjectJoinRepository ugpjRepository;

	Project project = new Project();
	Long projectId = 1L;
	User user = new User();

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
		user.setSystemRole(Role.ROLE_USER);
		user.setUsername("tom");

		permission = new ProjectOwnerPermission(projectRepository, userRepository, pujRepository, ugpjRepository);

		when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
		when(userRepository.loadUserByUsername(user.getUsername())).thenReturn(user);
		when(pujRepository.getProjectJoinForUser(project, user))
				.thenReturn(new ProjectUserJoin(project, user, ProjectRole.PROJECT_OWNER));
	}

	@Test
	public void testLocalProject() {
		Authentication authentication = new PreAuthenticatedAuthenticationToken(user, user.getSystemRole());
		boolean customPermissionAllowed = permission.customPermissionAllowed(authentication, project);

		verify(userRepository).loadUserByUsername(user.getUsername());

		assertTrue(customPermissionAllowed, "user should be able to read project");
	}

	@Test
	public void testLocalProjectDenied() {
		User user2 = new User();
		user2.setUsername("bob");
		user2.setSystemRole(Role.ROLE_USER);

		when(userRepository.loadUserByUsername(user2.getUsername())).thenReturn(user2);

		Authentication authentication = new PreAuthenticatedAuthenticationToken(user2, user2.getSystemRole());
		boolean customPermissionAllowed = permission.customPermissionAllowed(authentication, project);

		verify(userRepository).loadUserByUsername(user2.getUsername());

		assertFalse(customPermissionAllowed, "user should not be able to read project");
	}

	@Test
	public void testRemoteProject() {
		project.setRemoteStatus(new RemoteStatus("http://somewhere", null));

		Authentication authentication = new ProjectSynchronizationAuthenticationToken(user);
		boolean customPermissionAllowed = permission.customPermissionAllowed(authentication, project);

		assertTrue(customPermissionAllowed, "user should be able to read project");
	}

	@Test
	public void testRemoteProjectWrongAuth() {
		project.setRemoteStatus(new RemoteStatus("http://somewhere", null));

		Authentication authentication = new PreAuthenticatedAuthenticationToken(user, user.getSystemRole());
		boolean customPermissionAllowed = permission.customPermissionAllowed(authentication, project);

		assertFalse(customPermissionAllowed, "user should not be able to read project");
	}
}
