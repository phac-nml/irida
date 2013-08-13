package ca.corefacility.bioinformatics.irida.security.permissions;

import java.util.Collection;

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
		UserRepository userRepository = getApplicationContext().getBean(UserRepository.class);

		// if not an administrator, then we need to figure out if the
		// authenticated user is participating in the project.
		User u = userRepository.getUserByUsername(authentication.getName());
		Collection<Join<Project, User>> projectUsers = userRepository.getUsersForProject(p);

		for (Join<Project, User> projectUser : projectUsers) {
			if (projectUser.getObject().equals(u)) {
				// this user is participating in the project.
				return true;
			}
		}

		return false;
	}

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

}
