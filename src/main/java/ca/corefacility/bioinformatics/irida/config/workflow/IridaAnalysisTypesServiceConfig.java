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
 * Class to load up {@link AnalysisTypesService}. Separated into separate class to handle Spring dependencies better.
 *
 */
@Configuration
@Import({ IridaPluginConfig.class })
public class IridaAnalysisTypesServiceConfig {
	
	@Autowired
	private IridaPluginConfig.IridaPluginList iridaPipelinePlugins;
	
	@Bean
	public AnalysisTypesService analysisTypesService() {
		Set<AnalysisType> runnableAnalysisTypes = Sets.newHashSet(BuiltInAnalysisTypes.PHYLOGENOMICS,
				BuiltInAnalysisTypes.SISTR_TYPING, BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION, BuiltInAnalysisTypes.BIO_HANSEL,
				BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION_COLLECTION, BuiltInAnalysisTypes.REFSEQ_MASHER,
				BuiltInAnalysisTypes.MLST_MENTALIST);
		Set<AnalysisType> otherAnalysisTypes = Sets.newHashSet(BuiltInAnalysisTypes.DEFAULT, BuiltInAnalysisTypes.FASTQC);
	
		for (IridaPlugin plugin : iridaPipelinePlugins.getPlugins()) {
			runnableAnalysisTypes.add(plugin.getAnalysisType());
		}
		
		return new AnalysisTypesServiceImpl(runnableAnalysisTypes, otherAnalysisTypes);
	}
}
