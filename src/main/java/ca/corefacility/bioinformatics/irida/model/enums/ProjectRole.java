package ca.corefacility.bioinformatics.irida.model.enums;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;

import java.util.Collection;

/**
 *
 */
public enum ProjectRole {

    PROJECT_USER("PROJECT_USER"),
    PROJECT_OWNER("PROJECT_OWNER");

    private String code;

    private ProjectRole(String code) {
        this.code = code;
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


    /**
     * Compares and returns the highest level {@link ProjectRole} from a {@link ProjectUserJoin} and a collection of {@link UserGroupProjectJoin}s.
     *
     * @param projectUserJoin       a user's {@link ProjectUserJoin}
     * @param userGroupProjectJoins a collection of {@link UserGroupProjectJoin}
     * @return the max {@link ProjectRole}
     */
    public static ProjectRole getMaxRoleForProjectsAndGroups(ProjectUserJoin projectUserJoin, Collection<UserGroupProjectJoin> userGroupProjectJoins) {
        ProjectRole projectRole = null;

        if (projectUserJoin != null) {
            projectRole = projectUserJoin.getProjectRole();
        }

        for (UserGroupProjectJoin userGroupProjectJoin : userGroupProjectJoins) {
            if (projectRole.toString() == "PROJECT_USER" && userGroupProjectJoin.getProjectRole()
                    .toString() == "PROJECT_OWNER") {
                projectRole = userGroupProjectJoin.getProjectRole();
            }
        }

        return projectRole;
    }
}

