package ca.corefacility.bioinformatics.irida.security.permissions;

import org.springframework.security.core.Authentication;

public interface BasePermission<DomainObjectType> {

	/**
	 * Get the implementation-specific permission provided.
	 *
	 * @return the permission provided by the permission class.
	 */
	String getPermissionProvided();

	/**
	 * Is the authenticated user allowed to perform some action on the target
	 * domain object?
	 *
	 * @param authentication     the authenticated user.
	 * @param targetDomainObject the object the user is requesting to perform an action on.
	 * @return true if the action is allowed, false otherwise.
	 */
	boolean isAllowed(Authentication authentication, Object targetDomainObject);
}
