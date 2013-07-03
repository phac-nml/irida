package ca.corefacility.bioinformatics.irida.repositories;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.InvalidPropertyException;
import ca.corefacility.bioinformatics.irida.model.FieldMap;
import ca.corefacility.bioinformatics.irida.model.enums.Order;
import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * All Service interfaces should extend this interface to inherit common methods
 * relating to creating, reading, updating and deleting objects from
 * persistence.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface CRUDRepository<IdentifierType, Type> {

    /**
     * Create a new object in the persistence store.
     *
     * @param object The object to persist.
     * @return The object as it was persisted in the database. May modify the
     * identifier of the object when returned.
     * @throws IllegalArgumentException If the object to persist does not pass
     * validation, then an argument will be thrown with a reason for failure.
     */
    public Type create(Type object) throws IllegalArgumentException;

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
     * Read multiple objects by the given collection of identifiers
     * @param idents The unique identifiers of the objects to read
     * @return A collection of the requested objects
     */
    public Collection<Type> readMultiple(Collection<Identifier> idents);

    /**
     * Update the specified object in the database. The object <b>must</b> have
     * a valid identifier prior to being passed to this method.
     *
     * @param id The identifier of the object to update.
     * @param updatedFields A map of the properties of the object that were updated.
     * @return The object as it was persisted in the database. May modify the
     * identifier of the object when returned.
     * @throws IllegalArgumentException If the object to persist does not have a
     * valid identifier, or the object does not pass validation, then an
     * exception will be thrown with a reason for failure.
     */    
    public Type update(IdentifierType id, Map<String, Object> updatedFields) throws InvalidPropertyException;

    /**
     * Delete the object with the specified identifier from the database.
     *
     * @param id The identifier of the object to delete.
     * @throws EntityNotFoundException If the identifier does not exist in the
     * database.
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
     * @param sortProperty the property that should be used to sort the objects.
     * @param order the order of the sort.
     * @return the list of users within the specified range.
     * @throws IllegalArgumentException If the <code>Type</code> has no public
     * property <code>sortProperty</code>.
     */
    public List<Type> list(int page, int size, String sortProperty, Order order);

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

    /**
     * List objects of this type that have the given fields
     * @param fields The fields we want to select from the database
     * @return A Map<Identifier, Map<String,Object>> of object identifiers and key/value pairs of the selected fields
     */    
    public List<FieldMap> listMappedFields(List<String> fields);
    
    /**
     * List objects of this type that have the given fields in a paged fashion
     * @param fields The fields to select from the database
     * @param page The page to list
     * @param size The size of a page
     * @param sortProperty The property to sort the items on
     * @param order The order to sort in
     * @return A List<{@link FieldMap}> of the requested fields
     */    
    public List<FieldMap> listMappedFields(List<String> fields,int page, int size, String sortProperty, Order order);
}
