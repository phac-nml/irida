package ca.corefacility.bioinformatics.irida.security.permissions.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSynchronizable;
import ca.corefacility.bioinformatics.irida.repositories.ProjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectUserJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.UserGroupProjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.security.ProjectSynchronizationAuthenticationToken;

/**
 * Confirms that a given user is the owner of a project
 */
@Component
public class ProjectOwnerPermission extends ModifyProjectPermission {
	private static final Logger logger = LoggerFactory.getLogger(ProjectOwnerPermission.class);
	private static final String PERMISSION_PROVIDED = "isProjectOwner";

	/**
	 * Construct an instance of {@link ProjectOwnerPermission}.
	 *
	 * @param projectRepository the project repository.
	 * @param userRepository    the user repository.
	 * @param pujRepository     the project user join repository.
	 * @param ugpjRepository    The user group project join repository.
	 */
	@Autowired
	public ProjectOwnerPermission(final ProjectRepository projectRepository, final UserRepository userRepository,
			final ProjectUserJoinRepository pujRepository, final UserGroupProjectJoinRepository ugpjRepository) {
		super(projectRepository, userRepository, pujRepository, ugpjRepository);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean customPermissionAllowed(Authentication authentication, Project p) {
		logger.trace("Testing permission for [" + authentication + "] has manager permissions on project [" + p + "]");

		/*
		 * Check to ensure if project is remote that it's being updated in the
		 * right context
		 */
		if (!canUpdateRemoteObject(p, authentication)) {
			return false;
		}

		return super.customPermissionAllowed(authentication, p);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean adminAccessAllowed(Authentication authentication, Object targetDomainObject) {
		/*
		 * if the object is a remote object, don't allow admin updating
		 * permissions
		 */
		if (targetDomainObject instanceof RemoteSynchronizable
				&& ((RemoteSynchronizable) targetDomainObject).isRemote()) {
			return false;
		}

		return true;
	}

	@Override
	public String getPermissionProvided() {
		return PERMISSION_PROVIDED;
	}

	/**
	 * Check if the given object is a remote object, and if so if the authentication is a
	 * {@link ProjectSynchronizationAuthenticationToken} object
	 *
	 * @param object         the object to test
	 * @param authentication the authentication to test
	 * @return true if either the object is not remote, or if it is remote and the authentication is a
	 *         {@link ProjectSynchronizationAuthenticationToken}
	 */
	public boolean canUpdateRemoteObject(Object object, Authentication authentication) {
		if (object instanceof RemoteSynchronizable && ((RemoteSynchronizable) object).isRemote()) {
			/*
			 * if the object is remote and the authentication is a
			 * ProjectSynchronizationAuthenticationToken, everything's ok
			 */
			if (authentication instanceof ProjectSynchronizationAuthenticationToken) {
				logger.trace(
						"Object is remote and authentication is ProjectSynchronizationAuthenticationToken.  Access is approved");
				return true;
			} else {
				logger.trace("Access DENIED.  Object is remote but authentication is "
						+ authentication.getClass().getName());
				return false;
			}
		}

		/*
		 * If the object isn't remote, there's nothing to do here.
		 */
		logger.trace("Object is not remote. Access is approved.");
		return true;
	}
}
