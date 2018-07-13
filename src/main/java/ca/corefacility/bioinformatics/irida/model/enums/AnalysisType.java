package ca.corefacility.bioinformatics.irida.model.enums;

import java.util.Objects;

public class AnalysisType {
	
	private String typeDatabase;
	private String typeXml;
	
	public AnalysisType(String typeDatabase, String typeXml) {
		this.typeDatabase = typeDatabase;
		this.typeXml = typeXml;
	}

	@Override
	public int hashCode() {
		return Objects.hash(typeDatabase, typeXml);
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
		return Objects.equals(this.typeDatabase, other.typeDatabase) &&
				Objects.equals(this.typeXml, other.typeXml);
	}

	@Override
	public String toString() {
		return "AnalysisType [typeDatabase=" + typeDatabase + ", typeXml=" + typeXml + "]";
	}

	public String getName() {
		return typeXml;
	}
}
