package ca.corefacility.bioinformatics.irida.security.permissions;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;

/**
 * Confirms that the authenticated user is allowed to read a project.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class ReadProjectPermission extends BasePermission<Project> {

	private static final Logger logger = LoggerFactory.getLogger(ReadProjectPermission.class);
	private static final String PERMISSION_PROVIDED = "canReadProject";

	/**
	 * Construct an instance of {@link ReadProjectPermission}.
	 */
	public ReadProjectPermission() {
		super(Project.class, "projectRepository");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean customPermissionAllowed(Authentication authentication, Project p) {
		logger.trace("Testing permission for [" + authentication + "] on project [" + p + "]");
		UserRepository userRepository = getApplicationContext().getBean(UserRepository.class);

		// if not an administrator, then we need to figure out if the
		// authenticated user is participating in the project.
		User u = userRepository.loadUserByUsername(authentication.getName());
		Collection<Join<Project, User>> projectUsers = userRepository.getUsersForProject(p);

		for (Join<Project, User> projectUser : projectUsers) {
			if (projectUser.getObject().equals(u)) {
				logger.trace("Permission GRANTED for [" + authentication + "] on project [" + p + "]");
				// this user is participating in the project.
				return true;
			}
		}
		
		logger.trace("Permission DENIED for [" + authentication + "] on project [" + p + "]");
		return false;
	}

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

}
