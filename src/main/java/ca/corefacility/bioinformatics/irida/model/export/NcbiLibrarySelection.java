package ca.corefacility.bioinformatics.irida.model.export;

/**
 * Library selection of an ncbi export
 */
public enum NcbiLibrarySelection {

	RANDOM("RANDOM"),
	
	FIVE_METHYL("5-methylcytidine antibody"),

	CAGE("CAGE"),

	CFH("CF-H"),

	CFM("CF-M"),

	CFS("CF-S"),

	CFT("CF-T"),

	CHIP("ChIP"),

	DNASE("DNAse"),

	HMPR("HMPR"),

	HYBRID("Hybrid Selection"),

	MBD2("MBD2 protein methyl-CpG binding domain"),

	MF("MF"),

	MNASE("MNase"),

	MSLL("MSLL"),

	PCR("PCR"),

	RACE("RACE"),

	RANDOMPCR("RANDOM PCR"),

	RTPCR("RT-PCR"),

	REDUCED_REPRESENTATION("Reduced Representation"),

	RESTRICTION_DIGEST("Restriction Digest"),

	CDNA("cDNA"),

	OTHER("other"),

	SIZE_FRACTIONATION("size fractionation"),
	
	UNSPECIFIED("unspecified");

	private String value;

	private NcbiLibrarySelection(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static NcbiLibrarySelection fromString(String value) {
		for (NcbiLibrarySelection selection : NcbiLibrarySelection.values()) {
			if (selection.getValue().equals(value)) {
				return selection;
			}
		}
		return UNSPECIFIED;
	}
}
