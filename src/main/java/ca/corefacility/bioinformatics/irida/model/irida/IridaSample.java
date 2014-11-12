package ca.corefacility.bioinformatics.irida.model.irida;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface IridaSample {
	/**
	 * Get the local numerical identifier
	 * 
	 * @return
	 */
	public Long getId();

	/**
	 * Get the name of this sample
	 * 
	 * @return
	 */
	@NotNull(message = "{sample.name.notnull}")
	@Size(min = 3, message = "{sample.name.too.short}")
	@ValidSampleName
	public String getSampleName();

	/**
	 * Get the sample ID as produced by the sequencer which produced it
	 * 
	 * @return
	 */
	@NotNull(message = "{sample.external.id.notnull}")
	@Size(min = 3, message = "{sample.external.id.too.short}")
	@ValidSampleName
	public String getSequencerSampleId();

	/**
	 * Get a text description of the sample
	 * 
	 * @return
	 */
	public String getDescription();

	/**
	 * microbial or eukaryotic strain name
	 * 
	 * @return
	 */
	@NotNull(message = "{sample.strain.name.notnull}", groups = { NCBISubmission.class, NCBISubmissionOneOf.class })
	@Size(min = 3, message = "{sample.strain.name.too.short}")
	public String getStrain();

	/**
	 * Date of sampling
	 * 
	 * @return
	 */
	@NotNull(message = "{sample.collection.date.notnull}", groups = NCBISubmission.class)
	public Date getCollectionDate();

	/**
	 * Name of the person who collected the sample.
	 * 
	 * @return
	 */
	@NotNull(message = "{sample.collected.by.notnull}", groups = NCBISubmission.class)
	@Size(min = 3, message = "{sample.collected.by.too.short}")
	public String getCollectedBy();

	/**
	 * Get the latitude where this sample was collected
	 * 
	 * @return
	 */
	@NotNull(message = "{sample.latitude.notnull}", groups = NCBISubmission.class)
	@Latitude
	public String getLatitude();

	/**
	 * Get the longitude where this sample was collected
	 * 
	 * @return
	 */
	@NotNull(message = "{sample.longitude.notnull}", groups = NCBISubmission.class)
	@Longitude
	public String getLongitude();

	/**
	 * Get the organism represented in this sample
	 * 
	 * @return
	 */
	@NotNull(message = "{sample.organism.notnull}", groups = NCBISubmission.class)
	@Size(min = 3, message = "{sample.organism.too.short}")
	public String getOrganism();

	/**
	 * Get the identification or description of the specific individual from
	 * which this sample was obtained
	 * 
	 * @return
	 */
	@NotNull(message = "{sample.isolate.notnull}", groups = { NCBISubmission.class, NCBISubmissionOneOf.class })
	@Size(min = 3, message = "{sample.isolate.too.short}")
	public String getIsolate();

	/**
	 * Geographical origin of the sample (country derived from
	 * http://www.insdc.org/documents/country-qualifier-vocabulary).
	 * 
	 * @return
	 */
	@NotNull(message = "{sample.geographic.location.name.notnull}", groups = NCBISubmission.class)
	@Pattern(regexp = "\\w+(:\\w+(:\\w+)?)?", message = "{sample.geographic.location.name.pattern}")
	@Size(min = 3, message = "{sample.geographic.location.name.too.short}")
	public String getGeographicLocationName();

	/**
	 * Describes the physical, environmental and/or local geographical source of
	 * the biological sample from which the sample was derived.
	 * 
	 * @return
	 */
	@NotNull(message = "{sample.isolation.source.notnull}", groups = NCBISubmission.class)
	public String getIsolationSource();

	/**
	 * Name of source institute and unique culture identifier. See the
	 * description for the proper format and list of allowed institutes,
	 * http://www.insdc.org/controlled-vocabulary-culturecollection-qualifier
	 * 
	 * @return
	 */
	@NotNull(message = "{sample.culture.collection.notnull}", groups = NCBISubmission.class)
	@Size(min = 1, message = "{sample.culture.collection.too.short}")
	public String getCultureCollection();

	/**
	 * observed genotype
	 * 
	 * @return
	 */
	public String getGenotype();

	/**
	 * Number of passages and passage method
	 * 
	 * @return
	 */
	public String getPassageHistory();

	/**
	 * Some bacterial specific pathotypes (example Eschericia coli - STEC, UPEC)
	 * 
	 * @return
	 */
	public String getPathotype();

	/**
	 * Taxonomy below subspecies; a variety (in bacteria, fungi or virus)
	 * usually based on its antigenic properties. Same as serovar and serogroup.
	 * e.g. serotype="H1N1" in Influenza A virus CY098518.
	 * 
	 * @return
	 */
	public String getSerotype();

	/**
	 * Taxonomy below subspecies; a variety (in bacteria, fungi or virus)
	 * usually based on its antigenic properties. Same as serovar and serotype.
	 * Sometimes used as species identifier in bacteria with shaky taxonomy,
	 * e.g. Leptospira, serovar saopaolo S76607 (65357 in Entrez).
	 * 
	 * @return
	 */
	public String getSerovar();

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
	 * 
	 * @return
	 */
	public String getSpecimenVoucher();

	/**
	 * Taxonomy below subspecies; sometimes used in viruses to denote subgroups
	 * taken from a single isolate.
	 * 
	 * @return
	 */
	public String getSubgroup();

	/**
	 * Used as classifier in viruses (e.g. HIV type 1, Group M, Subtype A).
	 * 
	 * @return
	 */
	public String getSubtype();

}
