package ca.corefacility.bioinformatics.irida.processing.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
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

/**
 * File processor which launches an automated assembly and annotation pipeline
 * on uploaded sequences. It will check with a sequence's associated project for
 * whether or not it should assemble.
 */
@Component
public class AssemblyFileProcessor implements FileProcessor {
	private static final Logger logger = LoggerFactory.getLogger(AssemblyFileProcessor.class);

	private final SequencingObjectRepository objectRepository;
	private final SampleSequencingObjectJoinRepository ssoRepository;
	private final ProjectSampleJoinRepository psjRepository;
	private final AnalysisSubmissionRepository submissionRepository;
	private final UserRepository userRepository;
	private final IridaWorkflowsService workflowsService;

	@Autowired
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void process(SequencingObject sequencingObject) {
		logger.debug("Setting up automated assembly for sequence " + sequencingObject.getId());

		// assembly run by admin
		User admin = userRepository.loadUserByUsername("admin");

		// Ensure it's a paired upload. Single end can't currently be
		// assembled
		if (sequencingObject instanceof SequenceFilePair) {
			IridaWorkflow defaultWorkflowByType;

			// get the workflow
			try {
				defaultWorkflowByType = workflowsService.getDefaultWorkflowByType(BuiltInAnalysisTypes.ASSEMBLY_ANNOTATION);
			} catch (IridaWorkflowNotFoundException e) {
				throw new FileProcessorException("Cannot find assembly workflow", e);
			}

			UUID pipelineUUID = defaultWorkflowByType.getWorkflowIdentifier();

			// build an AnalysisSubmission
			Builder builder = new AnalysisSubmission.Builder(pipelineUUID);
			AnalysisSubmission submission = builder
					.inputFiles(Sets.newHashSet((SequenceFilePair) sequencingObject))
					.priority(AnalysisSubmission.Priority.LOW)
					.name("Automated Assembly " + sequencingObject.toString())
					.updateSamples(true).build();
			submission.setSubmitter(admin);

			submission = submissionRepository.save(submission);

			// Associate the submission with the seqobject
			sequencingObject.setAutomatedAssembly(submission);

			objectRepository.save(sequencingObject);

			logger.debug("Automated assembly submission created for sequencing object " + sequencingObject.getId());
		} else {
			logger.warn("Could not assemble sequencing object " + sequencingObject.getId()
					+ " because it's not paired end");
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
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public boolean shouldProcessFile(Long sequencingObjectId) {
		SequencingObject sequencingObject = objectRepository.findOne(sequencingObjectId);

		// Check if the object should be assembled
		if (shouldAssemble(sequencingObject)) {
			return true;
		}

		return false;
	}

	/**
	 * Check whether any {@link Project} associated with the
	 * {@link SequencingObject} is set to assemble
	 * 
	 * @param object
	 *            {@link SequencingObject} to check ot assemble
	 * @return true if it should assemble, false otherwise
	 */
	private boolean shouldAssemble(SequencingObject object) {
		boolean assemble = false;

		SampleSequencingObjectJoin sampleForSequencingObject = ssoRepository.getSampleForSequencingObject(object);

		/*
		 * This is something that should only ever happen in tests, but added
		 * check with a warning
		 */
		if (sampleForSequencingObject != null) {
			List<Join<Project, Sample>> projectForSample = psjRepository
					.getProjectForSample(sampleForSequencingObject.getSubject());

			assemble = projectForSample.stream().anyMatch(j -> j.getSubject().getAssembleUploads());
		} else {
			logger.warn("Cannot find sample for sequencing object.  Not assembling");
		}

		return assemble;
	}

}
