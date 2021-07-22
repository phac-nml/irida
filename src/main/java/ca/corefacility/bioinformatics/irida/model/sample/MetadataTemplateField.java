package ca.corefacility.bioinformatics.irida.model.sample;

import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * Describes an individual field in a {@link MetadataTemplate}.
 */
@Entity
@Table(name = "metadata_field")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class MetadataTemplateField {

	public static String DYNAMIC_FIELD_PREFIX = "irida-";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String label;

	@NotNull
	private String type;

	@OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
	private List<MetadataEntry> metadataEntries;

	public MetadataTemplateField() {
	}

	public MetadataTemplateField(String label, String type) {
		this.label = label;
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public String getType() {
		return type;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return label;
	}

	@Override
	public int hashCode() {
		return Objects.hash(label.toLowerCase(), type);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MetadataTemplateField) {
			MetadataTemplateField other = (MetadataTemplateField) obj;

			return Objects.equals(label.toLowerCase(), other.label.toLowerCase()) && Objects.equals(type, other.type);
		}
		return false;
	}

	/**
	 * Key for displaying the field in the UI
	 *
	 * @return the key of the field.
	 */
	public String getFieldKey() {
		return DYNAMIC_FIELD_PREFIX + id;
	}
}
