package ca.corefacility.bioinformatics.irida.model.remote;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Information about an entity that was copied from a remote api
 */
@Entity
public class RemoteStatus {

	@Id
	private String remoteURL;

	@NotNull
	@Enumerated(EnumType.STRING)
	private SyncStatus syncStatus;

	public RemoteStatus(String remoteURL) {
		syncStatus = SyncStatus.UNSYNCHRONIZED;
		this.remoteURL = remoteURL;
	}

	public String getRemoteURL() {
		return remoteURL;
	}

	public void setRemoteURL(String remoteURL) {
		this.remoteURL = remoteURL;
	}

	public void setSyncStatus(SyncStatus syncStatus) {
		this.syncStatus = syncStatus;
	}

	public SyncStatus getSyncStatus() {
		return syncStatus;
	}

	public enum SyncStatus {
		/**
		 * Entity should not be synchronized
		 */
		UNSYNCHRONIZED,
		/**
		 * Marked to be synchronized
		 */
		MARKED,
		/**
		 * Currently being copied
		 */
		UPDATING,
		/**
		 * Up to date
		 */
		SYNCHRONIZED
	}
}
