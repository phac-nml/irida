package ca.corefacility.bioinformatics.irida.security.permissions;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;

import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;

/**
 * Generic super-class for permission types to extend from.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 * @param <DomainObjectType>
 *            the type of domain object that this permission is evaluating.
 */
public abstract class BasePermission<DomainObjectType> implements ApplicationContextAware {

	/**
	 * Handle on the {@link ApplicationContext} for the application.
	 */
	private ApplicationContext applicationContext;

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
	 * the name of the repository to be used, as defined in spring
	 * configuration.
	 */
	private String repositoryId;

	/**
	 * The type of object to be loaded from the database.
	 */
	private Class<DomainObjectType> domainObjectType;

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
	protected BasePermission(Class<DomainObjectType> domainObjectType, String repositoryId) {
		this.repositoryId = repositoryId;
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
		CRUDRepository<Long, DomainObjectType> crudRepository = (CRUDRepository<Long, DomainObjectType>) applicationContext
				.getBean(repositoryId);
		DomainObjectType domainObject;

		if (targetDomainObject instanceof Long) {
			domainObject = crudRepository.read((Long) targetDomainObject);
		} else if (domainObjectType.isAssignableFrom(targetDomainObject.getClass())) {
			// reflection replacement for instanceof
			domainObject = (DomainObjectType) targetDomainObject;
		} else {
			throw new IllegalArgumentException("Parameter to " + getClass().getName() + " must be of type Long or "
					+ domainObjectType.getName() + ".");
		}

		// pass off any other logic to the implementing permission class.
		return customPermissionAllowed(authentication, domainObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * Get the {@link ApplicationContext} injected into the base permission.
	 * 
	 * @return instance of {@link ApplicationContext} for the application.
	 */
	protected ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}
}
