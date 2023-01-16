package ca.corefacility.bioinformatics.irida.security.permissions.project;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.UserGroupProjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.RepositoryBackedPermission;

/**
 * Superclass permission whether a user can modify project settings. This superclass checks if a user has ownership of a
 * project. This can be extended for specific settings.
 */
public abstract class ModifyProjectPermission extends RepositoryBackedPermission<Project, Long> {
	private static final Logger logger = LoggerFactory.getLogger(ModifyProjectPermission.class);

	private final UserRepository userRepository;
	private final ProjectUserJoinRepository pujRepository;
	private final UserGroupProjectJoinRepository ugpjRepository;

	/**
	 * Construct an instance of {@link ModifyProjectPermission}.
	 *
	 * @param projectRepository the project repository.
	 * @param userRepository    the user repository.
	 * @param pujRepository     the project user join repository.
	 * @param ugpjRepository    The user group project join repository
	 */
	@Autowired
	public ModifyProjectPermission(final ProjectRepository projectRepository, final UserRepository userRepository,
			final ProjectUserJoinRepository pujRepository, final UserGroupProjectJoinRepository ugpjRepository) {
		super(Project.class, Long.class, projectRepository);
		this.userRepository = userRepository;
		this.pujRepository = pujRepository;
		this.ugpjRepository = ugpjRepository;
	}

	/**
	 * {@inheritDoc}
	 */

	public boolean customPermissionAllowed(Authentication authentication, Project p) {
		logger.trace("Testing permission for [" + authentication + "] can modify project [" + p + "]");

		final User u = userRepository.loadUserByUsername(authentication.getName());

		// if not an administrator, then we need to figure out if the
		// authenticated user is an owner for this project.
		final ProjectUserJoin puj = pujRepository.getProjectJoinForUser(p, u);
		if (puj != null) {
			if (puj.getProjectRole().equals(ProjectRole.PROJECT_OWNER)) {
				logger.trace("Permission GRANTED for [" + authentication + "] on project [" + p + "]");
				// this user is an owner for the project.
				return true;
			}
		}

		// if we've made it this far, then that means that the user isn't
		// directly added to the project, so check if the user is in any groups
		// added to the project.
		final Collection<UserGroupProjectJoin> ugpjCollection = ugpjRepository.findGroupsForProjectAndUser(p, u);
		for (final UserGroupProjectJoin group : ugpjCollection) {
			if (group.getProjectRole().equals(ProjectRole.PROJECT_OWNER)) {
				logger.trace("Permission GRANTED for [" + authentication + "] on project [" + p
						+ "] by group membership in [" + group.getLabel() + "]");
				return true;
			}
		}

		logger.trace("Permission DENIED for [" + authentication + "] on project [" + p + "]");
		return false;
	}
}
