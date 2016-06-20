package ca.corefacility.bioinformatics.irida.security.permissions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteSynchronizable;
import ca.corefacility.bioinformatics.irida.security.ProjectSynchronizationAuthenticationToken;

/**
 * Permission Class used to check if an object is a remote object, and if so if
 * the given authentication is a
 * {@link ProjectSynchronizationAuthenticationToken}
 */
public class RemoteUpdatePermission {

	private static final Logger logger = LoggerFactory.getLogger(RemoteUpdatePermission.class);

	/**
	 * Check if the given object is a remote object, and if so if the
	 * authentication is a {@link ProjectSynchronizationAuthenticationToken}
	 * object
	 * 
	 * @param object
	 *            the object to test
	 * @param authentication
	 *            the authentication to test
	 * @return true if either the object is not remote, or if it is remote and
	 *         the authentication is a
	 *         {@link ProjectSynchronizationAuthenticationToken}
	 */
	public static boolean canUpdateRemoteObject(Object object, Authentication authentication) {
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
