package ca.corefacility.bioinformatics.irida.model.sample;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;

/**
 * A {@link MetadataTemplateField} with a special key "irida-static-*" to specify that it is statically added to a
 * {@link Sample} and not part of the sample metadata.  These {@link MetadataTemplateField}s can be used to add to a
 * {@link MetadataTemplate} but should not be part of a Sample metadata.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Audited
public class StaticMetadataTemplateField extends MetadataTemplateField {

	public static String STATIC_FIELD_PREFIX = "irida-static-";

	// developer-readable ID for the field
	@NotNull
	@Column(name = "static_id")
	private String staticId;

	public StaticMetadataTemplateField() {
		super();
	}

	public StaticMetadataTemplateField(String label, String type, String staticId) {
		super(label, type);
		this.staticId = staticId;
	}

	@Override
	public String getFieldKey() {
		return STATIC_FIELD_PREFIX + staticId;
	}

	public String getStaticId() {
		return staticId;
	}
}
