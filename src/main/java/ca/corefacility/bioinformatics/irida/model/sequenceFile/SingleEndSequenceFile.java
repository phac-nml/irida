package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * {@link SequencingObject} from a single ended sequence run. This class will
 * contain only one SequenceFile.
 */
@Entity
@Table(name = "sequence_file_single_end")
@EntityListeners(AuditingEntityListener.class)
@Audited
public class SingleEndSequenceFile extends SequencingObject {

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@NotNull
	private SequenceFile file;

	// Default constructor for hibernate
	@SuppressWarnings("unused")
	private SingleEndSequenceFile() {
		file = null;
	}

	public SingleEndSequenceFile(SequenceFile file) {
		this.file = file;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return file.getLabel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@JsonIgnore
	public Set<SequenceFile> getFiles() {
		return ImmutableSet.of(file);
	}

	public SequenceFile getSequenceFile() {
		return file;
	}
	
	public void setSequenceFile(SequenceFile file) {
		this.file = file;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SingleEndSequenceFile) {
			SingleEndSequenceFile sampleFile = (SingleEndSequenceFile) other;
			return Objects.equals(file, sampleFile.file);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(file);
	}

}
