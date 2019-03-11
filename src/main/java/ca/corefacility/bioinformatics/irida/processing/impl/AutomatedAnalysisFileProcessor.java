package ca.corefacility.bioinformatics.irida.processing.impl;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmissionTemplate;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionTemplateRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AutomatedAnalysisFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(AutomatedAnalysisFileProcessor.class);

	private SampleSequencingObjectJoinRepository ssoRepository;
	private ProjectSampleJoinRepository psjRepository;
	private AnalysisSubmissionRepository submissionRepository;
	private AnalysisSubmissionTemplateRepository analysisTemplateRepository;

	@Autowired
	public AutomatedAnalysisFileProcessor(SampleSequencingObjectJoinRepository ssoRepository,
			ProjectSampleJoinRepository psjRepository, AnalysisSubmissionRepository submissionRepository,
			AnalysisSubmissionTemplateRepository analysisTemplateRepository) {
		this.ssoRepository = ssoRepository;
		this.psjRepository = psjRepository;
		this.submissionRepository = submissionRepository;
		this.analysisTemplateRepository = analysisTemplateRepository;
	}

	@Override
	public void process(SequencingObject sequencingObject) {
		List<AnalysisSubmissionTemplate> analysisTemplates = getAnalysisTemplates(sequencingObject);

		for (AnalysisSubmissionTemplate template : analysisTemplates) {

			// build an SubmittableAnalysisSubmission
			AnalysisSubmission.Builder builder = new AnalysisSubmission.Builder(template.getWorkflowId());
			AnalysisSubmission submission = builder.inputFiles(Sets.newHashSet(sequencingObject))
					.priority(template.getPriority())
					.name(template.getName())
					.analysisDescription(template.getAnalysisDescription())
					.updateSamples(template.getUpdateSamples())
					.build();
			submission.setSubmitter(template.getSubmitter());

			submission = submissionRepository.save(submission);
		}
	}

	@Override
	public Boolean modifiesFile() {
		return false;
	}

	private List<AnalysisSubmissionTemplate> getAnalysisTemplates(SequencingObject object) {
		List<AnalysisSubmissionTemplate> submissionTemplates = new ArrayList<>();

		SampleSequencingObjectJoin sampleForSequencingObject = ssoRepository.getSampleForSequencingObject(object);

		/*
		 * This is something that should only ever happen in tests, but added
		 * check with a warning
		 */
		if (sampleForSequencingObject != null) {
			List<Join<Project, Sample>> projectForSample = psjRepository.getProjectForSample(
					sampleForSequencingObject.getSubject());

			for (Join<Project, Sample> j : projectForSample) {
				List<AnalysisSubmissionTemplate> analysisSubmissionTemplatesForProject = analysisTemplateRepository.getAnalysisSubmissionTemplatesForProject(
						j.getSubject());

				submissionTemplates.addAll(analysisSubmissionTemplatesForProject);
			}

		} else {
			logger.warn("Cannot find sample for sequencing object.  Not assembling");
		}

		return submissionTemplates;
	}
}
