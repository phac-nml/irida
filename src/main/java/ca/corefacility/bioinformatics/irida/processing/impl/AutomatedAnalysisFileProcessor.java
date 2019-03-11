package ca.corefacility.bioinformatics.irida.processing.impl;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmissionTemplate;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionTemplateRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class AutomatedAnalysisFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(AutomatedAnalysisFileProcessor.class);

	private SampleSequencingObjectJoinRepository ssoRepository;
	private ProjectSampleJoinRepository psjRepository;
	private AnalysisSubmissionRepository submissionRepository;
	private AnalysisSubmissionTemplateRepository analysisTemplateRepository;
	private IridaWorkflowsService workflowsService;
	private SequencingObjectRepository objectRepository;

	@Autowired
	public AutomatedAnalysisFileProcessor(SampleSequencingObjectJoinRepository ssoRepository,
			ProjectSampleJoinRepository psjRepository, AnalysisSubmissionRepository submissionRepository,
			AnalysisSubmissionTemplateRepository analysisTemplateRepository, IridaWorkflowsService workflowsService,
			SequencingObjectRepository objectRepository) {
		this.ssoRepository = ssoRepository;
		this.psjRepository = psjRepository;
		this.submissionRepository = submissionRepository;
		this.analysisTemplateRepository = analysisTemplateRepository;
		this.workflowsService = workflowsService;
		this.objectRepository = objectRepository;
	}

	@Override
	public void process(SequencingObject sequencingObject) {
		List<AnalysisSubmissionTemplate> analysisTemplates = getAnalysisTemplates(sequencingObject);

		for (AnalysisSubmissionTemplate template : analysisTemplates) {

			// build an SubmittableAnalysisSubmission
			AnalysisSubmission.Builder builder = new AnalysisSubmission.Builder(template);

			AnalysisSubmission submission = builder.inputFiles(Sets.newHashSet(sequencingObject))
					.build();
			submission.setSubmitter(template.getSubmitter());

			submission = submissionRepository.save(submission);

			legacyFileProcessorCompatibility(submission, sequencingObject);
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

	private void legacyFileProcessorCompatibility(AnalysisSubmission submission, SequencingObject sequencingObject) {
		try {
			IridaWorkflow assemblyWorkflow = workflowsService.getDefaultWorkflowByType(
					BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION);
			IridaWorkflow sistrWorkflow = workflowsService.getDefaultWorkflowByType(BuiltInAnalysisTypes.SISTR_TYPING);

			UUID assemblyWorkflowWorkflowIdentifier = assemblyWorkflow.getWorkflowIdentifier();
			UUID sistrWorkflowWorkflowIdentifier = sistrWorkflow.getWorkflowIdentifier();

			if (submission.getWorkflowId()
					.equals(assemblyWorkflowWorkflowIdentifier)) {
				// Associate the assembly submission with the seqobject
				sequencingObject.setAutomatedAssembly(submission);

				objectRepository.save(sequencingObject);
			} else if (submission.getWorkflowId()
					.equals(sistrWorkflowWorkflowIdentifier)) {
				// Associate the sistr submission with the seqobject
				sequencingObject.setSistrTyping(submission);

				objectRepository.save(sequencingObject);
			}

		} catch (IridaWorkflowNotFoundException e) {
			logger.error("Could not associate assembly workflow with analysis " + submission.getIdentifier(), e);
		}
	}
}
