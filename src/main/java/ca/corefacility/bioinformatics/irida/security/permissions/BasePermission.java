package ca.corefacility.bioinformatics.irida.security.permissions;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.Authentication;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.user.Role;

/**
 * Generic super-class for permission types to extend from.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 * @param <DomainObjectType>
 *            the type of domain object that this permission is evaluating.
 */
public abstract class BasePermission<DomainObjectType> {
	
	private static final Logger logger = LoggerFactory.getLogger(BasePermission.class);

	/**
	 * Get the implementation-specific permission provided.
	 * 
	 * @return the permission provided by the permission class.
	 */
	public abstract String getPermissionProvided();

	/**
	 * This method is called by {@link BasePermission} to evaluate the custom
	 * permissions provided by implementing classes.
	 * 
	 * @param authentication
	 *            the authenticated user.
	 * @param targetDomainObject
	 *            the object that the user is attempting to access.
	 * @return true if permitted, false otherwise.
	 */
	protected abstract boolean customPermissionAllowed(Authentication authentication,
			DomainObjectType targetDomainObject);

	/**
	 * The type of object to be loaded from the database.
	 */
	private Class<DomainObjectType> domainObjectType;
	
	/**
	 * The repository to load objects with.
	 */
	private CrudRepository<DomainObjectType, Long> repository;

	/**
	 * Constructor with handles on the type of repository and type of domain
	 * object.
	 * 
	 * @param domainObjectType
	 *            the domain object type managed by this permission.
	 * @param repositoryId
	 *            the identifier of the repository to load from the spring
	 *            application context.
	 */
	protected BasePermission(Class<DomainObjectType> domainObjectType, CrudRepository<DomainObjectType, Long> repository) {
		this.repository = repository;
		this.domainObjectType = domainObjectType;
	}

	/**
	 * Is the authenticated user allowed to perform some action on the target
	 * domain object?
	 * 
	 * @param authentication
	 *            the authenticated user.
	 * @param targetDomainObject
	 *            the object the user is requesting to perform an action on.
	 * @return true if the action is allowed, false otherwise.
	 */
	@SuppressWarnings("unchecked")
	public final boolean isAllowed(Authentication authentication, Object targetDomainObject) {
		// fast pass for administrators -- administrators are allowed to access
		// everything.
		if (authentication.getAuthorities().contains(Role.ROLE_ADMIN)) {
			return true;
		}

		// load the domain object (if necessary) so that the subclass can
		// evaluate access

		DomainObjectType domainObject;

		if (targetDomainObject instanceof Long) {
			logger.trace("Trying to find domain object by id [" + targetDomainObject + "]");
			domainObject = repository.findOne((Long) targetDomainObject);
			if (domainObject == null) {
				throw new EntityNotFoundException("Could not find entity with id [" + targetDomainObject + "]");
			}
		} else if (domainObjectType.isAssignableFrom(targetDomainObject.getClass())) {
			// reflection replacement for instanceof
			domainObject = (DomainObjectType) targetDomainObject;
		} else if (targetDomainObject instanceof Collection<?>) {
			Collection<?> domainObjects = (Collection<?>)targetDomainObject;
			
			boolean permitted = true;
			for (Object domainObjectObject : domainObjects) {
				if (domainObjectType.isAssignableFrom(domainObjectObject.getClass())) {
					DomainObjectType domainObject2 = (DomainObjectType)domainObjectObject;
					
					permitted &= customPermissionAllowed(authentication, domainObject2);
				} else {
					throw new IllegalArgumentException("Parameter to " + getClass().getName() + " is not a valid Collection, must be of type"
							+ "Collection<" + domainObjectType.getName() + ">.");
				}
			}

			return permitted;
		} else {
			throw new IllegalArgumentException("Parameter to " + getClass().getName() + " must be of type Long or "
					+ domainObjectType.getName() + ".");
		}
		
		// pass off any other logic to the implementing permission class.
		return customPermissionAllowed(authentication, domainObject);
	}
}
