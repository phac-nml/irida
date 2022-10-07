package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.util.Date;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.util.IridaFiles;

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

	private static final Logger logger = LoggerFactory.getLogger(Fast5Object.class);

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@NotNull
	private SequenceFile file;

	@Column(name = "fast5_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private Fast5Type fast5Type;

	protected Fast5Object() {
		fast5Type = Fast5Type.UNKNOWN;
	}

	public Fast5Object(SequenceFile file) {
		this();
		this.file = file;
		this.fast5Type = setType(file);
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
	 * Throws {@link UnsupportedOperationException} because you should not be able to update a file.
	 */
	@JsonIgnore
	@Override
	public void setModifiedDate(Date modifiedDate) {
		throw new UnsupportedOperationException("Cannot update a sequence file");
	}

	@Override
	public String getLabel() {
		return file.getFileName().toString();
	}

	public SequenceFile getFile() {
		return file;
	}

	public Fast5Type getFast5Type() {
		return fast5Type;
	}

	public void setFast5Type(Fast5Type fast5Type) {
		this.fast5Type = fast5Type;
	}

	/**
	 * Type of file stored buy this fast5 object
	 */
	public enum Fast5Type {
		SINGLE,
		ZIPPED,
		UNKNOWN
	}

	/**
	 * Get the {@link Fast5Type} for this object
	 *
	 * @param file if the file
	 * @return type of fast5object (unknown, zipped, or single)
	 */
	private Fast5Type setType(SequenceFile file) {

		Fast5Object.Fast5Type type = Fast5Object.Fast5Type.UNKNOWN;

		try {
			String extension = FilenameUtils.getExtension(getFile().getFileName());

			// Checks if file is where it should be before it checks if it is gzipped
			if ((IridaFiles.fileExists(file.getFile()) && file.isGzipped()) || extension.equals("gz")) {
				type = Fast5Object.Fast5Type.ZIPPED;
			} else if (extension.equals("fast5")) {
				type = Fast5Object.Fast5Type.SINGLE;
			}
		} catch (Exception e) {
			logger.warn("Problem checking for zipped file.  Setting as UNKNOWN type", e);
		}

		return type;
	}
}
