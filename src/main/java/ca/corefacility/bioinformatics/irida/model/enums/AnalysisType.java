package ca.corefacility.bioinformatics.irida.model.enums;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import com.google.common.collect.Sets;

/**
 * Defines a specific type of an analysis.
 * 
 *
 */
@XmlEnum
public enum AnalysisType {

	/**
	 * A phylogenomics analysis type for generating phylogenomic trees.
	 */
	@XmlEnumValue("phylogenomics")
	PHYLOGENOMICS("phylogenomics"),

	/**
	 * SISTR Typing.
	 */
	@XmlEnumValue("sistr-typing")
	SISTR_TYPING("sistr-typing"),
	
	/**
	 * An assembly and annotation analysis type on a single sample.
	 */
	@XmlEnumValue("assembly-annotation")
	ASSEMBLY_ANNOTATION("assembly-annotation"),

	/**
	 * An assembly and annotation analysis type on a collection of samples.
	 */
	@XmlEnumValue("assembly-annotation-collection")
	ASSEMBLY_ANNOTATION_COLLECTION("assembly-annotation-collection"),

	/**
	 * refseq_masher genomic distance estimation and containment of sample to NCBI RefSeq genomes
	 */
	@XmlEnumValue("refseq_masher")
	REFSEQ_MASHER("refseq_masher"),
	
	/**
	 * A fastqc analysis type
	 */
	@XmlEnumValue("fastqc")
	FASTQC("fastqc"),

	/**
	 * A default analysis type.
	 */
	@XmlEnumValue("default")
	DEFAULT("default");

	/**
	 * Creates an {@link AnalysisType} from the corresponding String.
	 * 
	 * @param type
	 *            The string defining which type to return.
	 * @return The corresponding {@link AnalysisType}.
	 */
	public static AnalysisType fromString(String type) {
		checkNotNull(type, "type is null");
		checkArgument(typeMap.containsKey(type), "no corresponding AnalysisType for " + type);

		return typeMap.get(type);
	}

	private static Map<String, AnalysisType> typeMap = new HashMap<>();

	private String type;

	/**
	 * Sets of a Map used to convert a string to an AnalysisType
	 */
	static {
		for (AnalysisType type : AnalysisType.values()) {
			typeMap.put(type.toString(), type);
		}
	}

	private AnalysisType(String type) {
		this.type = type;
	}

	/**
	 * Generates an array of all {@link AnalysisType}s minus the
	 * {@code AnalysisType.DEFAULT}.
	 * 
	 * @return An array of all {@link AnalysisType}s minus the
	 *         {@code AnalysisType.DEFAULT}
	 */
	public static AnalysisType[] valuesMinusDefault() {
		AnalysisType[] values = AnalysisType.values();
		Set<AnalysisType> valuesSet = Sets.newHashSet(values);
		valuesSet.remove(AnalysisType.DEFAULT);
		return valuesSet.toArray(new AnalysisType[values.length - 1]);
	}

	/**
	 * Get the array of all {@link AnalysisType}s that can be executed by
	 * galaxy. This removes {@link AnalysisType#DEFAULT} and
	 * {@link AnalysisType#FASTQC}
	 * 
	 * @return An array of all {@link AnalysisType}s which can be executed by
	 *         galaxy
	 */
	public static AnalysisType[] executableAnalysisTypes() {
		AnalysisType[] values = AnalysisType.values();
		Set<AnalysisType> valuesSet = Sets.newHashSet(values);
		valuesSet.remove(AnalysisType.DEFAULT);
		valuesSet.remove(AnalysisType.FASTQC);

		return valuesSet.toArray(new AnalysisType[values.length - 2]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return type;
	}
}
