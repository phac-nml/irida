package ca.corefacility.bioinformatics.irida.model.sample;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class StaticMetadataTemplateField extends MetadataTemplateField {

	public static String STATIC_FIELD_PREFIX = "irida-static-";

	@NotNull
	@Column(name="static_id")
	private String staticId;

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
