package ca.corefacility.bioinformatics.irida.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.WorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Stores common code for integration tests that require special database setup
 * code. This includes code for setup of sequence files in a database and
 * waiting for submission to complete in Galaxy.
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class DatabaseSetupGalaxyITService {

	private final ExecutorService executor = Executors.newFixedThreadPool(1);

	private ReferenceFileRepository referenceFileRepository;
	private SequenceFileService seqeunceFileService;
	private SampleService sampleService;
	private AnalysisExecutionService analysisExecutionServiceSimplified;
	private AnalysisSubmissionService analysisSubmissionService;
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	/**
	 * Builds a new AnalysisExecutionGalaxyITService with the given
	 * services/repositories.
	 * 
	 * @param referenceFileRepository
	 * @param seqeunceFileService
	 * @param sampleService
	 * @param analysisExecutionServiceSimplified
	 * @param analysisSubmissionService
	 * @param analysisSubmissionRepsitory
	 */
	public DatabaseSetupGalaxyITService(ReferenceFileRepository referenceFileRepository,
			SequenceFileService seqeunceFileService,
			SampleService sampleService,
			AnalysisExecutionService analysisExecutionServiceSimplified,
			AnalysisSubmissionService analysisSubmissionService,
			AnalysisSubmissionRepository analysisSubmissionRepository) {
		super();
		this.referenceFileRepository = referenceFileRepository;
		this.seqeunceFileService = seqeunceFileService;
		this.sampleService = sampleService;
		this.analysisExecutionServiceSimplified = analysisExecutionServiceSimplified;
		this.analysisSubmissionService = analysisSubmissionService;
		this.analysisSubmissionRepository = analysisSubmissionRepository;
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
	 * @param iridaWorkflowID
	 *            The id of an irida workflow.
	 * @return An AnalysisSubmissionPhylogenomics which has been saved to the
	 *         database.
	 */
	public AnalysisSubmission setupSubmissionInDatabase(long sampleId, Path sequenceFilePath, Path referenceFilePath,
			UUID iridaWorkflowId) {

		SequenceFile sequenceFile = setupSampleSequenceFileInDatabase(sampleId, sequenceFilePath).get(0);

		Set<SequenceFile> sequenceFiles = new HashSet<>();
		sequenceFiles.add(sequenceFile);

		ReferenceFile referenceFile = referenceFileRepository.save(new ReferenceFile(referenceFilePath));

		AnalysisSubmission submission = analysisSubmissionService.create(new AnalysisSubmission("my analysis",
				sequenceFiles, referenceFile, iridaWorkflowId));

		return analysisSubmissionRepository.findOne(submission.getId());
	}

	/**
	 * Attaches the given sequence file paths to a particular sample id.
	 * 
	 * @param sampleId
	 *            The id of the sample to attach a sequence file to.
	 * @param sequenceFilePaths
	 *            A path of the sequence file to attach.
	 * @return A List of SequenceFile objects with the given sequence file path
	 *         attached and saved in the database.
	 */
	public List<SequenceFile> setupSampleSequenceFileInDatabase(long sampleId, Path... sequenceFilePaths) {
		Sample sample = sampleService.read(sampleId);
		List<SequenceFile> returnedSequenceFiles = new ArrayList<>();

		for (Path sequenceFilePath : sequenceFilePaths) {
			Join<Sample, SequenceFile> sampleSeqFile = seqeunceFileService.createSequenceFileInSample(new SequenceFile(
					sequenceFilePath), sample);
			SequenceFile sequenceFile = sampleSeqFile.getObject();
			returnedSequenceFiles.add(sequenceFile);
		}
		return returnedSequenceFiles;
	}

	/**
	 * Wait for the given analysis submission to be complete.
	 * 
	 * @param analysisSubmission
	 *            The analysis submission to wait for.
	 * @throws Exception
	 */
	public void waitUntilSubmissionCompleteSimplified(AnalysisSubmission analysisSubmission) throws Exception {
		final int totalSecondsWait = 1 * 60; // 1 minute
		final int pollingTime = 2000; // 2 seconds

		Future<Void> waitForHistory = executor.submit(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				WorkflowStatus workflowStatus;
				do {
					workflowStatus = analysisExecutionServiceSimplified.getWorkflowStatus(analysisSubmission);
					Thread.sleep(pollingTime);
				} while (!WorkflowState.OK.equals(workflowStatus.getState()));

				return null;
			}

		});
		try {
			waitForHistory.get(totalSecondsWait, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			throw new Exception("Timeout > " + totalSecondsWait + " s when waiting for history for "
					+ analysisSubmission, e);
		}
	}

	/**
	 * Asserts that the given status is in a valid state for a workflow.
	 * 
	 * @param status
	 */
	public void assertValidStatus(WorkflowStatus status) {
		assertNotNull("WorkflowStatus is null", status);
		assertFalse("WorkflowState is " + WorkflowState.UNKNOWN, WorkflowState.UNKNOWN.equals(status.getState()));
		float percentComplete = status.getPercentComplete();
		assertTrue("percentComplete not in range of 0 to 100", 0.0f <= percentComplete && percentComplete <= 100.0f);
	}
}
