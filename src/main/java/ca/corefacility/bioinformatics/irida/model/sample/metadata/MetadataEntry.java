package ca.corefacility.bioinformatics.irida.model.sample.metadata;

import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * Class for storing generic metadata for a {@link Sample}
 */
@Entity
@Audited
@Table(name = "metadata_entry")
@Inheritance(strategy = InheritanceType.JOINED)
public class MetadataEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	private String value;

	@NotNull
	private String type;

	public MetadataEntry() {
	}

	public MetadataEntry(String value, String type) {
		this.value = value;
		this.type = type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MetadataEntry) {
			MetadataEntry entry = (MetadataEntry) obj;
			return Objects.equals(entry.getValue(), value) && Objects.equals(entry.getType(), type);
		}
		return false;
	}
}
