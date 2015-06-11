package ca.corefacility.bioinformatics.irida.security.permissions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.RemoteRelatedProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

import com.google.common.collect.Lists;

public class ManageRemoteRelatedProjectPermissionTest {
	private ManageRemoteRelatedProjectPermission permission;

	@Mock
	RemoteRelatedProjectRepository rrpRepository;

	@Mock
	ProjectRepository projectRepository;

	@Mock
	UserRepository userRepository;

	@Mock
	ProjectUserJoinRepository pujRepository;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		permission = new ManageRemoteRelatedProjectPermission(rrpRepository, projectRepository, userRepository,
				pujRepository);
	}

	@Test
	public void testOwner() {
		String username = "aaron";
		User u = new User();
		u.setUsername(username);
		Authentication auth = new UsernamePasswordAuthenticationToken("aaron", "password1");

		Project project = new Project();

		ProjectUserJoin projectUserJoin = new ProjectUserJoin(project, u, ProjectRole.PROJECT_OWNER);

		RemoteRelatedProject rrp = new RemoteRelatedProject(project, null, "http://somewhere");

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(pujRepository.getUsersForProjectByRole(project, ProjectRole.PROJECT_OWNER)).thenReturn(
				Lists.newArrayList(projectUserJoin));

		assertTrue("owner should be allowed", permission.isAllowed(auth, rrp));
	}

	@Test
	public void testUser() {
		String username = "aaron";
		User u = new User();
		u.setUsername(username);
		Authentication auth = new UsernamePasswordAuthenticationToken("aaron", "password1");

		Project project = new Project();

		RemoteRelatedProject rrp = new RemoteRelatedProject(project, null, "http://somewhere");

		when(userRepository.loadUserByUsername(username)).thenReturn(u);
		when(pujRepository.getUsersForProjectByRole(project, ProjectRole.PROJECT_OWNER)).thenReturn(
				Lists.newArrayList());

		assertFalse("user shouldn't be allowed", permission.isAllowed(auth, rrp));
	}
}
