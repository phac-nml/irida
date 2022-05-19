package ca.corefacility.bioinformatics.irida.model.export;

import java.util.EnumMap;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class NCBIPlatformModel {
	private final EnumMap<NCBIPlatform, List<String>> platforms = new EnumMap<NCBIPlatform, List<String>>(
			NCBIPlatform.class);

	public NCBIPlatformModel() {
		List<String> ABI_SOLID = ImmutableList.of(NCBIInstrumentModel.AB_5500_GENETIC_ANALYZER.getModel(),
				NCBIInstrumentModel.AB_5500_XL_GENETIC_ANALYZER.getModel(),
				NCBIInstrumentModel.AB_5500_XL_W_GENETIC_ANALYSIS_SYSTEM.getModel(),
				NCBIInstrumentModel.AB_SOLID_SYSTEM.getModel(), NCBIInstrumentModel.AB_SOLID_SYSTEM_2_0.getModel(),
				NCBIInstrumentModel.AB_SOLID_SYSTEM_3_0.getModel());

		List<String> BGISEQ = ImmutableList.of(NCBIInstrumentModel.BGISEQ_500.getModel(),
				NCBIInstrumentModel.DNBSEQ_G_400.getModel(), NCBIInstrumentModel.DNBSEQ_G_50.getModel(),
				NCBIInstrumentModel.DNBSEQ_T_7.getModel(), NCBIInstrumentModel.MGISEQ_2000_RS.getModel());

		List<String> CAPILLARY = ImmutableList.of(NCBIInstrumentModel.AB_310_GENETIC_ANALYZER.getModel(),
				NCBIInstrumentModel.AB_3130_GENETIC_ANALYZER.getModel(),
				NCBIInstrumentModel.AB_3130_X_L_GENETIC_ANALYZER.getModel(),
				NCBIInstrumentModel.AB_3730_GENETIC_ANALYZER.getModel(),
				NCBIInstrumentModel.AB_3730_X_L_GENETIC_ANALYZER.getModel());

		List<String> ILLUMINA = ImmutableList.of(NCBIInstrumentModel.HI_SEQ_X_FIVE.getModel(),
				NCBIInstrumentModel.HI_SEQ_LIQUIBASE_PRO_PACKAGED_X_TEN.getModel(),
				NCBIInstrumentModel.ILLUMINA_GENOME_ANALYZER.getModel(),
				NCBIInstrumentModel.ILLUMINA_GENOME_ANALYZER_II.getModel(),
				NCBIInstrumentModel.ILLUMINA_GENOME_ANALYZER_IIX.getModel(),
				NCBIInstrumentModel.ILLUMINA_HI_SCAN_SQ.getModel(), NCBIInstrumentModel.ILLUMINA_HI_SEQ_1000.getModel(),
				NCBIInstrumentModel.ILLUMINA_HI_SEQ_1500.getModel(),
				NCBIInstrumentModel.ILLUMINA_HI_SEQ_2000.getModel(),
				NCBIInstrumentModel.ILLUMINA_HI_SEQ_2500.getModel(),
				NCBIInstrumentModel.ILLUMINA_HI_SEQ_3000.getModel(),
				NCBIInstrumentModel.ILLUMINA_HI_SEQ_4000.getModel(), NCBIInstrumentModel.ILLUMINA_HI_SEQ_X.getModel(),
				NCBIInstrumentModel.ILLUMINA_MI_SEQ.getModel(), NCBIInstrumentModel.ILLUMINA_MINI_SEQ.getModel(),
				NCBIInstrumentModel.ILLUMINA_NOVA_SEQ_6000.getModel(),
				NCBIInstrumentModel.ILLUMINA_I_SEQ_100.getModel(), NCBIInstrumentModel.NEXT_SEQ_1000.getModel(),
				NCBIInstrumentModel.NEXT_SEQ_2000.getModel(), NCBIInstrumentModel.NEXT_SEQ_500.getModel(),
				NCBIInstrumentModel.NEXT_SEQ_550.getModel());

		List<String> ION_TORRENT = ImmutableList.of(NCBIInstrumentModel.ION_GENE_STUDIO_S_5.getModel(),
				NCBIInstrumentModel.ION_GENE_STUDIO_S_5_PLUS.getModel(),
				NCBIInstrumentModel.ION_GENE_STUDIO_S_5_PRIME.getModel(),
				NCBIInstrumentModel.ION_TORRENT_PGM.getModel(), NCBIInstrumentModel.ION_TORRENT_PROTON.getModel(),
				NCBIInstrumentModel.ION_TORRENT_S_5.getModel(), NCBIInstrumentModel.ION_TORRENT_S_5_XL.getModel(),
				NCBIInstrumentModel.ION_UNSPECIFIED.getModel());

		List<String> LS454 = ImmutableList.of(NCBIInstrumentModel.LS_454_GS.getModel(),
				NCBIInstrumentModel.LS_454_GS_20.getModel(), NCBIInstrumentModel.LS_454_GS_FLX.getModel(),
				NCBIInstrumentModel.LS_454_GS_FLX_TITANIUM.getModel(),
				NCBIInstrumentModel.LS_454_GS_FLX_PLUS.getModel(), NCBIInstrumentModel.LS_454_GS_JUNIOR.getModel());

		List<String> OXFORD_NANOPORE = ImmutableList.of(NCBIInstrumentModel.GRID_ION.getModel(),
				NCBIInstrumentModel.MIN_ION.getModel(), NCBIInstrumentModel.PROMETH_ION.getModel());

		List<String> PACBIO_SMRT = ImmutableList.of(NCBIInstrumentModel.PAC_BIO_RS.getModel(),
				NCBIInstrumentModel.PAC_BIO_RS_II.getModel(), NCBIInstrumentModel.SEQUEL.getModel(),
				NCBIInstrumentModel.SEQUEL_II.getModel());

		platforms.put(NCBIPlatform.ABI_SOLID, ABI_SOLID);
		platforms.put(NCBIPlatform.BGISEQ, BGISEQ);
		platforms.put(NCBIPlatform.CAPILLARY, CAPILLARY);
		platforms.put(NCBIPlatform.ILLUMINA, ILLUMINA);
		platforms.put(NCBIPlatform.ION_TORRENT, ION_TORRENT);
		platforms.put(NCBIPlatform.LS454, LS454);
		platforms.put(NCBIPlatform.OXFORD_NANOPORE, OXFORD_NANOPORE);
		platforms.put(NCBIPlatform.PACBIO_SMRT, PACBIO_SMRT);
	}

	public EnumMap<NCBIPlatform, List<String>> getPlatforms() {
		return platforms;
	}
}
