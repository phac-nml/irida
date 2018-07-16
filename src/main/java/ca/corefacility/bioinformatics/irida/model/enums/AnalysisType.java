package ca.corefacility.bioinformatics.irida.model.enums;

import java.util.Objects;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import static com.google.common.base.Preconditions.checkNotNull;

@XmlType(name="analysisType")
public class AnalysisType {

	@XmlValue
	private final String type;
	
	protected AnalysisType() {
		this.type = null;
	}
	
	public AnalysisType(String typeDatabase) {
		checkNotNull(typeDatabase, "type cannot be null");
		
		this.type = typeDatabase;
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
		return "AnalysisType [type=" + type + "]";
	}

	public String getName() {
		return type;
	}
}
