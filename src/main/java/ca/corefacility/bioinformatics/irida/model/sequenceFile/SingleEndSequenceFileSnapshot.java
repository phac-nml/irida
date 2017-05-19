package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.util.Date;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSnapshot;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Snapshot of a {@link SingleEndSequenceFile} read from a remote site
 */
@Entity
@Table(name = "remote_sequence_file_single")
@EntityListeners(AuditingEntityListener.class)
@Audited
@Deprecated
public class SingleEndSequenceFileSnapshot implements IridaSingleEndSequenceFile, IridaThing, IridaSnapshot {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private Date createdDate;

	@NotNull
	@Column(name = "remote_uri")
	private String remoteURI;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@NotNull
	private final SequenceFileSnapshot file;

	// Default constructor for hibernate
	@SuppressWarnings("unused")
	private SingleEndSequenceFileSnapshot() {
		file = null;
	}

	public SingleEndSequenceFileSnapshot(SingleEndSequenceFile base, SequenceFileSnapshot file) {
		this.file = file;
		remoteURI = base.getSelfHref();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return file.getLabel();
	}

	@JsonIgnore
	public SequenceFileSnapshot getSequenceFile() {
		return file;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SingleEndSequenceFileSnapshot) {
			SingleEndSequenceFileSnapshot sampleFile = (SingleEndSequenceFileSnapshot) other;
			return Objects.equals(file, sampleFile.file);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(file);
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getRemoteURI() {
		return remoteURI;
	}

}
