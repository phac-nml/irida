package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.exceptions.DuplicateSampleException;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.CoverageQCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.FileProcessorErrorQCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry.QCEntryStatus;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.repositories.sample.SampleRepository;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@ServiceIntegrationTest
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SequenceFileServiceImplIT.xml")
@DatabaseTearDown(value = "/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml", type = DatabaseOperation.DELETE_ALL)
public class SequencingObjectServiceImplIT {
	private static final Logger logger = LoggerFactory.getLogger(SequencingObjectServiceImplIT.class);

	private static final String SEQUENCE = "ACGTACGTN";
	private static final byte[] FASTQ_FILE_CONTENTS = ("@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n"
			+ SEQUENCE + "\n+\n?????????").getBytes();

	private static final String CHECKSUM = "85e440ab2f17636ab24b12e8e4b4d445b6131e7df785cbd02d56c2688eef55fb";

	private static final String ZIPPED_CHECKSUM = "123f53507f29a3375f6c1ddd054dadaf4e90cacda536452019b7a37a678f8d7d";

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AnalysisService analysisService;

	@Autowired
	private SampleService sampleService;

	@Autowired
	private SampleRepository sampleRepository;

	@Autowired
	private SequencingObjectService objectService;

	@Autowired
	private SequencingRunService sequencingRunService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	@Qualifier("sequenceFileBaseDirectory")
	private Path baseDirectory;

	@BeforeEach
	public void setUp() throws IOException {
		Files.createDirectories(baseDirectory);
		FileUtils.cleanDirectory(baseDirectory.toFile());
	}

	private SequencingObjectServiceImplIT asRole(Role r, String username) {
		User u = new User();
		u.setUsername(username);
		u.setPassword(passwordEncoder.encode("Password1!"));
		u.setSystemRole(r);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, "Password1!",
				ImmutableList.of(r));
		auth.setDetails(u);
		SecurityContextHolder.getContext().setAuthentication(auth);
		return this;
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testAddSequenceFilePairAsSequencer() throws IOException {

		SequenceFile file1 = createSequenceFile("file1");
		SequenceFile file2 = createSequenceFile("file2");

		SequenceFilePair sequenceFilePair = new SequenceFilePair(file1, file2);

		SequencingObject createSequenceFilePair = objectService.create(sequenceFilePair);
		assertTrue(createSequenceFilePair.getFiles().contains(file1));
		assertTrue(createSequenceFilePair.getFiles().contains(file2));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetSequenceFilePairForSample() {
		Sample s = sampleService.read(2L);

		Set<Long> fileIds = Sets.newHashSet(3L, 4L);

		Collection<SampleSequencingObjectJoin> sequenceFilePairsForSample = objectService.getSequencesForSampleOfType(s,
				SequenceFilePair.class);
		assertEquals(1, sequenceFilePairsForSample.size());
		SequencingObject pair = sequenceFilePairsForSample.iterator().next().getObject();

		for (SequenceFile file : pair.getFiles()) {
			assertTrue(fileIds.contains(file.getId()), "file id should be in set");
			fileIds.remove(file.getId());
		}

		assertTrue(fileIds.isEmpty(), "all file ids should have been found");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testCreateSequenceFileInSample() throws IOException, InterruptedException {
		Sample s = sampleService.read(1L);
		SequenceFile sf = new SequenceFile();
		Path sequenceFile = Files.createTempFile("TEMPORARY-SEQUENCE-FILE", ".gz");
		OutputStream gzOut = new GZIPOutputStream(Files.newOutputStream(sequenceFile));
		gzOut.write(FASTQ_FILE_CONTENTS);
		gzOut.close();

		sf.setFile(sequenceFile);
		SingleEndSequenceFile so = new SingleEndSequenceFile(sf);

		objectService.createSequencingObjectInSample(so, s);

		SequencingRun mr = sequencingRunService.read(1L);
		sequencingRunService.addSequencingObjectToSequencingRun(mr, so);

		// Sleeping for a bit to let file processing run
		Thread.sleep(15000);

		Sample readSample = sampleService.read(s.getId());

		List<QCEntry> qcEntries = sampleService.getQCEntriesForSample(readSample);

		assertEquals(1, qcEntries.size(), "should be one qc entries");
		QCEntry qcEntry = qcEntries.iterator().next();
		assertTrue(qcEntry instanceof CoverageQCEntry, "should be coverage entry");
		CoverageQCEntry coverage = (CoverageQCEntry) qcEntry;
		assertEquals(18, coverage.getTotalBases(), "should be 18 bases");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testCreateCorruptSequenceFileInSample() throws IOException, InterruptedException {
		Sample s = sampleService.read(1L);
		SequenceFile sf = new SequenceFile();
		Path sequenceFile = Files.createTempFile("TEMPORARY-SEQUENCE-FILE", ".gz");
		OutputStream gzOut = Files.newOutputStream(sequenceFile);
		gzOut.write("not a file".getBytes());
		gzOut.close();

		sf.setFile(sequenceFile);
		SingleEndSequenceFile so = new SingleEndSequenceFile(sf);

		objectService.createSequencingObjectInSample(so, s);

		// Wait 15 seconds. file processing should have failed by then.
		Thread.sleep(15000);

		Sample readSample = sampleService.read(s.getId());

		List<QCEntry> qcEntries = sampleService.getQCEntriesForSample(readSample);

		assertFalse(qcEntries.isEmpty(), "should be a qc entry");
		QCEntry qc = qcEntries.iterator().next();

		assertTrue(qc instanceof FileProcessorErrorQCEntry, "should be a FileProcessorErrorQCEntry");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testGetSequencefilesForSampleAsSequencer() throws IOException {
		Sample s = sampleService.read(1L);
		Collection<SampleSequencingObjectJoin> sequencingObjectsForSample = objectService
				.getSequencingObjectsForSample(s);
		assertEquals(1, sequencingObjectsForSample.size());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetUnpairedFilesForSample() {
		Sample s = sampleService.read(2L);

		Collection<SampleSequencingObjectJoin> sequencesForSampleOfType = objectService.getSequencesForSampleOfType(s,
				SingleEndSequenceFile.class);

		assertEquals(1, sequencesForSampleOfType.size());
		SampleSequencingObjectJoin join = sequencesForSampleOfType.iterator().next();

		assertEquals(Long.valueOf(4), join.getObject().getId());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetUnpairedFilesForSampleWithNoPairs() {
		Sample s = sampleService.read(1L);

		Collection<SampleSequencingObjectJoin> sequencesForSampleOfType = objectService.getSequencesForSampleOfType(s,
				SingleEndSequenceFile.class);
		assertEquals(1, sequencesForSampleOfType.size());
		SampleSequencingObjectJoin join = sequencesForSampleOfType.iterator().next();

		assertEquals(Long.valueOf(2), join.getObject().getId());
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testCreateNotCompressedSequenceFile() throws IOException, InterruptedException {
		final Long expectedRevisionNumber = 1L;
		SequenceFile sf = createSequenceFile("file1");
		Path sequenceFile = sf.getFile();
		SingleEndSequenceFile singleEndSequenceFile = new SingleEndSequenceFile(sf);

		logger.trace("About to save the file.");
		SequencingObject sequencingObject = asRole(Role.ROLE_SEQUENCER, "fbristow").objectService
				.create(singleEndSequenceFile);
		logger.trace("Finished saving the file.");

		assertNotNull(sequencingObject.getId(), "ID wasn't assigned.");

		// Sleeping for a bit to let file processing run
		Thread.sleep(15000);

		// figure out what the version number of the sequence file is (should be
		// 1; the file wasn't gzipped, but fastqc will have modified it.)
		SequencingObject readObject = null;
		do {
			readObject = asRole(Role.ROLE_ADMIN, "admin").objectService.read(sequencingObject.getId());
			sf = readObject.getFiles().iterator().next();
			if (sf.getFileRevisionNumber() < expectedRevisionNumber) {
				logger.info("Still waiting on thread to finish, having a bit of a sleep.");
				Thread.sleep(1000);
			}
		} while (sf.getFileRevisionNumber() < expectedRevisionNumber);
		assertEquals(expectedRevisionNumber, sf.getFileRevisionNumber(), "Wrong version number after processing.");

		// verify the file checksum was taken properly
		assertEquals(CHECKSUM, sf.getUploadSha256(), "checksum should be equal");

		AnalysisFastQC analysis = asRole(Role.ROLE_ADMIN, "admin").analysisService
				.getFastQCAnalysisForSequenceFile(readObject, sf.getId());
		assertNotNull(analysis, "FastQCAnalysis should have been created for the file.");

		Set<OverrepresentedSequence> overrepresentedSequences = analysis.getOverrepresentedSequences();
		assertNotNull(overrepresentedSequences, "No overrepresented sequences were found.");
		assertEquals(1, overrepresentedSequences.size(), "Wrong number of overrepresented sequences were found.");
		OverrepresentedSequence overrepresentedSequence = overrepresentedSequences.iterator().next();
		assertEquals(SEQUENCE, overrepresentedSequence.getSequence(), "Sequence was not the correct sequence.");
		assertEquals(2, overrepresentedSequence.getOverrepresentedSequenceCount(), "The count was not correct.");
		assertEquals(new BigDecimal("100.00"), overrepresentedSequence.getPercentage(), "The percent was not correct.");

		// confirm that the file structure is correct
		Path idDirectory = baseDirectory.resolve(Paths.get(sf.getId().toString()));
		assertTrue(
				Files.exists(idDirectory.resolve(
						Paths.get(sf.getFileRevisionNumber().toString(), sequenceFile.getFileName().toString()))),
				"Revision directory doesn't exist.");
		// no other files or directories should be beneath the ID directory
		int fileCount = 0;
		Iterator<Path> dir = Files.newDirectoryStream(idDirectory).iterator();
		while (dir.hasNext()) {
			dir.next();
			fileCount++;
		}
		assertEquals(1, fileCount, "Wrong number of directories beneath the id directory");
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testCreateCompressedSequenceFile() throws IOException, InterruptedException {
		final Long expectedRevisionNumber = 2L;
		SequenceFile sf = new SequenceFile();
		Path sequenceFile = Files.createTempFile("TEMPORARY-SEQUENCE-FILE", ".gz");
		OutputStream gzOut = new GZIPOutputStream(Files.newOutputStream(sequenceFile));
		gzOut.write(FASTQ_FILE_CONTENTS);
		gzOut.close();

		sf.setFile(sequenceFile);
		SingleEndSequenceFile singleEndSequenceFile = new SingleEndSequenceFile(sf);

		logger.trace("About to save the file.");
		SequencingObject sequencingObject = objectService.create(singleEndSequenceFile);
		logger.trace("Finished saving the file.");

		assertNotNull(sequencingObject.getId(), "ID wasn't assigned.");

		// Sleeping for a bit to let file processing run
		Thread.sleep(10000);

		// figure out what the version number of the sequence file is (should be
		// 2; the file was gzipped)
		// get the MOST RECENT version of the sequence file from the database
		// (it will have been modified outside of the create method.)
		SequencingObject readObject = null;
		do {
			readObject = asRole(Role.ROLE_ADMIN, "admin").objectService.read(sequencingObject.getId());
			sf = readObject.getFiles().iterator().next();
			if (sf.getFileRevisionNumber() < expectedRevisionNumber) {
				logger.info("Still waiting on thread to finish, having a bit of a sleep.");
				Thread.sleep(1000);
			}
		} while (sf.getFileRevisionNumber() < expectedRevisionNumber);

		// one more sleep to make sure everything's settled
		Thread.sleep(1000);

		assertEquals(expectedRevisionNumber, sf.getFileRevisionNumber(), "Wrong version number after processing.");
		assertFalse(sf.getFile().getFileName().toString().endsWith(".gz"), "File name is still gzipped.");
		AnalysisFastQC analysis = asRole(Role.ROLE_ADMIN, "admin").analysisService
				.getFastQCAnalysisForSequenceFile(readObject, sf.getId());

		// verify the file checksum was taken properly
		assertEquals(ZIPPED_CHECKSUM, sf.getUploadSha256(), "checksum should be equal");

		Set<OverrepresentedSequence> overrepresentedSequences = analysis.getOverrepresentedSequences();
		assertNotNull(overrepresentedSequences, "No overrepresented sequences were found.");
		assertEquals(1, overrepresentedSequences.size(), "Wrong number of overrepresented sequences were found.");
		OverrepresentedSequence overrepresentedSequence = overrepresentedSequences.iterator().next();
		assertEquals(SEQUENCE, overrepresentedSequence.getSequence(), "Sequence was not the correct sequence.");
		assertEquals(2, overrepresentedSequence.getOverrepresentedSequenceCount(), "The count was not correct.");
		assertEquals(new BigDecimal("100.00"), overrepresentedSequence.getPercentage(), "The percent was not correct.");

		// confirm that the file structure is correct
		String filename = sequenceFile.getFileName().toString();
		filename = filename.substring(0, filename.lastIndexOf('.'));
		Path idDirectory = baseDirectory.resolve(Paths.get(sf.getId().toString()));
		assertTrue(Files.exists(idDirectory.resolve(Paths.get(sf.getFileRevisionNumber().toString(), filename))),
				"Revision directory doesn't exist.");
		// no other files or directories should be beneath the ID directory
		int fileCount = 0;
		Iterator<Path> dir = Files.newDirectoryStream(idDirectory).iterator();
		while (dir.hasNext()) {
			dir.next();
			fileCount++;
		}
		assertEquals(2, fileCount, "Wrong number of directories beneath the id directory");
	}

	@Test
	@WithMockUser(username = "fbristow1", roles = "USER")
	public void testReadSequenceFileAsUserWithPermissions() {
		assertNotNull(objectService.read(2L));
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testReadSequenceFileAsUserNoPermissions() {
		assertThrows(AccessDeniedException.class, () -> {
			objectService.read(2L);
		});
	}

	@Test
	@WithMockUser(username = "fbristow1", roles = "USER")
	public void testReadOptionalProperties() {
		SequencingObject sequencingObject = objectService.read(2L);
		SequenceFile read = sequencingObject.getFileWithId(1L);
		assertEquals(read.getOptionalProperty("samplePlate"), "5");
		assertEquals(read.getOptionalProperty("sampleWell"), "10");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testCoverage() throws IOException, InterruptedException {
		Project project = projectService.read(1L);
		project.setGenomeSize(3L);
		project.setMinimumCoverage(2);

		project = projectService.update(project);

		Sample s = sampleService.read(1L);
		SequenceFile sf = new SequenceFile();
		Path sequenceFile = Files.createTempFile("TEMPORARY-SEQUENCE-FILE", ".gz");
		OutputStream gzOut = new GZIPOutputStream(Files.newOutputStream(sequenceFile));
		gzOut.write(FASTQ_FILE_CONTENTS);
		gzOut.close();

		sf.setFile(sequenceFile);
		SingleEndSequenceFile so = new SingleEndSequenceFile(sf);

		objectService.createSequencingObjectInSample(so, s);

		SequencingRun mr = sequencingRunService.read(1L);
		sequencingRunService.addSequencingObjectToSequencingRun(mr, so);

		// Wait 15 seconds. file processing should have run by then.
		Thread.sleep(15000);

		Sample readSample = sampleService.read(s.getId());

		List<QCEntry> qcEntries = sampleService.getQCEntriesForSample(readSample);

		assertEquals(1, qcEntries.size(), "should be one qc entry");
		QCEntry qcEntry = qcEntries.iterator().next();
		qcEntry.addProjectSettings(project);

		assertTrue(qcEntry instanceof CoverageQCEntry, "should be coverage entry");
		assertEquals(QCEntryStatus.POSITIVE, qcEntry.getStatus(), "qc should have passed");
		assertEquals(qcEntry.getMessage(), "6x", "should be 6x coverage");

		project.setMinimumCoverage(10);
		project = projectService.update(project);

		// Wait 10 seconds. file processing should have run by then.
		Thread.sleep(10000);

		qcEntries = sampleService.getQCEntriesForSample(readSample);
		assertEquals(1, qcEntries.size(), "should be one qc entry");
		qcEntry = qcEntries.iterator().next();
		qcEntry.addProjectSettings(project);
		assertTrue(qcEntry instanceof CoverageQCEntry, "should be coverage entry");
		assertEquals(QCEntryStatus.NEGATIVE, qcEntry.getStatus(), "qc should have failed");
	}

	/**
	 * Tests to make sure we get a properly constructed map of samples to
	 * sequencing objects.
	 */
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetUniqueSamplesForSequencingObjectsSuccess() {
		SequencingObject s1 = objectService.read(1L);
		SequencingObject s2 = objectService.read(2L);

		Sample sa1 = sampleService.read(1L);
		Sample sa2 = sampleService.read(2L);

		Map<Sample, SequencingObject> sampleMap = objectService
				.getUniqueSamplesForSequencingObjects(Sets.newHashSet(s1, s2));
		assertEquals(2, sampleMap.size(), "Incorrect number of results returned in sample map");
		assertEquals(s2.getId(), sampleMap.get(sa1).getId(), "Incorrect sequencing object mapped to sample");
		assertEquals(s1.getId(), sampleMap.get(sa2).getId(), "Incorrect sequencing object mapped to sample");
	}

	/**
	 * Tests failure when a sample for one sequencing object does not exist.
	 */
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetUniqueSamplesForSequencingObjectsFailNoSample() {
		SequencingObject s1 = objectService.read(1L);
		SequencingObject s2 = objectService.read(2L);

		sampleRepository.deleteById(1L);

		assertThrows(EntityNotFoundException.class, () -> {
			objectService.getUniqueSamplesForSequencingObjects(Sets.newHashSet(s1, s2));
		});
	}

	/**
	 * Tests failure for duplicate samples in sequencing objects.
	 */
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetUniqueSamplesForSequencingObjectsFailDuplicateSample() {
		SequencingObject s1 = objectService.read(1L);
		SequencingObject s4 = objectService.read(4L);

		assertThrows(DuplicateSampleException.class, () -> {
			objectService.getUniqueSamplesForSequencingObjects(Sets.newHashSet(s1, s4));
		});
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testConcatenateSequenceFiles() throws IOException, InterruptedException, ConcatenateException {
		String newFileName = "newname";
		Sample sample = sampleService.read(1L);

		SequenceFile file1 = createSequenceFile("file1");
		SequenceFile file2 = createSequenceFile("file2");

		long originalLength = file1.getFile().toFile().length();

		SequencingObject so1 = new SingleEndSequenceFile(file1);
		SequencingObject so2 = new SingleEndSequenceFile(file2);

		SampleSequencingObjectJoin join1 = objectService.createSequencingObjectInSample(so1, sample);
		SampleSequencingObjectJoin join2 = objectService.createSequencingObjectInSample(so2, sample);

		// Wait 5 seconds. file processing should have run by then.
		Thread.sleep(5000);

		// re-read the original files so file processing will be complete
		so1 = objectService.read(join1.getObject().getId());
		so2 = objectService.read(join2.getObject().getId());

		Collection<SampleSequencingObjectJoin> originalSeqs = objectService.getSequencingObjectsForSample(sample);

		List<SequencingObject> fileSet = Lists.newArrayList(so1, so2);

		SampleSequencingObjectJoin concatenateSequences = objectService.concatenateSequences(fileSet, newFileName,
				sample, false);

		// Wait 5 seconds. file processing should have run by then.
		Thread.sleep(5000);

		Collection<SampleSequencingObjectJoin> newSeqs = objectService.getSequencingObjectsForSample(sample);

		// re-read the concatenated file so file processing will be complete
		concatenateSequences = sampleService.getSampleForSequencingObject(concatenateSequences.getObject());

		assertTrue(newSeqs.containsAll(originalSeqs), "new seq collection should contain originals");
		assertTrue(newSeqs.contains(concatenateSequences), "new seq collection should contain new object");
		assertEquals(originalSeqs.size() + 1, newSeqs.size(), "new seq collection should have 1 more object");

		SequencingObject newSeqObject = concatenateSequences.getObject();
		SequenceFile newFile = newSeqObject.getFiles().iterator().next();
		assertTrue(newFile.getFileName().contains(newFileName), "new file should contain new name");

		long newFileSize = newFile.getFile().toFile().length();

		assertEquals(originalLength * 2, newFileSize, "new file should be 2x size of originals");
	}

	private SequenceFile createSequenceFile(String name) throws IOException {
		Path sequenceFile = Files.createTempFile(name, ".fastq");
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);

		return new SequenceFile(sequenceFile);
	}
}
