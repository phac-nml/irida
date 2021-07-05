package ca.corefacility.bioinformatics.irida.model.sample.metadata;

import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class for storing generic metadata for a {@link Sample}
 */
@Entity
@Audited
@Table(name = "metadata_entry")
@Inheritance(strategy = InheritanceType.JOINED)
public class MetadataEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Lob
	private String value;

	@NotNull
	private String type;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "field_id", nullable = false)
	private MetadataTemplateField field;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	@JoinColumn(name = "sample_id", nullable = false)
	private Sample sample;

	public MetadataEntry() {
	}

	public MetadataEntry(String value, String type) {
		this.value = value;
		this.type = type;
	}

	public MetadataEntry(String value, String type, MetadataTemplateField field) {
		this(value, type);
		this.field = field;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	public Long getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	/**
	 * Merges the passed metadata entry into this metadata entry.
	 *
	 * @param metadataEntry The new metadata entry.
	 */
	public void merge(MetadataEntry metadataEntry) {
		checkNotNull(metadataEntry, "metadataEntry is null");
		checkArgument(this.getClass()
				.equals(metadataEntry.getClass()), "Cannot merge " + metadataEntry + " into " + this);

		this.type = metadataEntry.getType();
		this.value = metadataEntry.getValue();
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, type, field);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MetadataEntry) {
			MetadataEntry entry = (MetadataEntry) obj;
			return Objects.equals(entry.getValue(), value) && Objects.equals(entry.getType(), type);
		}
		return false;
	}

	@JsonIgnore
	public MetadataTemplateField getField() {
		return field;
	}

	@JsonIgnore
	public void setField(MetadataTemplateField field) {
		this.field = field;
	}

	@JsonIgnore
	public void setSample(Sample sample) {
		this.sample = sample;
	}

	@JsonIgnore
	public Sample getSample() {
		return sample;
	}
}
