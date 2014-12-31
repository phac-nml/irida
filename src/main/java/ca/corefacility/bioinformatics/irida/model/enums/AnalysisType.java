package ca.corefacility.bioinformatics.irida.model.enums;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Defines a specific type of an analysis.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
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
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return type;
	}
}
