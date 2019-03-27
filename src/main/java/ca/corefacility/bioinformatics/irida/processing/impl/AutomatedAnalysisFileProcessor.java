package ca.corefacility.bioinformatics.irida.processing.impl;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmissionTemplate;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.ProjectAnalysisSubmissionJoin;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionTemplateRepository;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.ProjectAnalysisSubmissionJoinRepository;
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

/**
 * File processor used to launch an automated analysis for uploaded data.  This will take the {@link
 * AnalysisSubmissionTemplate}s for a {@link Project} and convert them to {@link AnalysisSubmission}s and submit them.
 */
@Component
public class AutomatedAnalysisFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(AutomatedAnalysisFileProcessor.class);

	private final SampleSequencingObjectJoinRepository ssoRepository;
	private final ProjectSampleJoinRepository psjRepository;
	private final AnalysisSubmissionRepository submissionRepository;
	private final AnalysisSubmissionTemplateRepository analysisTemplateRepository;
	private final ProjectAnalysisSubmissionJoinRepository pasRepository;
	private final IridaWorkflowsService workflowsService;
	private final SequencingObjectRepository objectRepository;

	@Autowired
	public AutomatedAnalysisFileProcessor(SampleSequencingObjectJoinRepository ssoRepository,
			ProjectSampleJoinRepository psjRepository, AnalysisSubmissionRepository submissionRepository,
			AnalysisSubmissionTemplateRepository analysisTemplateRepository,
			ProjectAnalysisSubmissionJoinRepository pasRepository, IridaWorkflowsService workflowsService,
			SequencingObjectRepository objectRepository) {
		this.ssoRepository = ssoRepository;
		this.psjRepository = psjRepository;
		this.submissionRepository = submissionRepository;
		this.analysisTemplateRepository = analysisTemplateRepository;
		this.pasRepository = pasRepository;
		this.workflowsService = workflowsService;
		this.objectRepository = objectRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(SequencingObject sequencingObject) {
		List<AnalysisSubmissionTemplate> analysisTemplates = getAnalysisTemplates(sequencingObject);

		for (AnalysisSubmissionTemplate template : analysisTemplates) {

			// build an SubmittableAnalysisSubmission
			AnalysisSubmission.Builder builder = new AnalysisSubmission.Builder(template);

			AnalysisSubmission submission = builder.inputFiles(Sets.newHashSet(sequencingObject))
					.build();

			submission = submissionRepository.save(submission);

			//share submission back to the project
			Project project = template.getSubmittedProject();
			pasRepository.save(new ProjectAnalysisSubmissionJoin(project, submission));

			legacyFileProcessorCompatibility(submission, sequencingObject);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean modifiesFile() {
		return false;
	}

	/**
	 * Get the {@link AnalysisSubmissionTemplate}s for a given {@link SequencingObject}.  Search up the {@link Sample}
	 * and {@link Project}s the sequence belongs to.
	 *
	 * @param object the {@link SequencingObject}
	 * @return the List of {@link AnalysisSubmissionTemplate}
	 */
	private List<AnalysisSubmissionTemplate> getAnalysisTemplates(SequencingObject object) {
		List<AnalysisSubmissionTemplate> submissionTemplates = new ArrayList<>();

		SampleSequencingObjectJoin sampleForSequencingObject = ssoRepository.getSampleForSequencingObject(object);

		/*
		 * Checking if the seq object was deleted from the sample before this file processor was run
		 */
		if (sampleForSequencingObject != null) {
			List<Join<Project, Sample>> projectForSample = psjRepository.getProjectForSample(
					sampleForSequencingObject.getSubject());

			//get all the projects for the sample
			for (Join<Project, Sample> j : projectForSample) {

				//get the analysis templates for that project
				List<AnalysisSubmissionTemplate> analysisSubmissionTemplatesForProject = analysisTemplateRepository.getAnalysisSubmissionTemplatesForProject(
						j.getSubject());

				//check if the project owns this sample
				ProjectSampleJoin psj = (ProjectSampleJoin) j;
				boolean owner = psj.isOwner();

				analysisSubmissionTemplatesForProject.forEach(t -> {
					//adding the sample name to the template
					String name = t.getName();
					name = name + " - " + j.getObject()
							.getSampleName();
					t.setName(name);

					//don't try to update the sample if this project isn't the owner.  it'll fail when it tries.
					if (!owner) {
						t.setUpdateSamples(false);
					}
				});

				submissionTemplates.addAll(analysisSubmissionTemplatesForProject);
			}

		} else {
			logger.warn("Cannot find sample for sequencing object " + object.getId()
					+ ".  Not running automated pipelines.");
		}

		return submissionTemplates;
	}

	/**
	 * Do the work the old Assembly and SISTR file processors used to do and assign the assembly and SISTR result back
	 * to the {@link SequencingObject}.  This will only do something if its an Assembly or SISTR analysis and if
	 * updateSamples is true.
	 *
	 * @param submission       the {@link AnalysisSubmission} to check
	 * @param sequencingObject the {@link SequencingObject} to apply results to.
	 */
	private void legacyFileProcessorCompatibility(AnalysisSubmission submission, SequencingObject sequencingObject) {
		if (submission.getUpdateSamples()) {
			try {
				IridaWorkflow assemblyWorkflow = workflowsService.getDefaultWorkflowByType(
						BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION);
				IridaWorkflow sistrWorkflow = workflowsService.getDefaultWorkflowByType(
						BuiltInAnalysisTypes.SISTR_TYPING);

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
				logger.error("Could not associate automated workflow with analysis " + submission.getIdentifier(), e);
			}
		}
	}
}
