package ca.corefacility.bioinformatics.irida.config.workflow;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.config.services.IridaPluginConfig;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.plugins.IridaPlugin;
import ca.corefacility.bioinformatics.irida.service.AnalysisTypesService;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisTypesServiceImpl;

/**
 * Class to load up {@link AnalysisTypesService}. Separated into separate class
 * to handle Spring dependencies better.
 */
@Configuration
@Import({ IridaPluginConfig.class })
public class IridaAnalysisTypesServiceConfig {

	@Autowired
	private IridaPluginConfig.IridaPluginList iridaPipelinePlugins;

	/**
	 * Builds a new bean for a {@link AnalysisTypesService} to handle registered {@link AnalysisType}s.
	 *
	 * @return The {@link AnalysisTypesService}.
	 */
	@Bean
	public AnalysisTypesService analysisTypesService() {

		AnalysisTypesServiceImpl analysisTypesService = new AnalysisTypesServiceImpl();

		// defines AnalysisTypes built into IRIDA
		analysisTypesService.registerRunnableType(BuiltInAnalysisTypes.PHYLOGENOMICS, "tree");
		analysisTypesService.registerRunnableType(BuiltInAnalysisTypes.SISTR_TYPING, "sistr");
		analysisTypesService.registerRunnableType(BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION);
		analysisTypesService.registerRunnableType(BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION_COLLECTION);
		analysisTypesService.registerRunnableType(BuiltInAnalysisTypes.BIO_HANSEL, "biohansel");
		analysisTypesService.registerRunnableType(BuiltInAnalysisTypes.REFSEQ_MASHER);
		analysisTypesService.registerRunnableType(BuiltInAnalysisTypes.MLST_MENTALIST, "tree");

		//registers unrunnable types like fastqc
		analysisTypesService.registerUnrunnableType(BuiltInAnalysisTypes.DEFAULT);
		analysisTypesService.registerUnrunnableType(BuiltInAnalysisTypes.FASTQC);

		// adds additional analysis types for each loaded plugin
		for (IridaPlugin plugin : iridaPipelinePlugins.getPlugins()) {
			analysisTypesService.registerRunnableType(plugin.getAnalysisType());
		}

		return analysisTypesService;
	}
}
