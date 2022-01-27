package ca.corefacility.bioinformatics.irida.ria.unit.web.projects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

public class ProjectTestUtils {
	private ProjectService projectService;
	private UserService userService;
	private static Project project = null;
	private static final String USER_NAME = "testme";
	private static final User user = new User(USER_NAME, null, null, null, null, null);
	private static final String PROJECT_NAME = "test_project";
	private static final Long PROJECT_ID = 1L;
	private static final Long PROJECT_MODIFIED_DATE = 1403723706L;
	public static final String PROJECT_ORGANISM = "E. coli";

	public ProjectTestUtils(ProjectService projectService, UserService userService) {
		this.projectService = projectService;
		this.userService = userService;
	}

	/**
	 * Mocks the information found within the project sidebar.
	 */
	public void mockSidebarInfo() {
		Project project = getProject();
		Collection<Join<Project, User>> ownerList = new ArrayList<>();
		ownerList.add(new ProjectUserJoin(project, user, ProjectRole.PROJECT_OWNER));
		when(userService.getUsersForProjectByRole(any(Project.class), any(ProjectRole.class))).thenReturn(ownerList);
		when(projectService.read(PROJECT_ID)).thenReturn(project);
		when(userService.getUserByUsername(anyString())).thenReturn(user);
	}

	public Project getProject() {
		if (project == null) {
			project = new Project(PROJECT_NAME);
			project.setId(PROJECT_ID);
			project.setOrganism(PROJECT_ORGANISM);
			project.setModifiedDate(new Date(PROJECT_MODIFIED_DATE));
		}
		return project;
	}

	public Collection<Join<Project, User>> getUsersForProject(Project project) {
		Collection<Join<Project, User>> users = new ArrayList<>();
		users.add(new ProjectUserJoin(project, new User("tester1", "test@me.com", "", "Test", "Test2", "234234"),
				ProjectRole.PROJECT_USER));
		users.add(new ProjectUserJoin(project, new User("tester2", "test@me.com", "", "Test", "Test23", "213231"),
				ProjectRole.PROJECT_OWNER));
		return users;
	}
}
