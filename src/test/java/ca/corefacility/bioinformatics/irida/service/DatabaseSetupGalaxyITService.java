package ca.corefacility.bioinformatics.irida.service;

import static com.google.common.base.Preconditions.checkArgument;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowState;
import ca.corefacility.bioinformatics.irida.model.workflow.execution.galaxy.GalaxyWorkflowStatus;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.repositories.analysis.submission.AnalysisSubmissionRepository;
import ca.corefacility.bioinformatics.irida.repositories.referencefile.ReferenceFileRepository;
import ca.corefacility.bioinformatics.irida.repositories.sequencefile.SequenceFilePairRepository;
import ca.corefacility.bioinformatics.irida.service.analysis.execution.AnalysisExecutionService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Stores common code for integration tests that require special database setup
 * code. This includes code for setup of sequence files in a database and
 * waiting for submission to complete in Galaxy.
 * 
 *
 */
public class DatabaseSetupGalaxyITService {

	private final ExecutorService executor = Executors.newFixedThreadPool(1);

	private ReferenceFileRepository referenceFileRepository;
	private SequenceFileService seqeunceFileService;
	private SequenceFilePairRepository sequenceFilePairRepository;
	private SampleService sampleService;
	private AnalysisExecutionService analysisExecutionService;
	private AnalysisSubmissionService analysisSubmissionService;
	private AnalysisSubmissionRepository analysisSubmissionRepository;

	/**
	 * Builds a new AnalysisExecutionGalaxyITService with the given
	 * services/repositories.
	 * 
	 * @param referenceFileRepository
	 * @param seqeunceFileService
	 * @param sampleService
	 * @param analysisExecutionService
	 * @param analysisSubmissionService
	 * @param analysisSubmissionRepsitory
	 */
	public DatabaseSetupGalaxyITService(ReferenceFileRepository referenceFileRepository,
			SequenceFileService seqeunceFileService,
			SampleService sampleService,
			AnalysisExecutionService analysisExecutionService,
			AnalysisSubmissionService analysisSubmissionService,
			AnalysisSubmissionRepository analysisSubmissionRepository,
			SequenceFilePairRepository sequenceFilePairRepository) {
		super();
		this.referenceFileRepository = referenceFileRepository;
		this.seqeunceFileService = seqeunceFileService;
		this.sampleService = sampleService;
		this.analysisExecutionService = analysisExecutionService;
		this.analysisSubmissionService = analysisSubmissionService;
		this.analysisSubmissionRepository = analysisSubmissionRepository;
		this.sequenceFilePairRepository = sequenceFilePairRepository;
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
	 * @return An {@link AnalysisSubmission} which has been saved to the
	 *         database.
	 */
	public AnalysisSubmission setupSubmissionInDatabase(long sampleId, Path sequenceFilePath, Path referenceFilePath,
			UUID iridaWorkflowId) {

		SequenceFile sequenceFile = setupSampleSequenceFileInDatabase(sampleId, sequenceFilePath).get(0);

		Set<SequenceFile> sequenceFiles = new HashSet<>();
		sequenceFiles.add(sequenceFile);

		ReferenceFile referenceFile = referenceFileRepository.save(new ReferenceFile(referenceFilePath));

		AnalysisSubmission submission = AnalysisSubmission.builder(iridaWorkflowId)
				.name("my analysis")
				.inputFilesSingle(sequenceFiles)
				.referenceFile(referenceFile)
				.build();
		analysisSubmissionService.create(submission);

		return analysisSubmissionRepository.findOne(submission.getId());
	}
	
	/**
	 * Sets up an {@link AnalysisSubmission} with a list of paired sequence
	 * files and saves all dependencies in database.
	 * 
	 * @param sampleId
	 *            The id of the sample to associate with the given sequence
	 *            file.
	 * @param sequenceFilePaths1
	 *            A list of paths for the first part of the pair.
	 * @param sequenceFilePaths2
	 *            A list of paths for the second part of the pair. The path to
	 *            an input sequence file for this test.
	 * @param referenceFilePath
	 *            The path to an input reference file for this test.
	 * @param iridaWorkflowID
	 *            The id of an irida workflow.
	 * @return An {@link AnalysisSubmission} which has been saved to the
	 *         database.
	 */
	public AnalysisSubmission setupPairSubmissionInDatabase(long sampleId, List<Path> sequenceFilePaths1,
			List<Path> sequenceFilePaths2, Path referenceFilePath, UUID iridaWorkflowId) {

		List<SequenceFilePair> sequenceFilePairs = setupSampleSequenceFileInDatabase(sampleId, sequenceFilePaths1,
				sequenceFilePaths2);

		ReferenceFile referenceFile = referenceFileRepository.save(new ReferenceFile(referenceFilePath));

		AnalysisSubmission submission = AnalysisSubmission.builder(iridaWorkflowId)
				.name("paired analysis")
				.inputFilesPaired(Sets.newHashSet(sequenceFilePairs))
				.referenceFile(referenceFile)
				.build();
		analysisSubmissionService.create(submission);

		return analysisSubmissionRepository.findOne(submission.getId());
	}
	
	/**
	 * Sets up an {@link AnalysisSubmission} with a list of paired sequence
	 * files and saves all dependencies in database.
	 * 
	 * @param sampleId
	 *            The id of the sample to associate with the given sequence
	 *            file.
	 * @param sequenceFilePaths1
	 *            A list of paths for the first part of the pair.
	 * @param sequenceFilePaths2
	 *            A list of paths for the second part of the pair. The path to
	 *            an input sequence file for this test.
	 * @param referenceFilePath
	 *            The path to an input reference file for this test.
	 * @param parameters
	 *            The input parameters.
	 * @param iridaWorkflowID
	 *            The id of an irida workflow.
	 * @return An {@link AnalysisSubmission} which has been saved to the
	 *         database.
	 */
	public AnalysisSubmission setupPairSubmissionInDatabase(long sampleId, List<Path> sequenceFilePaths1,
			List<Path> sequenceFilePaths2, Path referenceFilePath, Map<String, String> parameters, UUID iridaWorkflowId) {

		List<SequenceFilePair> sequenceFilePairs = setupSampleSequenceFileInDatabase(sampleId, sequenceFilePaths1,
				sequenceFilePaths2);

		ReferenceFile referenceFile = referenceFileRepository.save(new ReferenceFile(referenceFilePath));

		AnalysisSubmission submission = AnalysisSubmission.builder(iridaWorkflowId).name("paired analysis")
				.inputFilesPaired(Sets.newHashSet(sequenceFilePairs)).referenceFile(referenceFile)
				.inputParameters(parameters).build();
		analysisSubmissionService.create(submission);

		return analysisSubmissionRepository.findOne(submission.getId());
	}
	
	/**
	 * Sets up an {@link AnalysisSubmission} with a list of paired sequence
	 * files and saves all dependencies in database.
	 * 
	 * @param sampleId
	 *            The id of the sample to associate with the given sequence
	 *            file.
	 * @param sequenceFilePaths1
	 *            A list of paths for the first part of the pair.
	 * @param sequenceFilePaths2
	 *            A list of paths for the second part of the pair. The path to
	 *            an input sequence file for this test.
	 * @param iridaWorkflowID
	 *            The id of an irida workflow.
	 * @return An {@link AnalysisSubmission} which has been saved to the
	 *         database.
	 */
	public AnalysisSubmission setupPairSubmissionInDatabase(long sampleId, List<Path> sequenceFilePaths1,
			List<Path> sequenceFilePaths2, UUID iridaWorkflowId) {

		List<SequenceFilePair> sequenceFilePairs = setupSampleSequenceFileInDatabase(sampleId, sequenceFilePaths1,
				sequenceFilePaths2);

		AnalysisSubmission submission = AnalysisSubmission.builder(iridaWorkflowId)
				.name("paired analysis")
				.inputFilesPaired(Sets.newHashSet(sequenceFilePairs))
				.build(); 
		analysisSubmissionService.create(submission);

		return analysisSubmissionRepository.findOne(submission.getId());
	}
	
	/**
	 * Sets up an {@link AnalysisSubmission} with a set of
	 * {@link SequenceFilePair}s that have already been setup with samples.
	 * 
	 * @param sequenceFilePairs
	 *            The set of {@link SequenceFilePair}s to submit.
	 * @param referenceFilePath
	 *            The path to an input reference file for this test.
	 * @param iridaWorkflowID
	 *            The id of an irida workflow.
	 * @return An {@link AnalysisSubmission} which has been saved to the
	 *         database.
	 */
	public AnalysisSubmission setupPairSubmissionInDatabase(Set<SequenceFilePair> sequenceFilePairs,
			Path referenceFilePath, UUID iridaWorkflowId) {

		ReferenceFile referenceFile = referenceFileRepository.save(new ReferenceFile(referenceFilePath));

		AnalysisSubmission submission = AnalysisSubmission.builder(iridaWorkflowId)
				.name("paired analysis")
				.inputFilesPaired(sequenceFilePairs)
				.referenceFile(referenceFile)
				.build(); 
		analysisSubmissionService.create(submission);

		return analysisSubmissionRepository.findOne(submission.getId());
	}
	
	/**
	 * Sets up an {@link AnalysisSubmission} with a set of
	 * {@link SequenceFilePair}s that have already been setup with samples.
	 * 
	 * @param sequenceFilePairs
	 *            The set of {@link SequenceFilePair}s to submit.
	 * @param referenceFilePath
	 *            The path to an input reference file for this test.
	 * @param iridaWorkflowID
	 *            The id of an irida workflow.
	 * @param parameters
	 *            The parameters to use.
	 * @return An {@link AnalysisSubmission} which has been saved to the
	 *         database.
	 */
	public AnalysisSubmission setupPairSubmissionInDatabase(Set<SequenceFilePair> sequenceFilePairs,
			Path referenceFilePath, Map<String, String> parameters, UUID iridaWorkflowId) {

		ReferenceFile referenceFile = referenceFileRepository.save(new ReferenceFile(referenceFilePath));

		AnalysisSubmission submission = AnalysisSubmission.builder(iridaWorkflowId).name("paired analysis")
				.inputFilesPaired(sequenceFilePairs).referenceFile(referenceFile).inputParameters(parameters).build();
		analysisSubmissionService.create(submission);

		return analysisSubmissionRepository.findOne(submission.getId());
	}
	
	/**
	 * Sets up an {@link AnalysisSubmission} with a list of paired sequence
	 * files and a single sequence file under the same sample and saves all
	 * dependencies in database.
	 * 
	 * @param sampleId
	 *            The id of the sample to associate with the given sequence
	 *            file.
	 * @param sequenceFilePaths1
	 *            A list of paths for the first part of the pair.
	 * @param sequenceFilePaths2
	 *            A list of paths for the second part of the pair. The path to
	 *            an input sequence file for this test.
	 * @param singleSequenceFile
	 *            A single sequence file to add.
	 * @param referenceFilePath
	 *            The path to an input reference file for this test.
	 * @param iridaWorkflowID
	 *            The id of an irida workflow.
	 * @return An {@link AnalysisSubmission} which has been saved to the
	 *         database.
	 */
	public AnalysisSubmission setupSinglePairSubmissionInDatabaseSameSample(long sampleId,
			List<Path> sequenceFilePaths1, List<Path> sequenceFilePaths2, Path singleSequenceFile,
			Path referenceFilePath, UUID iridaWorkflowId) {

		return setupSinglePairSubmissionInDatabaseDifferentSample(sampleId, sampleId, sequenceFilePaths1,
				sequenceFilePaths2, singleSequenceFile, referenceFilePath, iridaWorkflowId);
	}
	
	/**
	 * Sets up an {@link AnalysisSubmission} with a list of paired sequence
	 * files and a single sequence file under a different sample and saves all
	 * dependencies in database.
	 * 
	 * @param sampleIdPaired
	 *            The id of the sample to associate with the paired sequence
	 *            files.
	 * @param sampleIdSingle
	 *            The id of the sample to associate with the single sequence
	 *            file.
	 * @param sequenceFilePaths1
	 *            A list of paths for the first part of the pair.
	 * @param sequenceFilePaths2
	 *            A list of paths for the second part of the pair. The path to
	 *            an input sequence file for this test.
	 * @param singleSequenceFile
	 *            A single sequence file to add.
	 * @param referenceFilePath
	 *            The path to an input reference file for this test.
	 * @param iridaWorkflowID
	 *            The id of an irida workflow.
	 * @return An {@link AnalysisSubmission} which has been saved to the
	 *         database.
	 */
	public AnalysisSubmission setupSinglePairSubmissionInDatabaseDifferentSample(long sampleIdPaired,
			long sampleIdSingle, List<Path> sequenceFilePaths1, List<Path> sequenceFilePaths2, Path singleSequenceFile,
			Path referenceFilePath, UUID iridaWorkflowId) {

		SequenceFile sequenceFile = setupSampleSequenceFileInDatabase(sampleIdSingle, singleSequenceFile).get(0);
		List<SequenceFilePair> sequenceFilePairs = setupSampleSequenceFileInDatabase(sampleIdPaired,
				sequenceFilePaths1, sequenceFilePaths2);

		ReferenceFile referenceFile = referenceFileRepository.save(new ReferenceFile(referenceFilePath));

		AnalysisSubmission submission = AnalysisSubmission.builder(iridaWorkflowId)
				.name("paired analysis")
				.inputFilesSingle(Sets.newHashSet(sequenceFile))
				.inputFilesPaired(Sets.newHashSet(sequenceFilePairs))
				.referenceFile(referenceFile)
				.build(); 
		analysisSubmissionService.create(submission);

		return analysisSubmissionRepository.findOne(submission.getId());
	}
	
	/**
	 * Sets up an AnalysisSubmission and saves all dependencies in database.
	 * 
	 * @param sampleId
	 *            The id of the sample to associate with the given sequence
	 *            file.
	 * @param sequenceFileSet
	 *            A set of sequence files to use for this submission.
	 * @param referenceFilePath
	 *            The path to an input reference file for this test.
	 * @param iridaWorkflowID
	 *            The id of an irida workflow.
	 * @return An AnalysisSubmissionPhylogenomics which has been saved to the
	 *         database.
	 */
	public AnalysisSubmission setupSubmissionInDatabase(long sampleId, Set<SequenceFile> sequenceFileSet,
			Path referenceFilePath, UUID iridaWorkflowId) {

		ReferenceFile referenceFile = referenceFileRepository.save(new ReferenceFile(referenceFilePath));

		AnalysisSubmission submission = AnalysisSubmission.builder(iridaWorkflowId)
				.name("my analysis")
				.inputFilesSingle(sequenceFileSet)
				.referenceFile(referenceFile)
				.build(); 
		analysisSubmissionService.create(submission);

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
	 * Attaches the given sequence file paths as pairs (using parallel lists) to
	 * a particular sample id.
	 * 
	 * @param sampleId
	 *            The id of the sample to attach a sequence file to.
	 * @param sequenceFiles1
	 *            A list of the first part of each pairs to setup.
	 * @param sequenceFiles2
	 *            A list of the second part of each pairs to setup.
	 * @return A {@link List} of {@link SequenceFilePair} objects with the given
	 *         sequence file paths attached and saved in the database.
	 */
	public List<SequenceFilePair> setupSampleSequenceFileInDatabase(long sampleId, List<Path> sequenceFiles1,
			List<Path> sequenceFiles2) {
		checkArgument(sequenceFiles1.size() == sequenceFiles2.size(), "sequenceFiles lists are unequal");

		Sample sample = sampleService.read(sampleId);
		List<SequenceFilePair> returnedSequenceFilePairs = new ArrayList<>();
		for (int i = 0; i < sequenceFiles1.size(); i++) {
			SequenceFile sf1 = new SequenceFile(sequenceFiles1.get(i));
			SequenceFile sf2 = new SequenceFile(sequenceFiles2.get(i));
			List<Join<Sample, SequenceFile>> sampleSeqFilePair = seqeunceFileService.createSequenceFilePairInSample(
					sf1, sf2, sample);
			SequenceFile createdSequenceFile1 = sampleSeqFilePair.get(0).getObject();
			SequenceFilePair sequenceFilePair = sequenceFilePairRepository.getPairForSequenceFile(createdSequenceFile1);
			returnedSequenceFilePairs.add(sequenceFilePair);
		}
		return returnedSequenceFilePairs;
	}

	/**
	 * Wait for the given analysis submission to be complete.
	 * 
	 * @param analysisSubmission
	 *            The analysis submission to wait for.
	 * @throws Exception
	 */
	public void waitUntilSubmissionComplete(AnalysisSubmission analysisSubmission) throws Exception {
		final int totalSecondsWait = 1 * 60; // 1 minute
		final int pollingTime = 2000; // 2 seconds

		Future<Void> waitForHistory = executor.submit(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				GalaxyWorkflowStatus workflowStatus;
				do {
					workflowStatus = analysisExecutionService.getWorkflowStatus(analysisSubmission);
					Thread.sleep(pollingTime);
				} while (!(GalaxyWorkflowState.OK.equals(workflowStatus.getState()) || GalaxyWorkflowState.ERROR
						.equals(workflowStatus.getState())));

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
	public void assertValidStatus(GalaxyWorkflowStatus status) {
		assertNotNull("WorkflowStatus is null", status);
		float percentComplete = status.getProportionComplete();
		assertTrue("proportion not in range of 0 to 1", 0.0f <= percentComplete && percentComplete <= 1.0f);
	}
}
