package ca.corefacility.bioinformatics.irida.processing.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission.Builder;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

import com.google.common.collect.Sets;

public class AssemblyFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(AssemblyFileProcessor.class);

	SequencingObjectRepository objectRepository;
	AnalysisSubmissionRepository submissionRepository;
	UserRepository userRepository;
	IridaWorkflowsService workflowsService;

	public AssemblyFileProcessor(SequencingObjectRepository objectRepository,
			AnalysisSubmissionRepository submissionRepository, IridaWorkflowsService workflowsService,
			UserRepository userRepository) {
		this.objectRepository = objectRepository;
		this.submissionRepository = submissionRepository;
		this.workflowsService = workflowsService;
		this.userRepository = userRepository;
	}

	@Override
	public void process(Long sequenceFileId) throws FileProcessorException {
		logger.trace("Setting up automated assembly");
		SequencingObject sequencingObject = objectRepository.findOne(sequenceFileId);

		User admin = userRepository.loadUserByUsername("admin");

		IridaWorkflow defaultWorkflowByType;
		try {
			defaultWorkflowByType = workflowsService.getDefaultWorkflowByType(AnalysisType.ASSEMBLY_ANNOTATION);
		} catch (IridaWorkflowNotFoundException e) {
			throw new FileProcessorException("Cannot find assembly workflow", e);
		}

		UUID pipelineUUID = defaultWorkflowByType.getWorkflowIdentifier();

		Builder builder = new AnalysisSubmission.Builder(pipelineUUID);
		AnalysisSubmission submission = builder.inputFilesPaired(Sets.newHashSet((SequenceFilePair) sequencingObject))
				.name("Automated Assembly").build();
		submission.setSubmitter(admin);

		submission = submissionRepository.save(submission);

		sequencingObject.setAutomatedAssembly(submission);

		objectRepository.save(sequencingObject);

		logger.trace("Submission created: " + submission.getId());
	}

	@Override
	public Boolean modifiesFile() {
		return false;
	}

}
