package ca.corefacility.bioinformatics.irida.model.remote;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;

/**
 * Marks an object which can be synchronized from a {@link RemoteAPI}
 */
public interface RemoteSynchornizable {

	/**
	 * Get the status of a remote object
	 * 
	 * @return {@link RemoteStatus} for the object
	 */
	public RemoteStatus getRemoteStatus();

	/**
	 * Set the {@link RemoteStatus} of a remote object
	 * 
	 * @param remoteStatus
	 *            the {@link RemoteStatus} to set
	 */
	public void setRemoteStatus(RemoteStatus remoteStatus);
}
