package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityExistsException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import java.util.List;
import javax.validation.ConstraintViolationException;

/**
 * All Service interfaces should extend this interface to inherit common methods
 * relating to creating, reading, updating and deleting objects from
 * persistence.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface CRUDService<IdentifierType extends Identifier, Type extends Comparable<Type>> {

    /**
     * Create a new object in the persistence store.
     *
     * @param object The object to persist.
     * @return The object as it was persisted in the database. May modify the
     * identifier of the object when returned.
     * @throws EntityExistsException If the object being persisted violates
     * uniqueness constraints in the database.
     * @throws ConstraintViolationException If the object being persisted cannot
     * be validated by validation rules associated with the object.
     */
    public Type create(Type object) throws EntityExistsException, ConstraintViolationException;

    /**
     * Read the object type by unique identifier.
     *
     * @param id The unique identifier for this object.
     * @return The object corresponding to the unique identifier.
     * @throws EntityNotFoundException If the identifier does not exist in the
     * database.
     */
    public Type read(IdentifierType id) throws EntityNotFoundException;

    /**
     * Update the specified object in the database. The object <b>must</b> have
     * a valid identifier prior to being passed to this method.
     *
     * @param object The object to update.
     * @return The object as it was persisted in the database. May modify the
     * identifier of the object when returned.
     * @throws IllegalArgumentException If the object being persisted violates
     * uniqueness constraints in the database.
     * @throws ConstraintViolationException If the object being persisted cannot
     * be validated by validation rules associated with the object.
     */
    public Type update(Type object) throws EntityExistsException, ConstraintViolationException;

    /**
     * Delete the object with the specified identifier from the database.
     *
     * @param id The identifier of the object to delete.
     * @throws EntityNotFoundException If no object with the specified
     * identifier exists in the database.
     */
    public void delete(IdentifierType id) throws EntityNotFoundException;

    /**
     * List all objects of
     * <code>Type</code> in the database.
     *
     * @return All objects of the specified <code>Type</code> in the database.
     */
    public List<Type> list();

    /**
     * List objects of
     * <code>Type</code> in the database, limited to some specific page.
     *
     * @param page the specific page to use.
     * @param size the size of the pages used to compute the number of pages.
     * @param sortProperty the property used to sort the collection.
     * @param order the order of the sort.
     * @return the list of users within the specified range.
     * @throws IllegalArgumentException If the <code>Type</code> has no public
     * property <code>sortProperty</code>.
     */
    public List<Type> list(int page, int size, String sortProperty, Order order);

    /**
     * List objects of
     * <code>Type</code> in the database, limited to some specific page, ordered
     * by calling the
     * <code>compareTo</code> method on the class.
     *
     * @param page the specific page to use.
     * @param size the size of the pages used to compute the number of pages.
     * @param order the order of the sort.
     * @return the list of users within the specified range.
     */
    public List<Type> list(int page, int size, Order order);

    /**
     * Check to see if an identifier for
     * <code>Type</code> exists in the database.
     *
     * @param id The identifier to check for.
     * @return <code>true</code> if the identifier exists, <code>false</code>
     * otherwise.
     */
    public Boolean exists(IdentifierType id);

    /**
     * How many entities of
     * <code>Type</code> exist in the database?
     *
     * @return the number of entities in the database.
     */
    public Integer count();
}
