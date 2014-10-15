package ca.corefacility.bioinformatics.irida.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.workflow.RemoteWorkflowRepository;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.galaxy.phylogenomics.impl.AnalysisExecutionServicePhylogenomics;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Stores common code for integration tests that attempt to run analyses in
 * Galaxy. This includes code for setup of sequence files in a database and
 * waiting for submission to complete in Galaxy.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class AnalysisExecutionGalaxyITService {

	@Autowired
	private RemoteWorkflowRepository remoteWorkflowRepository;

	@Autowired
	private ReferenceFileRepository referenceFileRepository;

	@Autowired
	private SequenceFileService seqeunceFileService;

	@Autowired
	private SampleService sampleService;

	@Autowired
	private AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics;

	@Autowired
	private AnalysisSubmissionService analysisSubmissionService;

	@Autowired
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	/**
	 * Builds a new AnalysisExecutionGalaxyITService with the given
	 * services/repositories.
	 * 
	 * @param remoteWorkflowRepository
	 * @param referenceFileRepository
	 * @param seqeunceFileService
	 * @param sampleService
	 * @param analysisExecutionServicePhylogenomics
	 * @param analysisSubmissionService
	 * @param analysisSubmissionRepsitory
	 */
	public AnalysisExecutionGalaxyITService(
			RemoteWorkflowRepository remoteWorkflowRepository,
			ReferenceFileRepository referenceFileRepository,
			SequenceFileService seqeunceFileService,
			SampleService sampleService,
			AnalysisExecutionServicePhylogenomics analysisExecutionServicePhylogenomics,
			AnalysisSubmissionService analysisSubmissionService,
			AnalysisSubmissionRepository analysisSubmissionRepository) {
		super();
		this.remoteWorkflowRepository = remoteWorkflowRepository;
		this.referenceFileRepository = referenceFileRepository;
		this.seqeunceFileService = seqeunceFileService;
		this.sampleService = sampleService;
		this.analysisExecutionServicePhylogenomics = analysisExecutionServicePhylogenomics;
		this.analysisSubmissionService = analysisSubmissionService;
		this.analysisSubmissionRepository = analysisSubmissionRepository;
	}

	/**
	 * Sets up an AnalysisSubmission and saves all dependencies (except the
	 * workflow) in database.
	 * 
	 * @param sampleId
	 *            The id of the sample to associate with the given sequence
	 *            file.
	 * @param sequenceFilePath
	 *            The path to an input sequence file for this test.
	 * @param referenceFilePath
	 *            The path to an input reference file for this test.
	 * @param remoteWorkflow
	 *            A remote workflow to execute for this test. This is assumed to
	 *            already exist in the database.
	 * @return An AnalysisSubmissionPhylogenomics which has been saved to the
	 *         database.
	 */
	public AnalysisSubmissionPhylogenomics setupSubmissionInDatabaseNoWorkflowSave(
			long sampleId, Path sequenceFilePath, Path referenceFilePath,
			RemoteWorkflowPhylogenomics remoteWorkflow) {

		SequenceFile sequenceFile = setupSampleSequenceFileInDatabase(sampleId, sequenceFilePath);

		Set<SequenceFile> sequenceFiles = new HashSet<>();
		sequenceFiles.add(sequenceFile);

		ReferenceFile referenceFile = referenceFileRepository
				.save(new ReferenceFile(referenceFilePath));

		AnalysisSubmission submission = analysisSubmissionService
				.create(new AnalysisSubmissionPhylogenomics("my analysis", sequenceFiles,
						referenceFile, remoteWorkflow));

		return analysisSubmissionRepository.getByType(submission.getId(),
				AnalysisSubmissionPhylogenomics.class);
	}
	
	/**
	 * Attaches the given sequence file path to a particular sample id.
	 * @param sampleId  The id of the sample to attach a sequence file to.
	 * @param sequenceFilePath  The path of the sequence file to attach.
	 * @return  A SequenceFile object with the given sequence file path attached and saved in the database.
	 */
	public SequenceFile setupSampleSequenceFileInDatabase(long sampleId, Path sequenceFilePath) {
		Sample sample = sampleService.read(sampleId);
		Join<Sample, SequenceFile> sampleSeqFile = seqeunceFileService
				.createSequenceFileInSample(new SequenceFile(sequenceFilePath),
						sample);
		SequenceFile sequenceFile = sampleSeqFile.getObject();
		return sequenceFile;
	}

	/**
	 * Sets up an AnalysisSubmission and saves all dependencies in database.
	 * 
	 * @param sampleId
	 *            The id of the sample to associate with the given sequence
	 *            file.
	 * @param sequenceFilePath
	 *            The path to an input sequence file for this test.
	 * @param referenceFilePath
	 *            The path to an input reference file for this test.
	 * @param remoteWorkflow
	 *            A remote workflow to execute for this test.
	 * @return An AnalysisSubmissionPhylogenomics which has been saved to the
	 *         database.
	 */
	public AnalysisSubmissionPhylogenomics setupSubmissionInDatabase(
			long sampleId, Path sequenceFilePath, Path referenceFilePath,
			RemoteWorkflowPhylogenomics remoteWorkflow) {

		RemoteWorkflowPhylogenomics remoteWorkflowSaved = remoteWorkflowRepository
				.save(remoteWorkflow);

		return setupSubmissionInDatabaseNoWorkflowSave(sampleId,
				sequenceFilePath, referenceFilePath, remoteWorkflowSaved);
	}

	/**
	 * Wait for the given analysis submission to be complete.
	 * 
	 * @param analysisSubmission
	 *            The analysis submission to wait for.
	 * @throws Exception
	 */
	public void waitUntilSubmissionComplete(
			AnalysisSubmissionPhylogenomics analysisSubmission)
			throws Exception {
		final int totalSecondsWait = 1 * 60; // 1 minute

		WorkflowStatus workflowStatus;

		long timeBefore = System.currentTimeMillis();
		do {
			workflowStatus = analysisExecutionServicePhylogenomics
					.getWorkflowStatus(analysisSubmission);

			long timeAfter = System.currentTimeMillis();
			double deltaSeconds = (timeAfter - timeBefore) / 1000.0;
			if (deltaSeconds <= totalSecondsWait) {
				Thread.sleep(2000);
			} else {
				throw new Exception("Timeout for submission "
						+ analysisSubmission.getRemoteAnalysisId() + " "
						+ deltaSeconds + "s > " + totalSecondsWait + "s");
			}
		} while (!WorkflowState.OK.equals(workflowStatus.getState()));
	}

	/**
	 * Asserts that the given status is in a valid state for a workflow.
	 * 
	 * @param status
	 */
	public void assertValidStatus(WorkflowStatus status) {
		assertNotNull("WorkflowStatus is null", status);
		assertFalse("WorkflowState is " + WorkflowState.UNKNOWN,
				WorkflowState.UNKNOWN.equals(status.getState()));
		float percentComplete = status.getPercentComplete();
		assertTrue("percentComplete not in range of 0 to 100",
				0.0f <= percentComplete && percentComplete <= 100.0f);
	}
}
