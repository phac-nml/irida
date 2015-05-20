package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFilePair;

/**
 * Remote representation of a {@link IridaSequenceFilePair}. Refers to 2
 * {@link SequenceFileSnapshot}s and a URI for the remote resource.
 */
@Entity
@Table(name = "remote_sequence_file_pair")
@EntityListeners(AuditingEntityListener.class)
@Audited
public class SequenceFilePairSnapshot implements IridaSequenceFilePair, IridaThing {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private Date createdDate;

	@OneToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER, orphanRemoval = true)
	@Size(min = 2, max = 2)
	@CollectionTable(name = "remote_sequence_file_pair_files", joinColumns = @JoinColumn(name = "pair_id"), uniqueConstraints = @UniqueConstraint(columnNames = { "files_id" }, name = "UK_REMOTE_SEQUENCE_FILE_PAIR"))
	private Set<SequenceFileSnapshot> files;

	/**
	 * Construct a new {@link SequenceFilePairSnapshot} for two
	 * {@link SequenceFileSnapshot}s.
	 * 
	 * @param file1
	 *            a file in the relationship
	 * @param file2
	 *            another file in the relationship
	 */
	public SequenceFilePairSnapshot(SequenceFileSnapshot file1, SequenceFileSnapshot file2) {
		createdDate = new Date();
		files = new HashSet<>(2);

		files.add(file1);
		files.add(file2);
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Set<SequenceFileSnapshot> getFiles() {
		return files;
	}

	@Override
	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public Date getModifiedDate() {
		return createdDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		throw new UnsupportedOperationException("cannot update a SequenceFilePair");
	}

	@Override
	public String getLabel() {
		return toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("RemoteSequenceFilePair: ");
		Iterator<SequenceFileSnapshot> iterator = files.iterator();
		builder.append(iterator.next().getLabel()).append(", ").append(iterator.next().getLabel());
		return builder.toString();
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

}
