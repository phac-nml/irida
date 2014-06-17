package ca.corefacility.bioinformatics.irida.pipeline.upload;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;

/**
 * Defines search methods for specific types of information within an execution manager.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 * @param <T>  The type of object to search for.
 * @param <ID>  The ID of the object to search for.
 */
public interface ExecutionManagerSearch<T,ID> {
	
	/**
	 * Finds the specific type of object by it's ID.
	 * @param id  The ID to search for.
	 * @return  A class for this specific type of object.
	 * @throws ExecutionManagerObjectNotFoundException  If the specific object could not be found.
	 */
	public T findById(ID id) throws ExecutionManagerObjectNotFoundException;
	
	/**
	 * Checks if the specific type of object exists.
	 * @param id  The id to search for.
	 * @return  True if this object exists, false otherwise.
	 */
	public boolean exists(ID id);
}
