package ca.corefacility.bioinformatics.irida.model.snapshot;

import java.nio.file.Path;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import ca.corefacility.bioinformatics.irida.model.irida.IridaSequenceFile;

@Entity
@Table(name = "sequence_file_snapshot")
public class SequenceFileSnapshot implements IridaSequenceFile {
	@Column(name = "filePath", unique = true)
	@NotNull(message = "{sequencefile.file.notnull}")
	private Path file;

	// Key/value map of additional properties you could set on a sequence file.
	// This may contain optional sequencer specific properties.
	@ElementCollection(fetch = FetchType.EAGER)
	@MapKeyColumn(name = "property_key", nullable = false)
	@Column(name = "property_value", nullable = false)
	@CollectionTable(name = "snapshot_sequence_file_properties", joinColumns = @JoinColumn(name = "sequence_file_id"), uniqueConstraints = @UniqueConstraint(columnNames = {
			"sequence_file_id", "property_key" }, name = "UK_SNAPSHOT_SEQUENCE_FILE_PROPERTY_KEY"))
	private Map<String, String> optionalProperties;

	public SequenceFileSnapshot(Path file, Map<String, String> optionalProperties) {
		this.file = file;
		this.optionalProperties = optionalProperties;
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
