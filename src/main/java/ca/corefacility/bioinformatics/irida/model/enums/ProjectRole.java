package ca.corefacility.bioinformatics.irida.model.enums;

import java.util.Collection;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;

/**
 *
 */
public enum ProjectRole {
	PROJECT_USER("PROJECT_USER", 1),
	PROJECT_OWNER("PROJECT_OWNER", 2);

	private String code;
	private int userRoleLevel;

	private ProjectRole(String code, int userRoleLevel) {
		this.code = code;
		this.userRoleLevel = userRoleLevel;
	}

	@Override
	public String toString() {
		return code;
	}

	/**
	 * Get a role from the given string code
	 *
	 * @param code the string to get a role for
	 * @return The requested ProjectRole
	 */
	public static ProjectRole fromString(String code) {
		switch (code.toUpperCase()) {
		case "PROJECT_USER":
			return PROJECT_USER;
		case "PROJECT_OWNER":
			return PROJECT_OWNER;
		default:
			return PROJECT_USER;
		}
	}

	public int getUserRoleLevel() {
		return userRoleLevel;
	}

	/**
	 * Compares and returns the highest level {@link ProjectRole} from a {@link ProjectUserJoin} and a collection of
	 * {@link UserGroupProjectJoin}s.
	 *
	 * @param projectUserJoin       a user's {@link ProjectUserJoin}
	 * @param userGroupProjectJoins a collection of {@link UserGroupProjectJoin}
	 * @return the max {@link ProjectRole}
	 */
	public static ProjectRole getMaxRoleForProjectsAndGroups(ProjectUserJoin projectUserJoin,
			Collection<UserGroupProjectJoin> userGroupProjectJoins) {
		ProjectRole projectRole = null;

		if (projectUserJoin != null) {
			projectRole = projectUserJoin.getProjectRole();
		}

		for (UserGroupProjectJoin userGroupProjectJoin : userGroupProjectJoins) {
			if (projectRole == null || projectRole.getUserRoleLevel() < userGroupProjectJoin.getProjectRole()
					.getUserRoleLevel()) {
				projectRole = userGroupProjectJoin.getProjectRole();
				if (projectRole.equals(PROJECT_OWNER)) {
					break;
				}
			}
		}

		return projectRole;
	}
}

