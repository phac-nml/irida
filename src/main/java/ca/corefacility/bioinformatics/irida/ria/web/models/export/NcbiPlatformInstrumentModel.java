package ca.corefacility.bioinformatics.irida.ria.web.models.export;

import java.util.EnumMap;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.export.NcbiInstrumentModel;

import com.google.common.collect.ImmutableList;

/**
 * Represents the relationship between sequencing platforms and instruments allowed by NCBI
 */
public class NcbiPlatformInstrumentModel {
	private final EnumMap<NcbiPlatform, List<String>> platforms = new EnumMap<NcbiPlatform, List<String>>(
			NcbiPlatform.class);

	public NcbiPlatformInstrumentModel() {

		List<String> BGISEQ = ImmutableList.of(NcbiInstrumentModel.BGISEQ_500.getModel(),
				NcbiInstrumentModel.DNBSEQ_G_50.getModel(), NcbiInstrumentModel.DNBSEQ_G_400.getModel(),
				NcbiInstrumentModel.DNBSEQ_T_7.getModel(), NcbiInstrumentModel.MGISEQ_2000_RS.getModel());

		List<String> CAPILLARY = ImmutableList.of(NcbiInstrumentModel.AB_310_GENETIC_ANALYZER.getModel(),
				NcbiInstrumentModel.AB_3130_GENETIC_ANALYZER.getModel(),
				NcbiInstrumentModel.AB_3130_X_L_GENETIC_ANALYZER.getModel(),
				NcbiInstrumentModel.AB_3730_GENETIC_ANALYZER.getModel(),
				NcbiInstrumentModel.AB_3730_X_L_GENETIC_ANALYZER.getModel());

		List<String> HELICOS = ImmutableList.of(NcbiInstrumentModel.HELICOS_HELISCOPE.getModel());

		List<String> ILLUMINA = ImmutableList.of(NcbiInstrumentModel.HI_SEQ_LIQUIBASE_PRO_PACKAGED_X_TEN.getModel(),
				NcbiInstrumentModel.HI_SEQ_X_FIVE.getModel(), NcbiInstrumentModel.ILLUMINA_GENOME_ANALYZER.getModel(),
				NcbiInstrumentModel.ILLUMINA_GENOME_ANALYZER_II.getModel(),
				NcbiInstrumentModel.ILLUMINA_GENOME_ANALYZER_IIX.getModel(),
				NcbiInstrumentModel.ILLUMINA_HI_SCAN_SQ.getModel(), NcbiInstrumentModel.ILLUMINA_HI_SEQ_1000.getModel(),
				NcbiInstrumentModel.ILLUMINA_HI_SEQ_1500.getModel(),
				NcbiInstrumentModel.ILLUMINA_HI_SEQ_2000.getModel(),
				NcbiInstrumentModel.ILLUMINA_HI_SEQ_2500.getModel(),
				NcbiInstrumentModel.ILLUMINA_HI_SEQ_3000.getModel(),
				NcbiInstrumentModel.ILLUMINA_HI_SEQ_4000.getModel(), NcbiInstrumentModel.ILLUMINA_HI_SEQ_X.getModel(),
				NcbiInstrumentModel.ILLUMINA_I_SEQ_100.getModel(), NcbiInstrumentModel.ILLUMINA_MI_SEQ.getModel(),
				NcbiInstrumentModel.ILLUMINA_MINI_SEQ.getModel(), NcbiInstrumentModel.ILLUMINA_NOVA_SEQ_6000.getModel(),
				NcbiInstrumentModel.NEXT_SEQ_500.getModel(), NcbiInstrumentModel.NEXT_SEQ_550.getModel(),
				NcbiInstrumentModel.NEXT_SEQ_1000.getModel(), NcbiInstrumentModel.NEXT_SEQ_2000.getModel());

		List<String> ION_TORRENT = ImmutableList.of(NcbiInstrumentModel.ION_GENE_STUDIO_S_5_PLUS.getModel(),
				NcbiInstrumentModel.ION_GENE_STUDIO_S_5_PRIME.getModel(),
				NcbiInstrumentModel.ION_GENE_STUDIO_S_5.getModel(), NcbiInstrumentModel.ION_TORRENT_PGM.getModel(),
				NcbiInstrumentModel.ION_TORRENT_PROTON.getModel(), NcbiInstrumentModel.ION_TORRENT_S_5_XL.getModel(),
				NcbiInstrumentModel.ION_TORRENT_S_5.getModel(), NcbiInstrumentModel.ION_UNSPECIFIED.getModel());

		List<String> LS454 = ImmutableList.of(NcbiInstrumentModel.LS_454_GS.getModel(),
				NcbiInstrumentModel.LS_454_GS_20.getModel(), NcbiInstrumentModel.LS_454_GS_FLX.getModel(),
				NcbiInstrumentModel.LS_454_GS_FLX_PLUS.getModel(),
				NcbiInstrumentModel.LS_454_GS_FLX_TITANIUM.getModel(), NcbiInstrumentModel.LS_454_GS_JUNIOR.getModel());

		List<String> OXFORD_NANOPORE = ImmutableList.of(NcbiInstrumentModel.GRID_ION.getModel(),
				NcbiInstrumentModel.MIN_ION.getModel(), NcbiInstrumentModel.PROMETH_ION.getModel());

		List<String> PACBIO_SMRT = ImmutableList.of(NcbiInstrumentModel.PAC_BIO_RS.getModel(),
				NcbiInstrumentModel.PAC_BIO_RS_II.getModel(), NcbiInstrumentModel.SEQUEL.getModel(),
				NcbiInstrumentModel.SEQUEL_II.getModel());

		platforms.put(NcbiPlatform.BGISEQ, BGISEQ);
		platforms.put(NcbiPlatform.CAPILLARY, CAPILLARY);
		platforms.put(NcbiPlatform.ILLUMINA, ILLUMINA);
		platforms.put(NcbiPlatform.HELICOS, HELICOS);
		platforms.put(NcbiPlatform.ION_TORRENT, ION_TORRENT);
		platforms.put(NcbiPlatform.LS454, LS454);
		platforms.put(NcbiPlatform.OXFORD_NANOPORE, OXFORD_NANOPORE);
		platforms.put(NcbiPlatform.PACBIO_SMRT, PACBIO_SMRT);
	}

	public EnumMap<NcbiPlatform, List<String>> getPlatforms() {
		return platforms;
	}
}
