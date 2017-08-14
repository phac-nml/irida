package ca.corefacility.bioinformatics.irida.service.impl.analysis.sample;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.analysis.sample.AnalysisSampleUpdatorService;
import ca.corefacility.bioinformatics.irida.service.analysis.sample.AnalysisSubmissionSampleService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Updates samples from an {@link AnalysisSubmission} with results from the analysis.
 */
@Service
public class AnalysisSubmissionSampleServiceImpl implements AnalysisSubmissionSampleService {

	private static final Logger logger = LoggerFactory.getLogger(AssemblySampleUpdatorService.class);

	@Resource(name="analysisSampleUpdatorMap")
	private Map<Class<? extends Analysis>, AnalysisSampleUpdatorService> analysisSampleUpdatorMap;
	
	@Autowired
	private SampleService sampleService;

	public AnalysisSubmissionSampleServiceImpl() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(AnalysisSubmission analysisSubmission) {
		Collection<Sample> samples = sampleService.getSamplesForAnalysisSubimssion(analysisSubmission);
		Analysis analysis = analysisSubmission.getAnalysis();

		checkNotNull(analysis, "No analysis associated with submission " + analysisSubmission);
		checkNotNull(samples, "No samples associated with submission " + analysisSubmission);

		AnalysisSampleUpdatorService analysisSampleUpdatorService = analysisSampleUpdatorMap.get(analysis.getClass());

		if (analysisSampleUpdatorService != null) {
			analysisSampleUpdatorService.update(samples, analysis);
		} else {
			logger.debug("No associated object for updating samples for analysis of type " + analysis.getClass());
		}
	}
}