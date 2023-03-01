package ca.corefacility.bioinformatics.irida.security.permissions;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

/**
 * Custom permission evaluator to determine whether or not an authenticated user has authorization to view or modify a
 * resource. Inspired by http://blog.solidcraft.eu/2011/03/spring-security-by-example-securing.html
 */
public class IridaPermissionEvaluator implements PermissionEvaluator {

	private static final Logger logger = LoggerFactory.getLogger(IridaPermissionEvaluator.class);

	private Collection<BasePermission<?>> permissions;
	private Map<String, BasePermission<?>> namedPermissionMap;

	public IridaPermissionEvaluator(RepositoryBackedPermission<?, ?>... permissions) {
		this(Arrays.asList(permissions));
	}

	public IridaPermissionEvaluator(Collection<BasePermission<?>> permissions) {
		this.permissions = permissions;
		this.namedPermissionMap = new HashMap<>();
	}

	/**
	 * Initialize the permission evaluator
	 */
	@PostConstruct
	public void init() {
		for (BasePermission<?> p : permissions) {
			logger.trace("Registering permission [" + p.getPermissionProvided() + "] with class ["
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

		BasePermission<?> permissionEvaluator = namedPermissionMap.get(permission.toString());
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
}
