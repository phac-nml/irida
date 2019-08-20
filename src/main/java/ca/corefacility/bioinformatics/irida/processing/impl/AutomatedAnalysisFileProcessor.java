package ca.corefacility.bioinformatics.irida.processing.impl;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AbstractAnalysisSubmission;
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
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * File processor used to launch an automated analysis for uploaded data.  This will take the {@link
 * AnalysisSubmissionTemplate}s for a {@link Project} and convert them to {@link AnalysisSubmission}s and submit them.
 */
@Component
public class AutomatedAnalysisFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(AutomatedAnalysisFileProcessor.class);

	private static final SimpleDateFormat LAUNCHED_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private final SampleSequencingObjectJoinRepository ssoRepository;
	private final ProjectSampleJoinRepository psjRepository;
	private final AnalysisSubmissionRepository submissionRepository;
	private final AnalysisSubmissionTemplateRepository analysisTemplateRepository;
	private final ProjectAnalysisSubmissionJoinRepository pasRepository;
	private final IridaWorkflowsService workflowsService;
	private final SequencingObjectRepository objectRepository;
	private final MessageSource messageSource;

	@Autowired
	public AutomatedAnalysisFileProcessor(SampleSequencingObjectJoinRepository ssoRepository,
			ProjectSampleJoinRepository psjRepository, AnalysisSubmissionRepository submissionRepository,
			AnalysisSubmissionTemplateRepository analysisTemplateRepository,
			ProjectAnalysisSubmissionJoinRepository pasRepository, IridaWorkflowsService workflowsService,
			SequencingObjectRepository objectRepository, MessageSource messageSource) {
		this.ssoRepository = ssoRepository;
		this.psjRepository = psjRepository;
		this.submissionRepository = submissionRepository;
		this.analysisTemplateRepository = analysisTemplateRepository;
		this.pasRepository = pasRepository;
		this.workflowsService = workflowsService;
		this.objectRepository = objectRepository;
		this.messageSource = messageSource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process(SequencingObject sequencingObject) {

		SampleSequencingObjectJoin sampleForSequencingObject = ssoRepository.getSampleForSequencingObject(
				sequencingObject);

		/*
		 * Checking if the seq object was deleted from the sample before this file processor was run
		 */
		if (sampleForSequencingObject != null) {

			//get the sample name
			String sampleName = sampleForSequencingObject.getSubject()
					.getSampleName();

			//get all the projects for this sample
			List<Join<Project, Sample>> projectsForSample = psjRepository.getProjectForSample(
					sampleForSequencingObject.getSubject());

			//for each project
			for (Join<Project, Sample> j : projectsForSample) {
				//get the analysis templates for this project
				List<AnalysisSubmissionTemplate> analysisTemplates = getAnalysisTemplatesForProject(
						(ProjectSampleJoin) j);

				Project project = j.getSubject();

				//for each analysis template
				for (AnalysisSubmissionTemplate template : analysisTemplates) {

					//check that the template is for the current version of the workflow.  If not it'll be disabled.
					template = checkCurrentWorkflowVersion(template);

					//ensure the template is enabled
					if (template.isEnabled()) {
						// build an SubmittableAnalysisSubmission
						AnalysisSubmission.Builder builder = new AnalysisSubmission.Builder(template);

						//adding the sample name to the template
						String templateName = template.getName();
						templateName += " - " + sampleName;
						builder.name(templateName);

						//set the analysis priority to the setting for the project
						builder.priority(project.getAnalysisPriority());

						//build the submission and save it
						AnalysisSubmission submission = builder.inputFiles(Sets.newHashSet(sequencingObject))
								.build();
						submission = submissionRepository.save(submission);

						//share submission back to the project
						pasRepository.save(new ProjectAnalysisSubmissionJoin(project, submission));

						//check if we have to do any legacy updates
						legacyFileProcessorCompatibility(submission, sequencingObject);

						//set the status message for the template
						String date = LAUNCHED_DATE_FORMAT.format(new Date());
						String message = messageSource.getMessage("analysis.template.status.lastlaunched",
								new Object[] { date }, Locale.getDefault());
						template.setStatusMessage(message);

						//re-save the template
						analysisTemplateRepository.save(template);
					}
				}

			}
		} else {
			logger.warn("Cannot find sample for sequencing object " + sequencingObject.getId()
					+ ".  Not running automated pipelines.");
		}
	}

	/**
	 * Get all the analysis templates for the given project-sample join
	 *
	 * @param projectSampleJoin the relationship between the project and sample
	 * @return a list of the analysis templates for the project
	 */
	private List<AnalysisSubmissionTemplate> getAnalysisTemplatesForProject(ProjectSampleJoin projectSampleJoin) {
		List<AnalysisSubmissionTemplate> analysisSubmissionTemplatesForProject = analysisTemplateRepository.getEnabledAnalysisSubmissionTemplatesForProject(
				projectSampleJoin.getSubject());

		//check if the project owns this sample
		boolean owner = projectSampleJoin.isOwner();

		analysisSubmissionTemplatesForProject.forEach(t -> {
			//don't try to update the sample if this project isn't the owner.  it'll fail when it tries.
			if (!owner) {
				t.setUpdateSamples(false);
			}
		});

		return analysisSubmissionTemplatesForProject;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean modifiesFile() {
		return false;
	}

	/**
	 * Check the given {@link AnalysisSubmissionTemplate} to see if it's the current version of the workflow.  If it
	 * isn't, it should be disabled as arguments may not line up.
	 *
	 * @param template the {@link AnalysisSubmissionTemplate} to check
	 * @return the same {@link AnalysisSubmissionTemplate}, but disabled and updated if it shouldn't be run.
	 */
	private AnalysisSubmissionTemplate checkCurrentWorkflowVersion(AnalysisSubmissionTemplate template) {
		UUID workflowId = template.getWorkflowId();

		//get the workflow for the template
		IridaWorkflow iridaWorkflow = null;
		try {
			iridaWorkflow = workflowsService.getIridaWorkflow(workflowId);

		} catch (IridaWorkflowNotFoundException e) {
			//if the workflow doesn't exist
			logger.warn("Project " + template.getSubmittedProject()
					.getId() + " attempted to run workflow " + workflowId
					+ " but it does not exist.  This template will be disabled.", e);

			//disable the template and set a message in the status
			template.setEnabled(false);
			String message = messageSource.getMessage("analysis.template.status.notexists", null, Locale.getDefault());
			template.setStatusMessage(message);

			analysisTemplateRepository.save(template);
		}

		//if we have a workflow
		if (iridaWorkflow != null) {
			AnalysisType analysisType = iridaWorkflow.getWorkflowDescription()
					.getAnalysisType();

			//check that the workflow has a default version
			IridaWorkflow defaultWorkflow = null;
			try {
				defaultWorkflow = workflowsService.getDefaultWorkflowByType(analysisType);
			} catch (IridaWorkflowNotFoundException e) {
				//if no default
				logger.warn("Project " + template.getSubmittedProject()
						.getId() + " attempted to run workflow type " + analysisType.getType()
						+ " but there is no default workflow for this type.  This template will be disabled.", e);

				//disable the template and set a message in the status
				template.setEnabled(false);
				String message = messageSource.getMessage("analysis.template.status.nodefault", null,
						Locale.getDefault());
				template.setStatusMessage(message);

				analysisTemplateRepository.save(template);
			}

			//if we have a default but it's not the same as the template's workflow
			if (defaultWorkflow != null && !workflowId.equals(defaultWorkflow.getWorkflowIdentifier())) {
				logger.warn("Project " + template.getSubmittedProject()
						.getId() + " attempted to run workflow " + workflowId
						+ " but is no longer the default workflow for this type.  This template will be disabled.");

				//disable the template and set a message in the status
				template.setEnabled(false);
				String message = messageSource.getMessage("analysis.template.status.outdated", null,
						Locale.getDefault());
				template.setStatusMessage(message);

				analysisTemplateRepository.save(template);
			}

		}

		return template;
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
