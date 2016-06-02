package ca.corefacility.bioinformatics.irida.model.remote;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;

/**
 * Information about an entity that was copied from a remote api. Entities using
 * this class should implement {@link RemoteSynchronizable}
 * 
 * @see RemoteSynchronizable
 */
@Entity

// TODO: Add when done testing uniqueConstraints = @UniqueConstraint(columnNames
// = "url")
@Table(name = "remote_status")
@Audited
public class RemoteStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@NotNull
	@Column(name = "url")
	private String url;

	@NotNull
	@ManyToOne(fetch = FetchType.EAGER)
	private RemoteAPI api;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "sync_status")
	private SyncStatus syncStatus;

	@SuppressWarnings("unused")
	private RemoteStatus() {
	}

	public RemoteStatus(String url, RemoteAPI api) {
		syncStatus = SyncStatus.UNSYNCHRONIZED;
		this.url = url;
		this.api = api;
	}

	public String getURL() {
		return url;
	}

	public void setURL(String url) {
		this.url = url;
	}

	public void setSyncStatus(SyncStatus syncStatus) {
		this.syncStatus = syncStatus;
	}

	public SyncStatus getSyncStatus() {
		return syncStatus;
	}

	public RemoteAPI getApi() {
		return api;
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
		SYNCHRONIZED,
		/**
		 * No active OAuth2 token to synchronize this project
		 */
		UNAUTHORIZED
	}
}
