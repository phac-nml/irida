package ca.corefacility.bioinformatics.irida.service;

import java.util.List;
import javax.validation.ConstraintViolationException;

/**
 * All Service interfaces should extend this interface to inherit common methods
 * relating to creating, reading, updating and deleting objects from
 * persistence.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface CRUDService<IdentifierType, Type> {

    /**
     * Create a new object in the persistence store.
     *
     * @param object The object to persist.
     * @return The object as it was persisted in the database. May modify the
     * identifier of the object when returned.
     * @throws IllegalArgumentException If the object being persisted violates
     * uniqueness constraints in the database.
     * @throws ConstraintViolationException If the object being persisted cannot
     * be validated by validation rules associated with the object.
     */
    public Type create(Type object) throws IllegalArgumentException, ConstraintViolationException;

    /**
     * Read the object type by unique identifier.
     *
     * @param id The unique identifier for this object.
     * @return The object corresponding to the unique identifier.
     * @throws IllegalArgumentException If the identifier does not exist in the
     * database.
     */
    public Type read(IdentifierType id) throws IllegalArgumentException;

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
    public Type update(Type object) throws IllegalArgumentException, ConstraintViolationException;

    /**
     * Delete the object with the specified identifier from the database.
     *
     * @param id The identifier of the object to delete.
     * @throws IllegalArgumentException If no object with the specified
     * identifier exists in the database.
     */
    public void delete(IdentifierType id) throws IllegalArgumentException;

    /**
     * List all objects of
     * <code>Type</code> in the database.
     *
     * @return All objects of the specified <code>Type</code> in the database.
     */
    public List<Type> list();

    /**
     * Check to see if an identifier for
     * <code>Type</code> exists in the database.
     *
     * @param id The identifier to check for.
     * @return <code>true</code> if the identifier exists, <code>false</code>
     * otherwise.
     */
    public Boolean exists(IdentifierType id);
}
