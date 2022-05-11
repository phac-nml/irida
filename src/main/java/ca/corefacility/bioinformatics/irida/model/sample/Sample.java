package ca.corefacility.bioinformatics.irida.model.sample;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import ca.corefacility.bioinformatics.irida.model.IridaRepresentationModel;
import ca.corefacility.bioinformatics.irida.model.MutableIridaThing;
import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleGenomeAssemblyJoin;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSynchronizable;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.validators.annotations.Latitude;
import ca.corefacility.bioinformatics.irida.validators.annotations.Longitude;
import ca.corefacility.bioinformatics.irida.validators.annotations.ValidSampleName;
import ca.corefacility.bioinformatics.irida.validators.groups.NCBISubmission;
import ca.corefacility.bioinformatics.irida.validators.groups.NCBISubmissionOneOf;
import ca.corefacility.bioinformatics.irida.web.controller.api.json.DateJson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A biological sample. Each sample may correspond to many files.
 * A {@link Sample} comprises of many attributes. The attributes assigned to a
 * {@link Sample} correspond to the NCBI Pathogen BioSample attributes. See
 * <a href=
 * "https://submit.ncbi.nlm.nih.gov/biosample/template/?package=Pathogen.cl.1.0&action=definition"
 * >BioSample Attributes: Package Pathogen</a> for more information.
 */
@Entity
@NamedEntityGraph(name = "sampleOnly")
@Table(name = "sample")
@Audited
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sample extends IridaRepresentationModel
		implements MutableIridaThing, Comparable<Sample>, RemoteSynchronizable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@CreatedDate
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	private Date createdDate;

	@LastModifiedDate
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedDate;

	@NotNull(message = "{sample.name.notnull}")
	@Size(min = 3, message = "{sample.name.too.short}")
	@ValidSampleName
	private String sampleName;

	@Lob
	private String description;

	/**
	 * The most descriptive organism name for this sample (to the species, if
	 * relevant).
	 */
	@NotNull(message = "{sample.organism.notnull}", groups = NCBISubmission.class)
	@Size(min = 3, message = "{sample.organism.too.short}")
	private String organism;

	/**
	 * identification or description of the specific individual from which this
	 * sample was obtained
	 */
	@NotNull(message = "{sample.isolate.notnull}", groups = { NCBISubmission.class, NCBISubmissionOneOf.class })
	@Size(min = 3, message = "{sample.isolate.too.short}")
	private String isolate;

	/**
	 * microbial or eukaryotic strain name
	 */
	@NotNull(message = "{sample.strain.name.notnull}", groups = { NCBISubmission.class, NCBISubmissionOneOf.class })
	@Size(min = 3, message = "{sample.strain.name.too.short}")
	private String strain;

	/**
	 * Name of the person who collected the sample.
	 */
	@NotNull(message = "{sample.collected.by.notnull}", groups = NCBISubmission.class)
	@Size(min = 3, message = "{sample.collected.by.too.short}")
	private String collectedBy;

	/**
	 * Date of sampling
	 */
	@Temporal(TemporalType.DATE)
	@JsonSerialize(using = DateJson.DateSerializer.class)
	@JsonDeserialize(using = DateJson.DateDeserializer.class)
	@NotNull(message = "{sample.collection.date.notnull}", groups = NCBISubmission.class)
	@Schema(type = "string", format = "date")
	private Date collectionDate;

	/**
	 * Geographical origin of the sample (country derived from
	 * http://www.insdc.org/documents/country-qualifier-vocabulary).
	 */
	@NotNull(message = "{sample.geographic.location.name.notnull}", groups = NCBISubmission.class)
	@Pattern(regexp = "\\w+(:\\w+(:\\w+)?)?", message = "{sample.geographic.location.name.pattern}")
	@Size(min = 3, message = "{sample.geographic.location.name.too.short}")
	private String geographicLocationName;

	/**
	 * Describes the physical, environmental and/or local geographical source of
	 * the biological sample from which the sample was derived.
	 */
	@Lob
	@NotNull(message = "{sample.isolation.source.notnull}", groups = NCBISubmission.class)
	private String isolationSource;

	/**
	 * lat_lon is marked as a *mandatory* attribute in NCBI BioSample, but in
	 * practice many of the fields are shown as "missing".
	 */
	@NotNull(message = "{sample.latitude.notnull}", groups = NCBISubmission.class)
	@Latitude
	private String latitude;

	@NotNull(message = "{sample.longitude.notnull}", groups = NCBISubmission.class)
	@Longitude
	private String longitude;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sample")
	private List<ProjectSampleJoin> projects;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sample")
	private List<SampleSequencingObjectJoin> sequenceFiles;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sample")
	@NotAudited
	private List<SampleAddedProjectEvent> events;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "remote_status")
	private RemoteStatus remoteStatus;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sample")
	private Set<MetadataEntry> metadataEntries;
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "sample")
	private List<SampleGenomeAssemblyJoin> genomeAssemblies;

	public Sample() {
		createdDate = new Date();
	}

	/**
	 * Create a new {@link Sample} with the given name
	 *
	 * @param sampleName The name of the sample
	 */
	public Sample(String sampleName) {
		this();
		this.sampleName = sampleName;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Sample) {
			Sample sample = (Sample) other;
			return Objects.equals(id, sample.id) && Objects.equals(createdDate, sample.createdDate) && Objects.equals(
					modifiedDate, sample.modifiedDate) && Objects.equals(sampleName, sample.sampleName)
					&& Objects.equals(description, sample.description) && Objects.equals(organism, sample.organism)
					&& Objects.equals(isolate, sample.isolate) && Objects.equals(strain, sample.strain)
					&& Objects.equals(collectedBy, sample.collectedBy) && Objects.equals(collectionDate,
					sample.collectionDate) && Objects.equals(geographicLocationName, sample.geographicLocationName)
					&& Objects.equals(isolationSource, sample.isolationSource) && Objects.equals(latitude,
					sample.latitude) && Objects.equals(longitude, sample.longitude);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, createdDate, modifiedDate, sampleName, description, organism, isolate, strain,
				collectedBy, collectionDate, geographicLocationName, isolationSource, latitude, longitude);
	}

	@Override
	public int compareTo(Sample other) {
		return modifiedDate.compareTo(other.modifiedDate);
	}

	@Override
	public String toString() {
		// @formatter:off
		return "Sample{" + "id=" + id +
				", sampleName='" + sampleName + '\'' +
				", organism='" + organism + '\'' +
				", modifiedDate=" + modifiedDate +
				", createdDate=" + createdDate +
				'}';
		// @formatter:on
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

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getCreatedDate() {
		return createdDate;
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

	@Override
	public RemoteStatus getRemoteStatus() {
		return remoteStatus;
	}

	@Override
	public void setRemoteStatus(RemoteStatus status) {
		this.remoteStatus = status;
	}
}
