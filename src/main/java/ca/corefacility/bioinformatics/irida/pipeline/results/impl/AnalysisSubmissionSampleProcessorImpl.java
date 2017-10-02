package ca.corefacility.bioinformatics.irida.pipeline.results.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;

import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSampleUpdator;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.service.analysis.annotations.RunAsUser;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Updates samples from an {@link AnalysisSubmission} with results from the
 * analysis.
 */
@Component
public class AnalysisSubmissionSampleProcessorImpl implements AnalysisSubmissionSampleProcessor {

	private static final Logger logger = LoggerFactory.getLogger(AnalysisSubmissionSampleProcessorImpl.class);

	private final Map<AnalysisType, AnalysisSampleUpdator> analysisSampleUpdatorMap;
	private final SampleRepository sampleRepository;

	/**
	 * Builds a new {@link AnalysisSubmissionSampleProcessorImpl}.
	 * 
	 * @param sampleService
	 *            The {@link SampleService}.
	 * @param analysisSampleUpdatorServices
	 *            A list of {@link AnalysisSampleUpdator}s to use for updating
	 *            samples.
	 */
	@Autowired
	public AnalysisSubmissionSampleProcessorImpl(SampleRepository sampleRepository,
			List<AnalysisSampleUpdator> analysisSampleUpdatorServices) {
		checkNotNull(analysisSampleUpdatorServices, "assemblySampleUpdatorService is null");
		this.sampleRepository = sampleRepository;
		this.analysisSampleUpdatorMap = Maps.newHashMap();

		for (AnalysisSampleUpdator analysisSampleUpdatorService : analysisSampleUpdatorServices) {
			AnalysisType analysisType = analysisSampleUpdatorService.getAnalysisType();
			checkArgument(!analysisSampleUpdatorMap.containsKey(analysisType),
					"Error: already have registered " + analysisSampleUpdatorService.getClass() + " for AnalysisType " + analysisType);

			analysisSampleUpdatorMap.put(analysisSampleUpdatorService.getAnalysisType(), analysisSampleUpdatorService);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@PreAuthorize("hasPermission(#analysisSubmission, 'canUpdateSamplesFromAnalysisSubmission')")
	@RunAsUser("#analysisSubmission.getSubmitter()")
	@Transactional
	public void updateSamples(AnalysisSubmission analysisSubmission) {
		if (!analysisSubmission.getUpdateSamples()) {
			logger.trace("Will not update samples from results for submission=" + analysisSubmission);
		} else {
			logger.debug("Updating sample from results for submission=" + analysisSubmission);

			Set<Sample> samples = sampleRepository.findSamplesForAnalysisSubmission(analysisSubmission.getId());
			Analysis analysis = analysisSubmission.getAnalysis();

			checkNotNull(analysis, "No analysis associated with submission " + analysisSubmission);
			checkNotNull(samples, "No samples associated with submission " + analysisSubmission);

			AnalysisSampleUpdator analysisSampleUpdatorService = analysisSampleUpdatorMap
					.get(analysis.getAnalysisType());

			if (analysisSampleUpdatorService != null) {
				analysisSampleUpdatorService.update(samples, analysisSubmission);
			} else {
				logger.debug(
						"No associated object for updating samples for analysis of type " + analysis.getAnalysisType());
			}
		}
	}
}