package ca.corefacility.bioinformatics.irida.model.export;

public enum NCBIInstrumentModel {
	AB_5500_GENETIC_ANALYZER("AB 5500 Genetic Analyzer"),
	AB_5500_XL_GENETIC_ANALYZER("AB 5500xl Genetic Analyzer"),
	AB_5500_XL_W_GENETIC_ANALYSIS_SYSTEM("AB 5500xl-W Genetic Analysis System"),
	AB_SOLID_SYSTEM("AB SOLiD System"),
	AB_SOLID_SYSTEM_2_0("AB SOLiD System 2.0"),
	AB_SOLID_SYSTEM_3_0("AB SOLiD System 3.0"),
	BGISEQ_500("BGISEQ-500"),
	DNBSEQ_G_400("DNBSEQ-G400"),
	DNBSEQ_G_50("DNBSEQ-G50"),
	DNBSEQ_T_7("DNBSEQ-T7 "),
	MGISEQ_2000_RS("MGISEQ-2000RS"),
	AB_310_GENETIC_ANALYZER("AB 310 Genetic Analyzer"),
	AB_3130_GENETIC_ANALYZER("AB 3130 Genetic Analyzer"),
	AB_3130_X_L_GENETIC_ANALYZER("AB 3130xL Genetic Analyzer"),
	AB_3730_GENETIC_ANALYZER("AB 3730 Genetic Analyzer"),
	AB_3730_X_L_GENETIC_ANALYZER("AB 3730xL Genetic Analyzer "),
	//	ILLUMINA
	HI_SEQ_X_FIVE("HiSeq X Five"),
	HI_SEQ_LIQUIBASE_PRO_PACKAGED_X_TEN("HiSeq liquibase.pro.packaged.X Ten"),
	ILLUMINA_GENOME_ANALYZER("Illumina Genome Analyzer"),
	ILLUMINA_GENOME_ANALYZER_II("Illumina Genome Analyzer II"),
	ILLUMINA_GENOME_ANALYZER_IIX("Illumina Genome Analyzer IIx"),
	ILLUMINA_HI_SCAN_SQ("Illumina HiScanSQ"),
	ILLUMINA_HI_SEQ_1000("Illumina HiSeq 1000"),
	ILLUMINA_HI_SEQ_1500("Illumina HiSeq 1500"),
	ILLUMINA_HI_SEQ_2000("Illumina HiSeq 2000"),
	ILLUMINA_HI_SEQ_2500("Illumina HiSeq 2500"),
	ILLUMINA_HI_SEQ_3000("Illumina HiSeq 3000"),
	ILLUMINA_HI_SEQ_4000("Illumina HiSeq 4000"),
	ILLUMINA_HI_SEQ_X("Illumina HiSeq X"),
	ILLUMINA_MI_SEQ("Illumina MiSeq"),
	ILLUMINA_MINI_SEQ("Illumina MiniSeq"),
	ILLUMINA_NOVA_SEQ_6000("Illumina NovaSeq 6000"),
	ILLUMINA_I_SEQ_100("Illumina iSeq 100"),
	NEXT_SEQ_1000("NextSeq 1000"),
	NEXT_SEQ_2000("NextSeq 2000"),
	NEXT_SEQ_500("NextSeq 500"),
	NEXT_SEQ_550("NextSeq 550"),
	// ION_TORRENT
	ION_GENE_STUDIO_S_5("Ion GeneStudio S5"),
	ION_GENE_STUDIO_S_5_PLUS("Ion GeneStudio S5 Plus"),
	ION_GENE_STUDIO_S_5_PRIME("Ion GeneStudio S5 Prime"),
	ION_TORRENT_PGM("Ion Torrent PGM"),
	ION_TORRENT_PROTON("Ion Torrent Proton"),
	ION_TORRENT_S_5("Ion Torrent S5"),
	ION_TORRENT_S_5_XL("Ion Torrent S5 XL"),
	ION_UNSPECIFIED("unspecified"),

	// LS454
	LS_454_GS("454 GS"),
	LS_454_GS_20("454 GS 20"),
	LS_454_GS_FLX("454 GS FLX"),
	LS_454_GS_FLX_TITANIUM("454 GS FLX Titanium"),
	LS_454_GS_FLX_PLUS("454 GS FLX+"),
	LS_454_GS_JUNIOR("454 GS Junior"),

	// OXFORD_NANOPORE
	GRID_ION("GridION"),
	MIN_ION("MinION"),
	PROMETH_ION("PromethION"),

	// PACBIO_SMRT
	PAC_BIO_RS("PacBio RS"),
	PAC_BIO_RS_II("PacBio RS II"),
	SEQUEL("Sequel"),
	SEQUEL_II("Sequel II");

	private final String model;

	NCBIInstrumentModel(String model) {
		this.model = model;
	}

	public String getModel() {
		return model;
	}
}
