package ca.corefacility.bioinformatics.irida.model.irida;

import ca.corefacility.bioinformatics.irida.repositories.remote.RemoteRepository;

/**
 * Interface describing objects which have been saved locally from a
 * {@link RemoteRepository}
 */
public interface IridaSnapshot {
	/**
	 * Get the remote URI which this object was originally read
	 * 
	 * @return a String URI
	 */
	public String getRemoteURI();
}
