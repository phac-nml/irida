package ca.corefacility.bioinformatics.irida.model.export;

/**
 * Instrument model for NCBI Upload.
 *
 * @see <a href=
 *      "http://www.ncbi.nlm.nih.gov/books/NBK54984/table/SRA_Glossary_BK.T._platform_descriptor_t/?report=objectonly">
 *      NCBI Docs</a>
 */
public enum NcbiInstrumentModel {

	ILLUMINAMISEQ("Illumina MiSeq"),

	ILLUMINANEXTSEQ500("NextSeq 500"),

	ILLUMINANEXTSEQ550("NextSeq 550"),

	ABSOLID4("AB SOLiD 4 System"),

	ABSOLID4HQ("AB SOLiD 4hq System"),

	ABSOLID5500("AB SOLiD 5500"),

	ABSOLID5500XL("AB SOLiD 5500xl"),

	ABSOLIDPI("AB SOLiD PI System"),

	ABSOLID2("AB SOLiD System 2.0"),

	ABSOLID3("AB SOLiD System 3.0"),

	ABSOLID("AB SOLiD System"),

	COMPLETEGENOMICS("Complete Genomics"),

	HELICOSHELISCOPE("Helicos HeliScope"),

	ILLUMINAGAII("Illumina Genome Analyzer II"),

	ILLUMINAGAIIX("Illumina Genome Analyzer IIx"),

	ILLUMINAGA("Illumina Genome Analyzer"),

	ILLUMINAHISEQ1000("Illumina HiSeq 1000"),

	ILLUMINAHISEQ1500("Illumina HiSeq 1500"),

	ILLUMINAHISEQ2000("Illumina HiSeq 2000"),

	ILLUMINAHISEQ2500("Illumina HiSeq 2500"),

	ILLUMINAHISEQ3000("Illumina HiSeq 3000"),

	ILLUMINAHISEQ4000("Illumina HiSeq 4000"),

	IONTORRENTPGM("Ion Torrent PGM"),

	IONTORRENTPROTON("Ion Torrent PROTON"),

	ROCHE454GS20("454 GS 20"),

	ROCHE454GSFLXTI("454 GS FLX Titanium"),

	ROCHE454GSFLX("454 GS FLX"),

	ROCHE454GSJR("454 GS Junior"),

	ROCHE454GS("454 GS"),

	PACBIORS("PacBio RS"),

	PACBIORSII("PacBio RS II");

	private String value;

	private NcbiInstrumentModel(String value) {
		this.value = value;
	}

	/**
	 * Return the string value of the {@link NcbiInstrumentModel}
	 *
	 * @return the string value.
	 */
	public String getValue() {
		return value;
	}

}
