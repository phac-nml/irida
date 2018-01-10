package ca.corefacility.bioinformatics.irida.security.permissions.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.UserGroupProjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserGroupJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;

/**
 * Confirms that a given user is the owner of a project (allowing access to
 * remote projects).
 */
@Component
public class ProjectOwnerAllowRemotePermission extends ModifyProjectPermission {

	private static final String PERMISSION_PROVIDED = "isProjectOwnerAllowRemote";

	/**
	 * Construct an instance of {@link ProjectOwnerPermission}.
	 * 
	 * @param projectRepository
	 *            the project repository.
	 * @param userRepository
	 *            the user repository.
	 * @param pujRepository
	 *            the project user join repository.
	 * @param ugpjRepository
	 *            the user group project repository.
	 * @param ugRepository
	 *            the user group join repository.
	 */
	@Autowired
	public ProjectOwnerAllowRemotePermission(ProjectRepository projectRepository, UserRepository userRepository,
			ProjectUserJoinRepository pujRepository, UserGroupProjectJoinRepository ugpjRepository,
			UserGroupJoinRepository ugRepository) {
		super(projectRepository, userRepository, pujRepository, ugpjRepository, ugRepository);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}
}
