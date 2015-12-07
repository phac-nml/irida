package ca.corefacility.bioinformatics.irida.model.irida;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import ca.corefacility.bioinformatics.irida.validators.annotations.Latitude;
import ca.corefacility.bioinformatics.irida.validators.annotations.Longitude;
import ca.corefacility.bioinformatics.irida.validators.annotations.ValidSampleName;
import ca.corefacility.bioinformatics.irida.validators.groups.NCBISubmission;
import ca.corefacility.bioinformatics.irida.validators.groups.NCBISubmissionOneOf;

/**
 * Description of what data should be exposed from a Sample in IRIDA. Many of
 * these fields are based on NCBI BioSample. Information can be found at
 * https://submit.ncbi.nlm.nih.gov/biosample/template/
 * 
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public interface IridaSample {
	/**
	 * Get the local numerical identifier
	 * 
	 * @return the numerical identifier for the sample.
	 */
	public Long getId();

	/**
	 * Get the name of this sample
	 * 
	 * @return the name of the sample.
	 */
	@NotNull(message = "{sample.name.notnull}")
	@Size(min = 3, message = "{sample.name.too.short}")
	@ValidSampleName
	public String getSampleName();

	/**
	 * Get a text description of the sample
	 * 
	 * @return a plain-text description of the sample.
	 */
	public String getDescription();

	/**
	 * microbial or eukaryotic strain name
	 * 
	 * @return the strain name of the sample.
	 */
	@NotNull(message = "{sample.strain.name.notnull}", groups = { NCBISubmission.class, NCBISubmissionOneOf.class })
	@Size(min = 3, message = "{sample.strain.name.too.short}")
	public String getStrain();

	/**
	 * Date of sampling
	 * 
	 * @return the collection date of the sample.
	 */
	@NotNull(message = "{sample.collection.date.notnull}", groups = NCBISubmission.class)
	public Date getCollectionDate();

	/**
	 * Name of the person who collected the sample.
	 * 
	 * @return the name of the person who collected the sample.
	 */
	@NotNull(message = "{sample.collected.by.notnull}", groups = NCBISubmission.class)
	@Size(min = 3, message = "{sample.collected.by.too.short}")
	public String getCollectedBy();

	/**
	 * Get the latitude where this sample was collected
	 * 
	 * @return the latitude of the location where the sample was collected.
	 */
	@NotNull(message = "{sample.latitude.notnull}", groups = NCBISubmission.class)
	@Latitude
	public String getLatitude();

	/**
	 * Get the longitude where this sample was collected
	 * 
	 * @return the longitude of the location where the sample was collected.
	 */
	@NotNull(message = "{sample.longitude.notnull}", groups = NCBISubmission.class)
	@Longitude
	public String getLongitude();

	/**
	 * Get the organism represented in this sample
	 * 
	 * @return the organism found in the sample.
	 */
	@NotNull(message = "{sample.organism.notnull}", groups = NCBISubmission.class)
	@Size(min = 3, message = "{sample.organism.too.short}")
	public String getOrganism();

	/**
	 * Get the identification or description of the specific individual from
	 * which this sample was obtained
	 * 
	 * @return the isolate identifier for the sample.
	 */
	@NotNull(message = "{sample.isolate.notnull}", groups = { NCBISubmission.class, NCBISubmissionOneOf.class })
	@Size(min = 3, message = "{sample.isolate.too.short}")
	public String getIsolate();

	/**
	 * Geographical origin of the sample (country derived from
	 * http://www.insdc.org/documents/country-qualifier-vocabulary).
	 * 
	 * @return the geographic location name of the sample.
	 */
	@NotNull(message = "{sample.geographic.location.name.notnull}", groups = NCBISubmission.class)
	@Pattern(regexp = "\\w+(:\\w+(:\\w+)?)?", message = "{sample.geographic.location.name.pattern}")
	@Size(min = 3, message = "{sample.geographic.location.name.too.short}")
	public String getGeographicLocationName();

	/**
	 * Describes the physical, environmental and/or local geographical source of
	 * the biological sample from which the sample was derived.
	 * 
	 * @return the source of the sample.
	 */
	@NotNull(message = "{sample.isolation.source.notnull}", groups = NCBISubmission.class)
	public String getIsolationSource();
}
