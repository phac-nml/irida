package ca.corefacility.bioinformatics.irida.service;

import java.io.Serializable;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityRevisionDeletedException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.Timestamped;

/**
 * All Service interfaces should extend this interface to inherit common methods
 * relating to creating, reading, updating and deleting objects from
 * persistence.
 *
 * @param <IdentifierType>
 *            Identifier for the object stored in this service
 * @param <Type>
 *            The object type being stored in this service
 */
@PreAuthorize("isAuthenticated()")
public interface CRUDService<IdentifierType extends Serializable, Type extends Timestamped> {

	/**
	 * Create a new object in the persistence store.
	 *
	 * @param object
	 *            The object to persist.
	 * @return The object as it was persisted in the database. May modify the
	 *         identifier of the object when returned.
	 * @throws EntityExistsException
	 *             If the object being persisted violates uniqueness constraints
	 *             in the database.
	 * @throws ConstraintViolationException
	 *             If the object being persisted cannot be validated by
	 *             validation rules associated with the object.
	 */
	@PreAuthorize("hasRole('ROLE_USER')")
	public Type create(@Valid Type object) throws EntityExistsException, ConstraintViolationException;

	/**
	 * Read the object type by unique identifier.
	 *
	 * @param id
	 *            The unique identifier for this object.
	 * @return The object corresponding to the unique identifier.
	 * @throws EntityNotFoundException
	 *             If the identifier does not exist in the database.
	 */
	@PreAuthorize("hasRole('ROLE_USER')")
	public Type read(IdentifierType id) throws EntityNotFoundException;

	/**
	 * Read multiple objects by the given collection of identifiers
	 *
	 * @param idents
	 *            The unique identifiers of the objects to read
	 * @return A collection of the requested objects
	 */
	@PreAuthorize("hasRole('ROLE_USER')")
	public Iterable<Type> readMultiple(Iterable<IdentifierType> idents);

	/**
	 * Update the specified object in the database. The object <b>must</b> have
	 * a valid identifier prior to being passed to this method.
	 *
	 * @param id
	 *            The identifier of the object to update.
	 * @param updatedProperties
	 *            the object properties that should be updated.
	 * @return The object as it was persisted in the database. May modify the
	 *         identifier of the object when returned.
	 * @throws EntityExistsException
	 *             If the object being persisted violates uniqueness constraints
	 *             in the database.
	 * @throws EntityNotFoundException
	 *             If no object with the supplied identifier exists in the
	 *             database.
	 * @throws ConstraintViolationException
	 *             If the object being persisted cannot be validated by
	 *             validation rules associated with the object.
	 * @throws InvalidPropertyException
	 *             If the updated properties map contains a property name that
	 *             does not exist on the domain model.
	 */
	@PreAuthorize("hasRole('ROLE_USER')")
	public Type update(IdentifierType id, Map<String, Object> updatedProperties) throws EntityExistsException,
			EntityNotFoundException, ConstraintViolationException, InvalidPropertyException;

	/**
	 * Delete the object with the specified identifier from the database.
	 *
	 * @param id
	 *            The identifier of the object to delete.
	 * @throws EntityNotFoundException
	 *             If no object with the specified identifier exists in the
	 *             database.
	 */
	@PreAuthorize("hasRole('ROLE_USER')")
	public void delete(IdentifierType id) throws EntityNotFoundException;

	/**
	 * List all objects of <code>Type</code> in the database.
	 *
	 * @return All objects of the specified <code>Type</code> in the database.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Iterable<Type> findAll();

	/**
	 * List objects of <code>Type</code> in the database, limited to some
	 * specific page.
	 *
	 * @param page
	 *            the specific page to use.
	 * @param size
	 *            the size of the pages used to compute the number of pages.
	 * @param sortProperty
	 *            the properties used to sort the collection.
	 * @param order
	 *            the order of the sort.
	 * @return the list of users within the specified range.
	 * @throws IllegalArgumentException
	 *             If the <code>Type</code> has no public property
	 *             <code>sortProperty</code>.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Page<Type> list(int page, int size, Direction order, String... sortProperty) throws IllegalArgumentException;

	/**
	 * List objects of <code>Type</code> in the database, limited to some
	 * specific page, ordered by calling the <code>compareTo</code> method on
	 * the class.
	 *
	 * @param page
	 *            the specific page to use.
	 * @param size
	 *            the size of the pages used to compute the number of pages.
	 * @param order
	 *            the order of the sort.
	 * @return the list of users within the specified range.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Page<Type> list(int page, int size, Direction order);

	/**
	 * Check to see if an identifier for <code>Type</code> exists in the
	 * database.
	 *
	 * @param id
	 *            The identifier to check for.
	 * @return <code>true</code> if the identifier exists, <code>false</code>
	 *         otherwise.
	 */
	@PreAuthorize("hasRole('ROLE_USER')")
	public Boolean exists(IdentifierType id);

	/**
	 * How many entities of <code>Type</code> exist in the database?
	 * 
	 * @return the number of entities in the database.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public long count();

	/**
	 * Search for an entity of <code>Type</code> with a given specification
	 * 
	 * @param specification
	 *            The search specification
	 * @param page
	 *            The page number
	 * @param size
	 *            the size of the page
	 * @param order
	 *            the order of the page sort
	 * @param sortProperties
	 *            The properties to sort on
	 * @return a Page of Type
	 */
	public Page<Type> search(Specification<Type> specification, int page, int size, Direction order,
			String... sortProperties);

	/**
	 * Find all of the revisions for the specified identifier.
	 * 
	 * @param id
	 *            the identifier to find revisions for.
	 * @return the collection of revisions for the identifier.
	 * @throws EntityRevisionDeletedException
	 *             if the resource corresponding to the identifier was
	 *             previously deleted.
	 */
	@PreAuthorize("hasRole('ROLE_USER')")
	public Revisions<Integer, Type> findRevisions(IdentifierType id) throws EntityRevisionDeletedException;

	/**
	 * Returns a {@link Page} of revisions for the entity with the given id.
	 * 
	 * @param id
	 *            the identifier to find revisions for.
	 * @param pageable
	 *            the page specification.
	 * @return the page of revisions for the specified resource.
	 * @throws EntityRevisionDeletedException
	 *             if the resource corresponding to the identifier was
	 *             previously deleted.
	 */
	public Page<Revision<Integer, Type>> findRevisions(IdentifierType id, Pageable pageable)
			throws EntityRevisionDeletedException;
}
