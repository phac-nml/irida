package ca.corefacility.bioinformatics.irida.security.permissions.project;

import java.util.Collection;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.UserGroupProjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.security.permissions.RepositoryBackedPermission;

/**
 * Confirms that the authenticated user is allowed to read a project.
 */
@Component
public class ReadProjectPermission extends RepositoryBackedPermission<Project, Long> {

	private static final Logger logger = LoggerFactory.getLogger(ReadProjectPermission.class);
	public static final String PERMISSION_PROVIDED = "canReadProject";

	private static final String ROLE_SEQUENCER = Role.ROLE_SEQUENCER.getAuthority();

	private final UserRepository userRepository;
	private final ProjectUserJoinRepository pujRepository;
	private final UserGroupProjectJoinRepository ugpjRepository;

	/**
	 * Construct an instance of {@link ReadProjectPermission}.
	 *
	 * @param projectRepository the project repository.
	 * @param userRepository    the user repository.
	 * @param pujRepository     the project user join repository.
	 * @param ugpjRepository    the user group/project join repository
	 */
	@Autowired
	public ReadProjectPermission(final ProjectRepository projectRepository, final UserRepository userRepository,
			final ProjectUserJoinRepository pujRepository, final UserGroupProjectJoinRepository ugpjRepository) {
		super(Project.class, Long.class, projectRepository);
		this.userRepository = userRepository;
		this.pujRepository = pujRepository;
		this.ugpjRepository = ugpjRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean customPermissionAllowed(final Authentication authentication, final Project p) {
		logger.trace("Testing permission for [" + authentication + "] on project [" + p + "]");

		if (authentication.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals(ROLE_SEQUENCER))) {
			logger.trace("Fast pass for sequencer role.");
			return true;
		}

		final User u = userRepository.loadUserByUsername(authentication.getName());

		// if not an administrator, then we need to figure out if the
		// authenticated user is participating in the project.
		final ProjectUserJoin puj = pujRepository.getProjectJoinForUser(p, u);
		if (puj != null) {
			logger.trace("Permission GRANTED for [" + authentication + "] on project [" + p + "]");
			// this user is participating in the project.
			return true;
		}

		// if we've made it this far, then that means that the user isn't
		// directly added to the project, so check if the user is in any groups
		// added to the project.
		final Collection<UserGroupProjectJoin> ugpjCollection = ugpjRepository.findGroupsForProjectAndUser(p, u);
		if (!ugpjCollection.isEmpty()){
			// get the first group listed for trace logging
			UserGroupProjectJoin group = ugpjCollection.iterator().next();
			logger.trace("Permission GRANTED for [" + authentication + "] on project [" + p
					+ "] by group membership in [" + group.getLabel() + "]");
			return true;
		}

		logger.trace("Permission DENIED for [" + authentication + "] on project [" + p + "]");
		return false;
	}

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

}
