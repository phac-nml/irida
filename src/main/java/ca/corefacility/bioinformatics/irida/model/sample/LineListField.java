package ca.corefacility.bioinformatics.irida.model.sample;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Describes an individual field in a linelist.
 */
@Entity
@Table(name = "line_list_field")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class LineListField {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String label;

	private String type;

	public LineListField(String label, String type) {
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
}
