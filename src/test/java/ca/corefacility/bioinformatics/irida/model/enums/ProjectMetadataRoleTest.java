package ca.corefacility.bioinformatics.irida.model.enums;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProjectMetadataRoleTest {

	private User user = new User(1L, "jdoe", "john.doe@somewhere.com", "ABCDEFGHIJ", "John", "Doe", "6666");
	private Project project = new Project("NewProject");
	private ProjectUserJoin levelOnePuj = new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER,
			ProjectMetadataRole.LEVEL_1);
	private ProjectUserJoin levelFourPuj = new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER,
			ProjectMetadataRole.LEVEL_4);
	private UserGroup group = new UserGroup("NewGroup");
	private UserGroupProjectJoin levelOneUgjp = new UserGroupProjectJoin(project, group, ProjectRole.PROJECT_USER,
			ProjectMetadataRole.LEVEL_1);
	private UserGroupProjectJoin levelFourUgjp = new UserGroupProjectJoin(project, group, ProjectRole.PROJECT_USER,
			ProjectMetadataRole.LEVEL_4);

	@Test
	public void testGetMaxRoleForProjectAndGroups() {
		ProjectMetadataRole role;

		// when no direct project membership and no group membership
		role = ProjectMetadataRole.getMaxRoleForProjectAndGroups(null,
				Collections.<UserGroupProjectJoin>emptyList());
		assertNull(role, "Expect null when no direct project membership or group membership");

		// when group membership only
		role = ProjectMetadataRole.getMaxRoleForProjectAndGroups(null, List.of(levelOneUgjp));
		assertEquals(role, levelOneUgjp.getMetadataRole(), "Expect to return group membership role");

		// when direct project membership only
		role = ProjectMetadataRole.getMaxRoleForProjectAndGroups(levelOnePuj,
				Collections.<UserGroupProjectJoin>emptyList());
		assertEquals(role, levelOnePuj.getMetadataRole(), "Expect to return project membership role");

		// when group project metadata role is greater than user project role
		role = ProjectMetadataRole.getMaxRoleForProjectAndGroups(levelOnePuj, List.of(levelFourUgjp));
		assertEquals(role, levelFourUgjp.getMetadataRole(), "Expect to return group membership role");

		// when group project metadata role is less than user project metadata role
		role = ProjectMetadataRole.getMaxRoleForProjectAndGroups(levelFourPuj, List.of(levelOneUgjp));
		assertEquals(role, levelFourPuj.getMetadataRole(), "Expect to return project membership role");
	}
}
