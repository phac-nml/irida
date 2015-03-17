package ca.corefacility.bioinformatics.irida.model.enums;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisAssemblyAnnotation;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;

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
	PHYLOGENOMICS("phylogenomics", AnalysisPhylogenomicsPipeline.class),
	
	/**
	 * An assembly and annotation analysis type.
	 */
	@XmlEnumValue("assembly-annotation")
	ASSEMBLY_ANNOTATION("assembly-annotation", AnalysisAssemblyAnnotation.class),

	/**
	 * A default analysis type.
	 */
	@XmlEnumValue("default")
	DEFAULT("default", Analysis.class);

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
	private Class<? extends Analysis> analysisClass;

	/**
	 * Sets of a Map used to convert a string to an AnalysisType
	 */
	static {
		for (AnalysisType type : AnalysisType.values()) {
			typeMap.put(type.toString(), type);
		}
	}

	private AnalysisType(String type, Class<? extends Analysis> analysisClass) {
		this.type = type;
		this.analysisClass = analysisClass;
	}
	
	/**
	 * Gets the particular {@link Analysis} class corresponding to this type.
	 * @return  An {@link Analysis} class for this type.
	 */
	public Class<? extends Analysis> getAnalysisClass() {
		return analysisClass;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return type;
	}
}
