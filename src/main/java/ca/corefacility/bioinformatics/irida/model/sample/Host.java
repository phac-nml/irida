package ca.corefacility.bioinformatics.irida.model.sample;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.validators.groups.NCBISubmission;

/**
 * A class to store host-specific metadata associated with a {@link Sample}. See
 * <a href=
 * "https://submit.ncbi.nlm.nih.gov/biosample/template/?package=Pathogen.cl.1.0&action=definition"
 * >NCBI BioSample Pathogen Attributes</a> for more information.
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

	/**
	 * The natural (as opposed to laboratory) host to the organism from which
	 * the sample was obtained. Use the full taxonomic name, eg, "Homo sapiens".
	 */
	@NotNull(message = "{host.taxonomy.notnull}", groups = NCBISubmission.class)
	private String taxonomicName;

	/**
	 * Name of relevant disease, e.g. Salmonella gastroenteritis. Controlled
	 * vocabulary, http://bioportal.bioontology.org/ontologies/1009 or
	 * http://www.ncbi.nlm.nih.gov/mesh
	 */
	@NotNull(message = "{host.disease.notnull}", groups = NCBISubmission.class)
	private String disease;

	/**
	 * Additional information not included in other defined vocabulary fields
	 */
	private String description;

	/**
	 * Final outcome of disease, e.g., death, chronic disease, recovery
	 */
	private String diseaseOutcome;

	/**
	 * Stage of disease at the time of sampling
	 */
	private String diseaseStage;

	/**
	 * Information regarding health state of the individual sampled at the time
	 * of sampling
	 */
	private String healthState;

	/**
	 * Gender or physical sex of the host
	 */
	private Gender sex;

	/**
	 * a unique identifier by which each subject can be referred to,
	 * de-identified, e.g. #131
	 */
	@Size(min = 1, message = "{host.subject.id.too.small}")
	private String subjectId;

	/**
	 * Type of tissue the initial sample was taken from. Controlled vocabulary,
	 * http://bioportal.bioontology.org/ontologies/1005)
	 */
	private String tissueSampleId;

	@Min(value = 0, message = "{host.age.min.too.small}")
	private Integer age;

	// values taken from BioSample pathogen package 1.0:
	// https://submit.ncbi.nlm.nih.gov/biosample/template/?package=Pathogen.cl.1.0&action=definition
	public static enum Gender {
		MALE, FEMALE, NEUTER, HERMAPHRODITE, NOT_DETERMINED
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Gender getSex() {
		return sex;
	}

	public void setSex(Gender sex) {
		this.sex = sex;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDiseaseOutcome() {
		return diseaseOutcome;
	}

	public void setDiseaseOutcome(String diseaseOutcome) {
		this.diseaseOutcome = diseaseOutcome;
	}

	public String getDiseaseStage() {
		return diseaseStage;
	}

	public void setDiseaseStage(String diseaseStage) {
		this.diseaseStage = diseaseStage;
	}

	public String getHealthState() {
		return healthState;
	}

	public void setHealthState(String healthState) {
		this.healthState = healthState;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getTissueSampleId() {
		return tissueSampleId;
	}

	public void setTissueSampleId(String tissueSampleId) {
		this.tissueSampleId = tissueSampleId;
	}

	public String getTaxonomicName() {
		return taxonomicName;
	}

	public void setTaxonomicName(String taxonomicName) {
		this.taxonomicName = taxonomicName;
	}

	public String getDisease() {
		return disease;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
}
