package ca.corefacility.bioinformatics.irida.model.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Defines a specific type of an analysis.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
@XmlEnum
public enum AnalysisType {
	
	/**
	 * A phylogenomics analysis type for generating phylogenomic trees.
	 */
	@XmlEnumValue("PHYLOGENOMICS")
	PHYLOGENOMICS,
	
	/**
	 * A default analysis type.
	 */
	@XmlEnumValue("DEFAULT")
	DEFAULT;
}
