package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
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
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaThing;

@Entity
@Table(name = "sequence_file_pair")
@EntityListeners(AuditingEntityListener.class)
@Audited
public class SequenceFilePair implements IridaThing {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@CreatedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@OneToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER, orphanRemoval = true)
	@Size(min = 2, max = 2)
	@CollectionTable(name = "sequence_file_pair_files", joinColumns = @JoinColumn(name = "pair_id"), uniqueConstraints = @UniqueConstraint(columnNames = { "files_id" }, name = "UK_SEQUENCE_FILE_PAIR"))
	private Set<SequenceFile> files;

	public SequenceFilePair() {
		createdDate = new Date();
		files = new HashSet<>();
	}

	public SequenceFilePair(SequenceFile file1, SequenceFile file2) {
		this();
		files.add(file1);
		files.add(file2);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	@Override
	public Date getModifiedDate() {
		return createdDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		throw new UnsupportedOperationException("Cannot update a sequence file pair");
	}

	@Override
	public String getLabel() {
		return toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("SequenceFilePair: ");
		Iterator<SequenceFile> iterator = files.iterator();
		builder.append(iterator.next().getLabel()).append(", ").append(iterator.next().getLabel());
		return builder.toString();
	}

	public Set<SequenceFile> getFiles() {
		return files;
	}

	public void setFiles(Set<SequenceFile> files) {
		this.files = files;
	}
}
