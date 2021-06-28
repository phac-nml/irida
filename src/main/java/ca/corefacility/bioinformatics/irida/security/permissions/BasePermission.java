package ca.corefacility.bioinformatics.irida.security.permissions;

import org.springframework.security.core.Authentication;

/**
 * Root interface for any permissions being built within IRIDA.  This interface defines the methods required for evaluating a permission in {@link IridaPermissionEvaluator}.
 *
 * @param <DomainObjectType> The class to be evaluated by this permission
 * @see IridaPermissionEvaluator
 */
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
