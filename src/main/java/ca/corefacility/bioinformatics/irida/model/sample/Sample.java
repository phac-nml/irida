package ca.corefacility.bioinformatics.irida.model.sample;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.biojava3.core.sequence.TaxonomyID;
import org.hibernate.envers.Audited;

import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.validators.annotations.Latitude;
import ca.corefacility.bioinformatics.irida.validators.annotations.Longitude;
import ca.corefacility.bioinformatics.irida.validators.annotations.ValidSampleName;

/**
 * A biological sample. Each sample may correspond to many files.
 * 
 * A {@link Sample} comprises of many attributes. The attributes assigned to a
 * {@link Sample} correspond to the NCBI Pathogen BioSample attributes. See <a
 * href=
 * "https://submit.ncbi.nlm.nih.gov/biosample/template/?package=Pathogen.cl.1.0&action=definition"
 * >BioSample Attributes: Package Pathogen</a> for more information.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
@Entity
@Table(name = "sample")
@Audited
public class Sample implements IridaThing, Comparable<Sample> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * Note: The unique constraint makes sense programmatically, however it does
	 * not make sense to have a unique constraint for an external identifier
	 * from the perspective of a user; especially since the external identifier
	 * is provided entirely externally from the system.
	 */
	@NotNull(message = "{sample.external.id.notnull}")
	@Size(min = 3, message = "{sample.external.id.too.short}")
	@ValidSampleName
	private String sequencerSampleId;

	@NotNull(message = "{sample.name.notnull}")
	@Size(min = 3, message = "{sample.name.too.short}")
	@ValidSampleName
	private String sampleName;

	@Lob
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "sampleTaxonomyId")),
			@AttributeOverride(name = "dataSource", column = @Column(name = "sampleTaxonomyDataSource")) })
	private TaxonomyID taxonomicId;

	@OneToOne
	private Host host;

	/** microbial or eukaryotic strain name */
	@Size(min = 3, message = "{sample.strain.name.too.short}")
	private String strain;

	// collection_date is a *mandatory* attribute in NCBI BioSample.
	private Date collectionDate;

	// collected_by is a *mandatory* attribute in NCBI BioSample.
	@Size(min = 3, message = "{sample.collected.by.too.short}")
	private String collectedBy;

	// probably want to use the disease ontology for this:
	// http://purl.obolibrary.org/obo/DOID_0050117
	private String disease;

	// lat_lon is marked as a *mandatory* attribute in NCBI BioSample, but in
	// practice many of the fields are shown as "missing".
	@Latitude
	private String latitude;

	@Longitude
	private String longitude;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sample")
	private List<ProjectSampleJoin> projects;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sample")
	private List<SampleSequenceFileJoin> sequenceFiles;

	public Sample() {
		createdDate = new Date();
		modifiedDate = createdDate;
	}

	/**
	 * Create a new {@link Sample} with the given name
	 * 
	 * @param sampleName
	 *            The name of the sample
	 */
	public Sample(String sampleName) {
		this.sampleName = sampleName;
	}

	/**
	 * Create a new {@link Sample} with the given name and ID
	 * 
	 * @param name
	 *            The sampleName of the sample
	 * @param sampleId
	 *            The ID of the sample
	 */
	public Sample(String sampleName, String sampleId) {
		this.sampleName = sampleName;
		this.sequencerSampleId = sampleId;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Sample) {
			Sample sample = (Sample) other;
			return Objects.equals(createdDate, sample.createdDate) && Objects.equals(modifiedDate, sample.modifiedDate)
					&& Objects.equals(sampleName, sample.sampleName) && Objects.equals(id, sample.id);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdDate, sampleName, modifiedDate, id);
	}

	@Override
	public int compareTo(Sample other) {
		return modifiedDate.compareTo(other.modifiedDate);
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public String getSequencerSampleId() {
		return sequencerSampleId;
	}

	public void setSequencerSampleId(String sampleId) {
		this.sequencerSampleId = sampleId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getLabel() {
		return sampleName;
	}

	@Override
	public Date getTimestamp() {
		return createdDate;
	}

	@Override
	public void setTimestamp(Date date) {
		this.createdDate = date;
	}

	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public List<ProjectSampleJoin> getProjects() {
		return projects;
	}

	public void setProjects(List<ProjectSampleJoin> projects) {
		this.projects = projects;
	}

	public List<SampleSequenceFileJoin> getSequenceFiles() {
		return sequenceFiles;
	}

	public void setSequenceFiles(List<SampleSequenceFileJoin> sequenceFiles) {
		this.sequenceFiles = sequenceFiles;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public TaxonomyID getTaxonomicId() {
		return taxonomicId;
	}

	public void setTaxonomicId(TaxonomyID taxonomicId) {
		this.taxonomicId = taxonomicId;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public String getStrain() {
		return strain;
	}

	public void setStrain(String strain) {
		this.strain = strain;
	}

	public Date getCollectionDate() {
		return collectionDate;
	}

	public void setCollectionDate(Date collectionDate) {
		this.collectionDate = collectionDate;
	}

	public String getCollectedBy() {
		return collectedBy;
	}

	public void setCollectedBy(String collectedBy) {
		this.collectedBy = collectedBy;
	}

	public String getDisease() {
		return disease;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}
