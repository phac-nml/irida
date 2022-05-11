package ca.corefacility.bioinformatics.irida.security.permissions.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.UserGroupProjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

/**
 * Permission checking if a user can update local project settings
 */
@Component
public class ManageLocalProjectSettingsPermission extends ModifyProjectPermission {
	
	private static final String PERMISSION_PROVIDED = "canManageLocalProjectSettings";

	/**
	 * Construct an instance of {@link ManageLocalProjectSettingsPermission}.
	 *
	 * @param projectRepository the project repository.
	 * @param userRepository    the user repository.
	 * @param pujRepository     the project user join repository.
	 * @param ugpjRepository    the user group project join repository
	 */
	@Autowired
	public ManageLocalProjectSettingsPermission(final ProjectRepository projectRepository,
			final UserRepository userRepository, final ProjectUserJoinRepository pujRepository,
			final UserGroupProjectJoinRepository ugpjRepository) {
		super(projectRepository, userRepository, pujRepository, ugpjRepository);
	}

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}
}
