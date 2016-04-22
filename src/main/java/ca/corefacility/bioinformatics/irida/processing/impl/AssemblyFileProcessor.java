package ca.corefacility.bioinformatics.irida.processing.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission.Builder;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.project.ProjectSampleJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequencingObjectJoinRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.collect.Sets;

public class AssemblyFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(AssemblyFileProcessor.class);

	SequencingObjectRepository objectRepository;
	SampleSequencingObjectJoinRepository ssoRepository;
	ProjectSampleJoinRepository psjRepository;
	AnalysisSubmissionRepository submissionRepository;
	UserRepository userRepository;
	IridaWorkflowsService workflowsService;

	public AssemblyFileProcessor(SequencingObjectRepository objectRepository,
			AnalysisSubmissionRepository submissionRepository, IridaWorkflowsService workflowsService,
			UserRepository userRepository, SampleSequencingObjectJoinRepository ssoRepository,
			ProjectSampleJoinRepository psjRepository) {
		this.objectRepository = objectRepository;
		this.submissionRepository = submissionRepository;
		this.workflowsService = workflowsService;
		this.userRepository = userRepository;
		this.ssoRepository = ssoRepository;
		this.psjRepository = psjRepository;
	}

	@Override
	public void process(Long sequenceFileId) throws FileProcessorException {
		SequencingObject sequencingObject = objectRepository.findOne(sequenceFileId);

		if (shouldAssemble(sequencingObject)) {
			logger.debug("Setting up automated assembly for sequence " + sequencingObject.getId());
			User admin = userRepository.loadUserByUsername("admin");

			if (sequencingObject instanceof SequenceFilePair) {
				IridaWorkflow defaultWorkflowByType;
				try {
					defaultWorkflowByType = workflowsService.getDefaultWorkflowByType(AnalysisType.ASSEMBLY_ANNOTATION);
				} catch (IridaWorkflowNotFoundException e) {
					throw new FileProcessorException("Cannot find assembly workflow", e);
				}

				UUID pipelineUUID = defaultWorkflowByType.getWorkflowIdentifier();

				Builder builder = new AnalysisSubmission.Builder(pipelineUUID);
				AnalysisSubmission submission = builder
						.inputFilesPaired(Sets.newHashSet((SequenceFilePair) sequencingObject))
						.name("Automated Assembly " + sequencingObject.toString()).build();
				submission.setSubmitter(admin);

				submission = submissionRepository.save(submission);

				sequencingObject.setAutomatedAssembly(submission);

				objectRepository.save(sequencingObject);

				logger.debug("Automated assembly submission created: " + submission.getId());
			} else {
				logger.warn("Could not assemble sequencing object " + sequencingObject.getId()
						+ " because it's not paired end");
			}
		}
	}

	@Override
	public Boolean modifiesFile() {
		return false;
	}

	private boolean shouldAssemble(SequencingObject object) {
		boolean assemble = false;

		SampleSequencingObjectJoin sampleForSequencingObject = ssoRepository.getSampleForSequencingObject(object);

		/*
		 * This is something that should only ever happen in tests, but added
		 * check with a warning
		 */
		if (sampleForSequencingObject != null) {
			List<Join<Project, Sample>> projectForSample = psjRepository.getProjectForSample(sampleForSequencingObject
					.getSubject());

			assemble = projectForSample.stream().anyMatch(j -> j.getSubject().getAssembleUploads());
		} else {
			logger.warn("Cannot find sample for sequencing object.  Not assembling");
		}

		return assemble;
	}

}
