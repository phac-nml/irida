package ca.corefacility.bioinformatics.irida.model.sample;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaResourceSupport;
import ca.corefacility.bioinformatics.irida.model.IridaThing;
import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.irida.IridaSample;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.user.Organization;
import ca.corefacility.bioinformatics.irida.validators.groups.NCBISubmission;

/**
 * A biological sample. Each sample may correspond to many files.
 * 
 * A {@link Sample} comprises of many attributes. The attributes assigned to a
 * {@link Sample} correspond to the NCBI Pathogen BioSample attributes. See <a
 * href=
 * "https://submit.ncbi.nlm.nih.gov/biosample/template/?package=Pathogen.cl.1.0&action=definition"
 * >BioSample Attributes: Package Pathogen</a> for more information.
 * 
 */
@Entity
@Table(name = "sample")
@Audited
@EntityListeners(AuditingEntityListener.class)
public class Sample extends IridaResourceSupport implements IridaThing, IridaSample, Comparable<Sample> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private final Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	/**
	 * Note: The unique constraint makes sense programmatically, however it does
	 * not make sense to have a unique constraint for an external identifier
	 * from the perspective of a user; especially since the external identifier
	 * is provided entirely externally from the system.
	 */
	private String sequencerSampleId;

	private String sampleName;

	@Lob
	private String description;

	/**
	 * The most descriptive organism name for this sample (to the species, if
	 * relevant).
	 */
	private String organism;

	/**
	 * identification or description of the specific individual from which this
	 * sample was obtained
	 */
	private String isolate;

	/**
	 * microbial or eukaryotic strain name
	 */
	private String strain;

	/**
	 * Name of the person who collected the sample.
	 */
	private String collectedBy;

	/**
	 * Date of sampling
	 */
	@Temporal(TemporalType.DATE)
	private Date collectionDate;

	/**
	 * Geographical origin of the sample (country derived from
	 * http://www.insdc.org/documents/country-qualifier-vocabulary).
	 */
	private String geographicLocationName;

	@NotNull(message = "{sample.host.notnull}", groups = NCBISubmission.class)
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Host host = new Host();

	/**
	 * Describes the physical, environmental and/or local geographical source of
	 * the biological sample from which the sample was derived.
	 */
	@Lob
	private String isolationSource;

	/**
	 * lat_lon is marked as a *mandatory* attribute in NCBI BioSample, but in
	 * practice many of the fields are shown as "missing".
	 */
	private String latitude;

	private String longitude;

	/**
	 * Name of source institute and unique culture identifier. See the
	 * description for the proper format and list of allowed institutes,
	 * http://www.insdc.org/controlled-vocabulary-culturecollection-qualifier
	 */
	private String cultureCollection;

	/**
	 * observed genotype
	 */
	@Lob
	private String genotype;

	/**
	 * Number of passages and passage method
	 */
	@Lob
	private String passageHistory;

	/**
	 * Some bacterial specific pathotypes (example Eschericia coli - STEC, UPEC)
	 */
	private String pathotype;

	/**
	 * Taxonomy below subspecies; a variety (in bacteria, fungi or virus)
	 * usually based on its antigenic properties. Same as serovar and serogroup.
	 * e.g. serotype="H1N1" in Influenza A virus CY098518.
	 */
	private String serotype;

	/**
	 * Taxonomy below subspecies; a variety (in bacteria, fungi or virus)
	 * usually based on its antigenic properties. Same as serovar and serotype.
	 * Sometimes used as species identifier in bacteria with shaky taxonomy,
	 * e.g. Leptospira, serovar saopaolo S76607 (65357 in Entrez).
	 */
	private String serovar;

	/**
	 * Identifier for the physical specimen. Use format:
	 * "[<institution-code>:[<collection-code>:]]<specimen_id>", eg,
	 * "UAM:Mamm:52179". Intended as a reference to the physical specimen that
	 * remains after it was analyzed. If the specimen was destroyed in the
	 * process of analysis, electronic images (e-vouchers) are an adequate
	 * substitute for a physical voucher specimen. Ideally the specimens will be
	 * deposited in a curated museum, herbarium, or frozen tissue collection,
	 * but often they will remain in a personal or laboratory collection for
	 * some time before they are deposited in a curated collection. There are
	 * three forms of specimen_voucher qualifiers. If the text of the qualifier
	 * includes one or more colons it is a 'structured voucher'. Structured
	 * vouchers include institution-codes (and optional collection-codes) taken
	 * from a controlled vocabulary maintained by the INSDC that denotes the
	 * museum or herbarium collection where the specimen resides, please visit:
	 * http://www.insdc.org/controlled-vocabulary-specimenvoucher-qualifier.
	 */
	private String specimenVoucher;

	/**
	 * Taxonomy below subspecies; sometimes used in viruses to denote subgroups
	 * taken from a single isolate.
	 */
	private String subgroup;

	/**
	 * Used as classifier in viruses (e.g. HIV type 1, Group M, Subtype A).
	 */
	private String subtype;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sample")
	private List<ProjectSampleJoin> projects;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sample")
	private List<SampleSequenceFileJoin> sequenceFiles;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
	private Organization organization;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REMOVE, mappedBy="sample")
	@NotAudited
	private List<SampleAddedProjectEvent> events;

	public Sample() {
		createdDate = new Date();
	}

	/**
	 * Create a new {@link Sample} with the given name
	 * 
	 * @param sampleName
	 *            The name of the sample
	 */
	public Sample(String sampleName) {
		this();
		this.sampleName = sampleName;
	}

	/**
	 * Create a new {@link Sample} with the given name and ID
	 * 
	 * @param sampleName
	 *            The sampleName of the sample
	 * @param sampleId
	 *            The ID of the sample
	 */
	public Sample(String sampleName, String sampleId) {
		this();
		this.sampleName = sampleName;
		this.sequencerSampleId = sampleId;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Sample) {
			Sample sample = (Sample) other;
			return Objects.equals(id, sample.id) && Objects.equals(createdDate, sample.createdDate)
					&& Objects.equals(modifiedDate, sample.modifiedDate)
					&& Objects.equals(sequencerSampleId, sample.sequencerSampleId)
					&& Objects.equals(sampleName, sample.sampleName) && Objects.equals(description, sample.description)
					&& Objects.equals(organism, sample.organism) && Objects.equals(isolate, sample.isolate)
					&& Objects.equals(strain, sample.strain) && Objects.equals(collectedBy, sample.collectedBy)
					&& Objects.equals(collectionDate, sample.collectionDate)
					&& Objects.equals(geographicLocationName, sample.geographicLocationName)
					&& Objects.equals(host, sample.host) && Objects.equals(isolationSource, sample.isolationSource)
					&& Objects.equals(latitude, sample.latitude) && Objects.equals(longitude, sample.longitude)
					&& Objects.equals(cultureCollection, sample.cultureCollection)
					&& Objects.equals(genotype, sample.genotype)
					&& Objects.equals(passageHistory, sample.passageHistory)
					&& Objects.equals(pathotype, sample.pathotype) && Objects.equals(serotype, sample.serotype)
					&& Objects.equals(serovar, sample.serovar)
					&& Objects.equals(specimenVoucher, sample.specimenVoucher)
					&& Objects.equals(subgroup, sample.subgroup) && Objects.equals(subtype, sample.subtype)
					&& Objects.equals(organization, sample.organization);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, createdDate, modifiedDate, sequencerSampleId, sampleName, description, organism,
				isolate, strain, collectedBy, collectionDate, geographicLocationName, host, isolationSource, latitude,
				longitude, cultureCollection, genotype, passageHistory, pathotype, serotype, serovar, specimenVoucher,
				subgroup, subtype, organization);
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
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Date getCreatedDate() {
		return createdDate;
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

	public String getOrganism() {
		return organism;
	}

	public void setOrganism(String organism) {
		this.organism = organism;
	}

	public String getIsolate() {
		return isolate;
	}

	public void setIsolate(String isolate) {
		this.isolate = isolate;
	}

	public String getGeographicLocationName() {
		return geographicLocationName;
	}

	public void setGeographicLocationName(String geographicLocationName) {
		this.geographicLocationName = geographicLocationName;
	}

	public String getIsolationSource() {
		return isolationSource;
	}

	public void setIsolationSource(String isolationSource) {
		this.isolationSource = isolationSource;
	}

	public String getCultureCollection() {
		return cultureCollection;
	}

	public void setCultureCollection(String cultureCollection) {
		this.cultureCollection = cultureCollection;
	}

	public String getGenotype() {
		return genotype;
	}

	public void setGenotype(String genotype) {
		this.genotype = genotype;
	}

	public String getPassageHistory() {
		return passageHistory;
	}

	public void setPassageHistory(String passageHistory) {
		this.passageHistory = passageHistory;
	}

	public String getPathotype() {
		return pathotype;
	}

	public void setPathotype(String pathotype) {
		this.pathotype = pathotype;
	}

	public String getSerotype() {
		return serotype;
	}

	public void setSerotype(String serotype) {
		this.serotype = serotype;
	}

	public String getSerovar() {
		return serovar;
	}

	public void setSerovar(String serovar) {
		this.serovar = serovar;
	}

	public String getSpecimenVoucher() {
		return specimenVoucher;
	}

	public void setSpecimenVoucher(String specimenVoucher) {
		this.specimenVoucher = specimenVoucher;
	}

	public String getSubgroup() {
		return subgroup;
	}

	public void setSubgroup(String subgroup) {
		this.subgroup = subgroup;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
}
