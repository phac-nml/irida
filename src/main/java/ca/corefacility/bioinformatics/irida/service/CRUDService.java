package ca.corefacility.bioinformatics.irida.service;

import java.util.List;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;

/**
 * All Service interfaces should extend this interface to inherit common methods
 * relating to creating, reading, updating and deleting objects from persistence.
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
@Validated
public interface CRUDService<IdentifierType, Type> {
    /**
     * Create a new object in the persistence store.
     * @param object The object to persist.
     * @return The object as it was persisted in the database. May modify the 
     * identifier of the object when returned.
     * @throws IllegalArgumentException If the object to persist does not pass
     * validation, then an argument will be thrown with a reason for failure.
     */
    public Type create(@Valid Type object) throws IllegalArgumentException;
    
    /**
     * Read the object type by unique identifier.
     * @param id The unique identifier for this object.
     * @return The object corresponding to the unique identifier.
     * @throws IllegalArgumentException If the identifier does not exist in the
     * database.
     */
    public Type read(IdentifierType id) throws IllegalArgumentException;

    /**
     * Update the specified object in the database. The object <b>must</b> have
     * a valid identifier prior to being passed to this method.
     * @param object The object to update.
     * @return The object as it was persisted in the database. May modify the
     * identifier of the object when returned.
     * @throws IllegalArgumentException If the object to persist does not have a
     * valid identifier, or the object does not pass validation, then an exception
     * will be thrown with a reason for failure.
     */
    public Type update(Type object) throws IllegalArgumentException;
    
    /**
     * Delete the object with the specified identifier from the database.
     * @param id The identifier of the object to delete.
     * @throws IllegalArgumentException If the identifier does not exist in the
     * database.
     */
    public void delete(IdentifierType id) throws IllegalArgumentException;
    
    /**
     * List all objects of <code>Type</code> in the database.
     * @return All objects of the specified <code>Type</code> in the database.
     */
    public List<Type> list();
    
    /**
     * Check to see if an identifier for <code>Type</code> exists in the database.
     * @param id The identifier to check for.
     * @return <code>true</code> if the identifier exists, <code>false</code>
     * otherwise.
     */
    public Boolean exists(IdentifierType id);
}
