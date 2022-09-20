package ca.corefacility.bioinformatics.irida.model.export;

/**
 * Library strategy of an ncbi export
 */
public enum NcbiLibraryStrategy {

	WGS("WGS"),

	AMPLICON("AMPLICON"),

	BISULFITE_SEQ("Bisulfite-Seq"),

	CLONEEND("CLONEEND"),

	CLONE("CLONE"),

	CTS("CTS"),

	CHIPSEQ("ChIP-Seq"),

	DNASE_HYPERSENSITIVITY("DNase-Hypersensitivity"),

	EST("EST"),

	FINISHING("FINISHING"),

	FL_CDNA("FL-cDNA"),

	MOD_SEQ("MBD-Seq"),

	MNASE_SEQ("MNase-Seq"),

	MRE_SEQ("MRE-Seq"),

	MEDIP_SEQ("MeDIP-Seq"),

	OTHER("OTHER"),

	POOLCLONE("POOLCLONE"),

	RNA_SEQ("RNA-Seq"),

	WCS("WCS"),

	WXS("WXS");

	private String value;

	private NcbiLibraryStrategy(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static NcbiLibraryStrategy fromString(String value) {
		for (NcbiLibraryStrategy strategy : NcbiLibraryStrategy.values()) {
			if (strategy.getValue().equals(value)) {
				return strategy;
			}
		}
		return null;
	}
}
