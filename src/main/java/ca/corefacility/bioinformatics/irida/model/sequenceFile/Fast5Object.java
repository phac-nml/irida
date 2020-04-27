package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.util.Date;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;

/**
 * {@link SequencingObject} implementation for storing .fast5 files.
 */
@Entity
@Table(name = "sequence_file_fast5")
@EntityListeners(AuditingEntityListener.class)
@Audited
public class Fast5Object extends SequencingObject {

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@NotNull
	private SequenceFile file;

	protected Fast5Object() {
	}

	public Fast5Object(SequenceFile file) {
		this.file = file;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@JsonIgnore
	public Set<SequenceFile> getFiles() {
		return ImmutableSet.of(file);
	}

	/**
	 * Throws {@link UnsupportedOperationException} because you should not be
	 * able to update a file.
	 */
	@JsonIgnore
	@Override
	public void setModifiedDate(Date modifiedDate) {
		throw new UnsupportedOperationException("Cannot update a sequence file");
	}

	@Override
	public String getLabel() {
		return file.getFileName()
				.toString();
	}

	public SequenceFile getFile() {
		return file;
	}
}
