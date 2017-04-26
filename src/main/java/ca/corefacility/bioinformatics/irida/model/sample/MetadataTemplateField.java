package ca.corefacility.bioinformatics.irida.model.sample;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Describes an individual field in a {@link MetadataTemplate}.
 */
@Entity
@Table(name = "metadata_field")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class MetadataTemplateField {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	private String label;

	@NotNull
	private String type;

	public MetadataTemplateField() {}

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
}
