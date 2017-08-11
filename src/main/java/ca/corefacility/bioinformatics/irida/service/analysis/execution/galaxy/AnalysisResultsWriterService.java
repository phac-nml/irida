package ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssemblyFromAnalysis;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisAssemblyAnnotation;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;

/**
 * A service for writing/handling analysis results writing.
 */
@Service
public class AnalysisResultsWriterService {

	private Map<Class<? extends Analysis>, AnalysisSampleUpdator> analysisUpdatorMap;

	public AnalysisResultsWriterService() {
		analysisUpdatorMap = new HashMap<>();
	}

	public void registerAnalysisSampleUpdator(Class<? extends Analysis> analysisClass, AnalysisSampleUpdator updator) {
		analysisUpdatorMap.put(analysisClass, updator);
	}

	public void updateSamples(Collection<Sample> samples, AnalysisSubmission completedSubmission) {
		Analysis analysis = completedSubmission.getAnalysis();
		AnalysisSampleUpdator sampleUpdator = analysisUpdatorMap.get(analysis.getClass());
		sampleUpdator.update(samples, analysis);
	}

	public static interface AnalysisSampleUpdator {
		public void update(Collection<Sample> samples, Analysis analysis);
	}

	public static class AssemblySampleUpdator implements AnalysisSampleUpdator {

		private static final Logger logger = LoggerFactory.getLogger(AssemblySampleUpdator.class);

		private final SampleRepository sampleRepository;

		public AssemblySampleUpdator(SampleRepository sampleRepository) {
			this.sampleRepository = sampleRepository;
		}

		@Override
		public void update(Collection<Sample> samples, Analysis analysis) {
			if (samples.size() != 1) {
				throw new RuntimeException("Error: expected only 1 sample, but got " + samples.size() + " samples");
			}

			Sample sample = samples.iterator().next();

			if (sample.getAssembly() == null) {
				sample.setGenomeAssembly(new GenomeAssemblyFromAnalysis((AnalysisAssemblyAnnotation) analysis));
				sampleRepository.save(sample);
			} else {
				logger.debug("Already exists assembly for sample " + sample + ", will not update assembly");
			}
		}
	}
}
