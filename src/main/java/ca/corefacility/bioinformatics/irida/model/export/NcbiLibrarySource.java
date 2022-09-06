package ca.corefacility.bioinformatics.irida.model.export;

/**
 * Library source of an NCBI export
 */
public enum NcbiLibrarySource {
	GENOMIC("GENOMIC"),

	METAGENOMIC("METAGENOMIC"),

	METATRANSCRIPTOMIC("METATRANSCRIPTOMIC"),

	OTHER("OTHER"),

	SYNTHETIC("SYNTHETIC"),

	TRANSCRIPTOMIC("TRANSCRIPTOMIC"),

	VIRALRNA("VIRAL RNA");

	private String value;

	private NcbiLibrarySource(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static NcbiLibrarySource fromString(String value) {
		for (NcbiLibrarySource source : NcbiLibrarySource.values()) {
			if (source.getValue().equals(value)) {
				return source;
			}
		}
		return null;
	}
}
