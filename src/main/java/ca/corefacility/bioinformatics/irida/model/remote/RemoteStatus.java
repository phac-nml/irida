package ca.corefacility.bioinformatics.irida.model.remote;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.user.User;

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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@Column(name = "remote_hash_code")
	private int remoteHashCode;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "read_by")
	private User readBy;

	@Column(name="last_update")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdate;

	@SuppressWarnings("unused")
	private RemoteStatus() {
	}

	public RemoteStatus(String url, RemoteAPI api) {
		syncStatus = SyncStatus.UNSYNCHRONIZED;
		this.url = url;
		this.api = api;
		lastUpdate = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public int getRemoteHashCode() {
		return remoteHashCode;
	}

	public void setRemoteHashCode(int remoteHashCode) {
		this.remoteHashCode = remoteHashCode;
	}

	public User getReadBy() {
		return readBy;
	}

	public void setReadBy(User readBy) {
		this.readBy = readBy;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * The status of the synchronized object
	 */
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
		UNAUTHORIZED, 
		/**
		 * An error occurred while synchronizing.
		 */
		ERROR
	}
}
