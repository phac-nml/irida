package ca.corefacility.bioinformatics.irida.security.permissions;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

/**
 * Custom permission evaluator to determine whether or not an authenticated user
 * has authorization to view or modify a resource.
 * 
 * Inspired by
 * http://blog.solidcraft.eu/2011/03/spring-security-by-example-securing.html
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public class IridaPermissionEvaluator implements PermissionEvaluator {

	private static final Logger logger = LoggerFactory.getLogger(IridaPermissionEvaluator.class);

	private Collection<Permission> permissions;
	private Map<String, Permission> namedPermissionMap;

	public IridaPermissionEvaluator(Collection<Permission> permissions) {
		this.permissions = permissions;
		this.namedPermissionMap = new HashMap<>();
	}

	@PostConstruct
	public void init() {
		for (Permission p : permissions) {
			logger.debug("Registering permission [" + p.getPermissionProvided() + "] with class ["
					+ p.getClass().getName() + "]");
			namedPermissionMap.put(p.getPermissionProvided(), p);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		if (!namedPermissionMap.containsKey(permission.toString())) {
			throw new UndefinedPermissionException("The permission [" + permission.toString()
					+ "] is not registered with " + getClass().getName() + ".");
		}

		Permission permissionEvaluator = namedPermissionMap.get(permission.toString());
		boolean allowed = permissionEvaluator.isAllowed(authentication, targetDomainObject);

		logger.trace("Permission request for access to [" + targetDomainObject + "] with permission [" + permission
				+ "] by [" + authentication + "]. Granted? [" + allowed + "]");

		return allowed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permission) {
		return false;
	}

	/**
	 * Generic interface to test whether or not an authenticated user is allowed
	 * to perform some action on a specific target domain object.
	 * 
	 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
	 */
	interface Permission {
		/**
		 * Is the authenticated user allowed to perform some action on the
		 * target domain object?
		 * 
		 * @param authentication
		 *            the authenticated user.
		 * @param targetDomainObject
		 *            the object the user is requesting to perform an action on.
		 * @return true if the action is allowed, false otherwise.
		 */
		public boolean isAllowed(Authentication authentication, Object targetDomainObject);

		/**
		 * Get the name of the permission that this class provides.
		 * 
		 * @return the name of the permission that this class provides.
		 */
		public String getPermissionProvided();
	}
}
