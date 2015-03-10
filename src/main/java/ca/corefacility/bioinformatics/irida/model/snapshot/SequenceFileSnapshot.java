package ca.corefacility.bioinformatics.irida.model.snapshot;

import java.nio.file.Path;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;

/**
 * Snapshot taken of an {@link IridaSequenceFile} object
 * 
 *
 */
@Entity
@Table(name = "sequence_file_snapshot")
@Inheritance(strategy = InheritanceType.JOINED)
public class SequenceFileSnapshot implements IridaSequenceFile {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "snapshot_id")
	private Long snapshotId;

	@Column(name = "id")
	private Long id;

	@Column(name = "file_path")
	@NotNull(message = "{sequencefile.file.notnull}")
	private Path file;

	// Key/value map of additional properties you could set on a sequence file.
	// This may contain optional sequencer specific properties.
	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "property_key", nullable = false)
	@Column(name = "property_value", nullable = false)
	@CollectionTable(name = "snapshot_sequence_file_properties", joinColumns = @JoinColumn(name = "sequence_file_snapshot_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"sequence_file_snapshot_id", "property_key" }, name = "UK_SNAPSHOT_SEQUENCE_FILE_PROPERTY_KEY"))
	private Map<String, String> optionalProperties;

	/**
	 * Construct a {@link SequenceFileSnapshot} from the given
	 * {@link IridaSequenceFile}
	 * 
	 * @param sequenceFile
	 *            The {@link IridaSequenceFile} to clone
	 */
	public SequenceFileSnapshot(IridaSequenceFile sequenceFile) {
		this.id = sequenceFile.getId();
		this.file = sequenceFile.getFile();
		this.optionalProperties = sequenceFile.getOptionalProperties();
	}

	/**
	 * Construct a {@link SequenceFileSnapshot} from the given
	 * {@link IridaSequenceFile} and given file path
	 * 
	 * @param sequenceFile
	 *            The {@link IridaSequenceFile} to clone
	 * @param file
	 *            The filesystem location of the sequence file
	 */
	public SequenceFileSnapshot(IridaSequenceFile sequenceFile, Path file) {
		this.id = sequenceFile.getId();
		this.file = file;
		this.optionalProperties = sequenceFile.getOptionalProperties();
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Path getFile() {
		return file;
	}

	@Override
	public Map<String, String> getOptionalProperties() {
		return optionalProperties;
	}

}
