package ca.corefacility.bioinformatics.irida.model.sample;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "qc_entry")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class QCEntry {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long id;

	@ManyToOne
	@JsonIgnore
	public Sample sample;

	public QCEntry() {
	}

	public QCEntry(Sample sample) {
		this.sample = sample;
	}

	public Long getId() {
		return id;
	}

	public abstract String getIcon();
}
