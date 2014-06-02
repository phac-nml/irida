package ca.corefacility.bioinformatics.irida.model.sample;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.biojava3.core.sequence.TaxonomyID;
import org.hibernate.envers.Audited;

/**
 * A class to store host-specific metadata associated with a {@link Sample}.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 *
 */
@Entity
@Table(name = "host")
@Audited
public class Host {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "hostTaxonomyId")),
			@AttributeOverride(name = "dataSource", column = @Column(name = "hostTaxonomyDataSource")) })
	@NotNull(message = "{host.taxonomy.notnull}")
	private TaxonomyID hostTaxonomy;

	private Gender gender;

	@Min(value = 0, message = "{host.age.min.too.small}")
	private int hostAgeMin;

	@Min(value = 0, message = "{host.age.max.too.small}")
	private int hostAgeMax;

	public static enum Gender {
		MALE, FEMALE
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public TaxonomyID getHostTaxonomy() {
		return hostTaxonomy;
	}

	public void setHostTaxonomy(TaxonomyID hostTaxonomy) {
		this.hostTaxonomy = hostTaxonomy;
	}

	public int getHostAgeMin() {
		return hostAgeMin;
	}

	public void setHostAgeMin(int hostAgeMin) {
		this.hostAgeMin = hostAgeMin;
	}

	public int getHostAgeMax() {
		return hostAgeMax;
	}

	public void setHostAgeMax(int hostAgeMax) {
		this.hostAgeMax = hostAgeMax;
	}
}
