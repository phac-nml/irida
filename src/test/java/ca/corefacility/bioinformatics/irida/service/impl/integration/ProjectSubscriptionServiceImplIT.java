package ca.corefacility.bioinformatics.irida.service.impl.integration;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.test.context.support.WithMockUser;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.subscription.ProjectSubscription;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ProjectSubscriptionService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ServiceIntegrationTest
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/ProjectSubscriptionServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectSubscriptionServiceImplIT {
	@Autowired
	private ProjectSubscriptionService projectSubscriptionService;
	@Autowired
	private UserService userService;
	@Autowired
	private ProjectService projectService;

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void getProjectSubscriptionsForUserTest() {
		final User user = userService.read(2L);

		Page<ProjectSubscription> page = projectSubscriptionService.getProjectSubscriptionsForUser(user, 0, 10,
				Sort.by(Direction.ASC, "id"));

		assertEquals(1, page.getTotalElements());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void getUsersWithEmailSubscriptionsTest() {
		final User user = userService.read(7L);

		List<User> users = projectSubscriptionService.getUsersWithEmailSubscriptions();

		assertEquals(1, users.size(), "Should be 1 user.");
		assertEquals(user, users.get(0), "User returned is incorrect.");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void getProjectsForUserWithEmailSubscriptionsTest() {
		final User user = userService.read(7L);

		List<Project> projects = projectSubscriptionService.getProjectsForUserWithEmailSubscriptions(user);

		assertEquals(2, projects.size(), "Should be 2 projects.");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void addProjectSubscriptionForProjectAndUserTest() {
		final User user = userService.read(1L);
		final Project project = projectService.read(9L);

		ProjectSubscription projectSubscription = projectSubscriptionService.addProjectSubscriptionForProjectAndUser(
				project, user);

		assertEquals(user, projectSubscription.getUser(), "User returned is incorrect.");
		assertEquals(project, projectSubscription.getProject(), "Project returned is incorrect.");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void removeProjectSubscriptionForProjectAndUserTest() {
		final User user = userService.read(8L);
		final Project project = projectService.read(2L);

		projectSubscriptionService.removeProjectSubscriptionForProjectAndUser(project, user);

		assertThrows(EntityNotFoundException.class, () -> {
			projectSubscriptionService.read(11L);
		});
	}
}
