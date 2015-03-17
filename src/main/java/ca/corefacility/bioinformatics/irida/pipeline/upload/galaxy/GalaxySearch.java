package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerObjectNotFoundException;
import ca.corefacility.bioinformatics.irida.pipeline.upload.ExecutionManagerSearch;

/**
 * Galaxy-specific implementations of ExecutionManagerSearch.
 *
 * @param <T>  The type of object to search for.
 * @param <ID>  The ID of the object to search for.
 */
public abstract class GalaxySearch<T,ID> implements ExecutionManagerSearch<T,ID> {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean exists(ID id) {
		try {
			return findById(id) != null;
		} catch (ExecutionManagerObjectNotFoundException e) {
			return false;
		}
	}
}