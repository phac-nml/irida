package ca.corefacility.bioinformatics.irida.processing.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission.Builder;
import ca.corefacility.bioinformatics.irida.processing.FileProcessor;
import ca.corefacility.bioinformatics.irida.processing.FileProcessorException;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequencingObjectRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisSubmissionService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

public class AssemblyFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(AssemblyFileProcessor.class);

	SequencingObjectRepository objectRepository;
	AnalysisSubmissionRepository submissionRepository;
	UserRepository userRepository;
	IridaWorkflowsService workflowsService;

	public AssemblyFileProcessor(SequencingObjectRepository objectRepository,
			AnalysisSubmissionRepository submissionRepository, IridaWorkflowsService workflowsService, UserRepository userRepository) {
		this.objectRepository = objectRepository;
		this.submissionRepository = submissionRepository;
		this.workflowsService = workflowsService;
		this.userRepository = userRepository;
	}

	@Override
	public void process(Long sequenceFileId) throws FileProcessorException {
		logger.debug("Getting file object");
		SequencingObject sequencingObject = objectRepository.findOne(sequenceFileId);

		User admin = userRepository.loadUserByUsername("admin");
		
		logger.debug("Getting workflow");
		UUID pipelineUUID = UUID.fromString("f73cbfd2-5478-4c19-95f9-690f3712f84d");

		IridaWorkflow iridaWorkflow = null;
		try {
			iridaWorkflow = workflowsService.getIridaWorkflow(pipelineUUID);
		} catch (IridaWorkflowNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Builder builder = new AnalysisSubmission.Builder(pipelineUUID);
		AnalysisSubmission submission = builder.inputFilesPaired(Sets.newHashSet((SequenceFilePair) sequencingObject))
				.name("testSubmission").build();
		submission.setSubmitter(admin);

		logger.debug("Creating submission");
		submissionRepository.save(submission);

		logger.debug("all done");
	}

	@Override
	public Boolean modifiesFile() {
		return false;
	}

}
