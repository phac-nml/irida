package ca.corefacility.bioinformatics.irida.config.workflow;

import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisTypes;
import ca.corefacility.bioinformatics.irida.service.AnalysisTypesService;
import ca.corefacility.bioinformatics.irida.service.impl.AnalysisTypesServiceImpl;

/**
 * Class to load up {@link AnalysisTypesService}. Separated into separate class to handle Spring dependencies better.
 *
 */
@Configuration
public class IridaAnalysisTypesServiceConfig {
	
	@Bean
	public AnalysisTypesService analysisTypesService() {
		Set<AnalysisType> runnableAnalysisTypes = Sets.newHashSet(AnalysisTypes.PHYLOGENOMICS,
				AnalysisTypes.SISTR_TYPING, AnalysisTypes.ASSEMBLY_ANNOTATION, AnalysisTypes.BIO_HANSEL,
				AnalysisTypes.ASSEMBLY_ANNOTATION_COLLECTION, AnalysisTypes.REFSEQ_MASHER,
				AnalysisTypes.MLST_MENTALIST);
		Set<AnalysisType> otherAnalysisTypes = Sets.newHashSet(AnalysisTypes.DEFAULT, AnalysisTypes.FASTQC);
		
		return new AnalysisTypesServiceImpl(runnableAnalysisTypes, otherAnalysisTypes);
	}
}
