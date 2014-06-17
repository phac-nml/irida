package ca.corefacility.bioinformatics.irida.pipeline.upload;

import java.util.List;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;

/**
 * Searches through a data source within an execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <T> The type of object to return.
 * @param <ID> The ID of the objects.
 * @param <NAME> Then name of the objects.
 */
public interface DataSourceSearch<T,ID,NAME> extends ExecutionManagerSearch<T, ID> {

	/**
	 * Searchs for the object by name.
	 * @param name  The name of the object.
	 * @return  A List of objects.
	 * @throws ExecutionManagerObjectNotFoundException If the specific object could not be found.
	 */
	public List<T> findByName(NAME name) throws ExecutionManagerObjectNotFoundException;
	
	/**
	 * Checks if the specific object exists by the name.
	 * @param name  The name of the object.
	 * @return  True if this object exists, false otherwise.
	 */
	public boolean existsByName(NAME name);
}
