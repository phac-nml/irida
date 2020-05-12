package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.util.Date;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import liquibase.util.file.FilenameUtils;

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

	@Column(name = "fast5_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private Fast5Type fast5Type;

	protected Fast5Object() {
	}

	public Fast5Object(SequenceFile file) {
		this.file = file;
		this.fast5Type = getFileType(file);
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

	public Fast5Type getFast5Type() {
		return fast5Type;
	}

	enum Fast5Type {
		SINGLE,
		ZIPPED,
		UNKNOWN
	}

	private Fast5Type getFileType(SequenceFile file) {
		String extension = FilenameUtils.getExtension(file.getFile()
				.toString());

		if (extension.equals("fast5")) {
			return Fast5Type.SINGLE;
		} else if (extension.equals("gz")) {
			return Fast5Type.ZIPPED;
		}
		return Fast5Type.UNKNOWN;
	}
}
