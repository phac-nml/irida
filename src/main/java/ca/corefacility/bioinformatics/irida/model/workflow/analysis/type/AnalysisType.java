package ca.corefacility.bioinformatics.irida.model.workflow.analysis.type;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * Defines a particular type of Analysis available in IRIDA.
 *
 */
@Embeddable
@XmlType(name = "analysisType")
public class AnalysisType {

	@XmlValue
	@Column(name = "analysis_type")
	private final String type;

	protected AnalysisType() {
		this.type = null;
	}

	/**
	 * Creates a new {@link AnalysisType} with the given type.
	 * 
	 * @param type The type.
	 */
	public AnalysisType(String type) {
		checkNotNull(type, "type cannot be null");

		this.type = type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnalysisType other = (AnalysisType) obj;
		return Objects.equals(this.type, other.type);
	}

	@Override
	public String toString() {
		return getType();
	}

	/**
	 * Gets the particular type of the analysis as a string.
	 * 
	 * @return The type as a string.
	 */
	public String getType() {
		return type;
	}
}
