package ca.corefacility.bioinformatics.irida.model.remote;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Marks an object which can be synchronized from a {@link RemoteAPI}
 */
public interface RemoteSynchronizable {

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

	/**
	 * Check if this entity was read from a remote api
	 * 
	 * @return true if the object was read from a remote api
	 */
	@JsonIgnore
	public default boolean isRemote() {
		RemoteStatus status = getRemoteStatus();

		if (status != null && status.getURL() != null) {
			return true;
		}
		return false;
	}
}
