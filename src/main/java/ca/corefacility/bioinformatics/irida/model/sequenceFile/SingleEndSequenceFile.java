package ca.corefacility.bioinformatics.irida.model.sequenceFile;

import java.nio.file.Path;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
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

import ca.corefacility.bioinformatics.irida.model.irida.IridaSingleEndSequenceFile;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
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
public class SingleEndSequenceFile extends SequencingObject implements IridaSingleEndSequenceFile {

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@NotNull
	private final SequenceFile file;

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
	public Set<SequenceFile> getFiles() {
		return ImmutableSet.of(file);
	}

	@JsonIgnore
	public SequenceFile getSequenceFile() {
		return file;
	}

	/**
	 * Proxying SequenceFile inner properties
	 */

	public Path getFile() {
		return file.getFile();
	}

	/**
	 * Add one optional property to the map of properties
	 * 
	 * @param key
	 *            The key of the property to add
	 * @param value
	 *            The value of the property to add
	 */
	@JsonAnySetter
	public void addOptionalProperty(String key, String value) {
		file.addOptionalProperty(key, value);
	}

	/**
	 * Get the Map of optional properties
	 * 
	 * @return A {@code Map<String,String>} of all the optional propertie
	 */
	@JsonAnyGetter
	public Map<String, String> getOptionalProperties() {
		return file.getOptionalProperties();
	}

	/**
	 * Get an individual optional property
	 * 
	 * @param key
	 *            The key of the property to read
	 * @return A String of the property's value
	 */
	public String getOptionalProperty(String key) {
		return file.getOptionalProperty(key);
	}

	/**
	 * Get the size of the file.
	 *
	 * @return The String representation of the file size
	 */
	@JsonIgnore
	public String getFileSize() {
		return file.getFileSize();
	}

	/**
	 * Set the Map of optional properties
	 * 
	 * @param optionalProperties
	 *            A {@code Map<String,String>} of all the optional properties
	 *            for this object
	 */
	public void setOptionalProperties(Map<String, String> optionalProperties) {
		file.setOptionalProperties(optionalProperties);
	}

	public String getFileName() {
		return file.getFileName().toString();
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

	@Override
	public void setFiles(Set<SequenceFile> files) {
		if (files.size() != 1) {
			throw new IllegalArgumentException("SingleEndSequenceFile can only store 1 file, found " + files.size());
		}
	}

}
