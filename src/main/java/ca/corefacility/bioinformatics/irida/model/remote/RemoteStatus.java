package ca.corefacility.bioinformatics.irida.model.remote;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

/**
 * Information about an entity that was copied from a remote api
 */
@Entity
@Table(name = "remote_status", uniqueConstraints = @UniqueConstraint(columnNames = "url"))
@Audited
public class RemoteStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@Column(name = "url")
	private String url;

	@NotNull
	@Enumerated(EnumType.STRING)
	private SyncStatus syncStatus;

	@SuppressWarnings("unused")
	private RemoteStatus() {
	}

	public RemoteStatus(String url) {
		syncStatus = SyncStatus.UNSYNCHRONIZED;
		this.url = url;
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
