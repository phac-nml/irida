package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;

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

	public SingleEndSequenceFile(SequenceFile file) {
		super();
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
	public Set<SequenceFile> getFiles() {
		return ImmutableSet.of(file);
	}

	@Override
	public void setFiles(Set<SequenceFile> files) {
		if (files.size() > 1) {
			throw new IllegalArgumentException("SingleEndSequenceFile can only store 1 SequenceFile");
		}

		file = files.iterator().next();

	}
	
	public SequenceFile getFile() {
		return file;
	}

}
