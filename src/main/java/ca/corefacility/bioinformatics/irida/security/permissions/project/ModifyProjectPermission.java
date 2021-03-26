package ca.corefacility.bioinformatics.irida.security.permissions.project;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupJoin;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.UserGroupProjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserGroupJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.RepositoryBackedPermission;

/**
 * Superclass permission whether a user can modify project settings.  This superclass checks if a user has ownership of a project. This can be extended for specific settings.
 */
public abstract class ModifyProjectPermission extends RepositoryBackedPermission<Project,Long> {
	private static final Logger logger = LoggerFactory.getLogger(ModifyProjectPermission.class);

	private final UserRepository userRepository;
	private final ProjectUserJoinRepository pujRepository;
	private final UserGroupProjectJoinRepository ugpjRepository;
	private final UserGroupJoinRepository ugRepository;

	/**
	 * Construct an instance of {@link ModifyProjectPermission}.
	 *
	 * @param projectRepository the project repository.
	 * @param userRepository    the user repository.
	 * @param pujRepository     the project user join repository.
	 * @param ugpjRepository    The user group project join repository
	 * @param ugRepository      The user group join repository
	 */
	@Autowired
	public ModifyProjectPermission(final ProjectRepository projectRepository, final UserRepository userRepository,
			final ProjectUserJoinRepository pujRepository, final UserGroupProjectJoinRepository ugpjRepository,
			final UserGroupJoinRepository ugRepository) {
		super(Project.class, Long.class, projectRepository);
		this.userRepository = userRepository;
		this.pujRepository = pujRepository;
		this.ugpjRepository = ugpjRepository;
		this.ugRepository = ugRepository;
	}

	/**
	 * {@inheritDoc}
	 */

	public boolean customPermissionAllowed(Authentication authentication, Project p) {
		logger.trace("Testing permission for [" + authentication + "] can modify project [" + p + "]");

		// check if the user is a project owner for this project
		User u = userRepository.loadUserByUsername(authentication.getName());
		List<Join<Project, User>> projectUsers = pujRepository.getUsersForProjectByRole(p, ProjectRole.PROJECT_OWNER);

		for (Join<Project, User> projectUser : projectUsers) {
			if (projectUser.getObject().equals(u)) {
				logger.trace("Permission GRANTED for [" + authentication + "] on project [" + p + "]");
				// this user is an owner for the project.
				return true;
			}
		}

		// if we've made it this far, then that means that the user isn't
		// directly added to the project, so check if the user is in any groups
		// added to the project.
		final Collection<UserGroupProjectJoin> groups = ugpjRepository.findGroupsByProject(p);
		for (final UserGroupProjectJoin group : groups) {
			if (group.getProjectRole().equals(ProjectRole.PROJECT_OWNER)) {
				final Collection<UserGroupJoin> groupMembers = ugRepository.findUsersInGroup(group.getObject());
				final boolean inGroup = groupMembers.stream().anyMatch(j -> j.getSubject().equals(u));
				if (inGroup) {
					logger.trace("Permission GRANTED for [" + authentication + "] on project [" + p
							+ "] by group membership in [" + group.getLabel() + "]");
					return true;
				}
			} else {
				logger.trace("Group is not PROJECT_OWNER, checking next project.");
			}
		}

		logger.trace("Permission DENIED for [" + authentication + "] on project [" + p + "]");
		return false;
	}
}
