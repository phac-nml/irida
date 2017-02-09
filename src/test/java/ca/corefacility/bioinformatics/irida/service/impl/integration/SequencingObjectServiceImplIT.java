package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.CoverageQCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.FileProcessorErrorQCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry.QCEntryStatus;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SequenceFileServiceImplIT.xml")
@DatabaseTearDown(value = "/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml", type = DatabaseOperation.DELETE_ALL)
public class SequencingObjectServiceImplIT {
	private static final Logger logger = LoggerFactory.getLogger(SequencingObjectServiceImplIT.class);

	private static final String SEQUENCE = "ACGTACGTN";
	private static final byte[] FASTQ_FILE_CONTENTS = ("@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n"
			+ SEQUENCE + "\n+\n?????????").getBytes();

	private static final String CHECKSUM = "85e440ab2f17636ab24b12e8e4b4d445b6131e7df785cbd02d56c2688eef55fb";

	private static final String ZIPPED_CHECKSUM = "a2ff0d0790029822b4a2457a2912414ffc532136729ad2fd06efc70fe428ab32";

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AnalysisService analysisService;

	@Autowired
	private SampleService sampleService;

	@Autowired
	private SequencingObjectService objectService;

	@Autowired
	private SequencingRunService sequencingRunService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	@Qualifier("sequenceFileBaseDirectory")
	private Path baseDirectory;

	@Before
	public void setUp() throws IOException {
		Files.createDirectories(baseDirectory);
	}

	private SequencingObjectServiceImplIT asRole(Role r, String username) {
		User u = new User();
		u.setUsername(username);
		u.setPassword(passwordEncoder.encode("Password1"));
		u.setSystemRole(r);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, "Password1",
				ImmutableList.of(r));
		auth.setDetails(u);
		SecurityContextHolder.getContext().setAuthentication(auth);
		return this;
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testAddSequenceFilePairAsSequencer() throws IOException {

		Path sequenceFile = Files.createTempFile("file1", ".fastq");
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);

		Path sequenceFile2 = Files.createTempFile("file2", ".fastq");
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);

		SequenceFile file1 = new SequenceFile(sequenceFile);
		SequenceFile file2 = new SequenceFile(sequenceFile2);

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
			assertTrue("file id should be in set", fileIds.contains(file.getId()));
			fileIds.remove(file.getId());
		}

		assertTrue("all file ids should have been found", fileIds.isEmpty());
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

		// Wait 5 seconds. file processing should have failed by then.
		Thread.sleep(5000);

		Sample readSample = sampleService.read(s.getId());

		List<QCEntry> qcEntries = sampleService.getQCEntriesForSample(readSample);

		assertTrue("should be no qc entries", qcEntries.isEmpty());
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

		// Wait 5 seconds. file processing should have failed by then.
		Thread.sleep(5000);

		Sample readSample = sampleService.read(s.getId());

		List<QCEntry> qcEntries = sampleService.getQCEntriesForSample(readSample);

		assertFalse("should be a qc entry", qcEntries.isEmpty());
		QCEntry qc = qcEntries.iterator().next();

		assertTrue("should be a FileProcessorErrorQCEntry", qc instanceof FileProcessorErrorQCEntry);
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

		assertEquals(new Long(4), join.getObject().getId());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetUnpairedFilesForSampleWithNoPairs() {
		Sample s = sampleService.read(1L);

		Collection<SampleSequencingObjectJoin> sequencesForSampleOfType = objectService.getSequencesForSampleOfType(s,
				SingleEndSequenceFile.class);
		assertEquals(1, sequencesForSampleOfType.size());
		SampleSequencingObjectJoin join = sequencesForSampleOfType.iterator().next();

		assertEquals(new Long(2), join.getObject().getId());
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testCreateNotCompressedSequenceFile() throws IOException, InterruptedException {
		final Long expectedRevisionNumber = 3L;
		SequenceFile sf = new SequenceFile();
		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);
		sf.setFile(sequenceFile);
		SingleEndSequenceFile singleEndSequenceFile = new SingleEndSequenceFile(sf);

		logger.trace("About to save the file.");
		SequencingObject sequencingObject = asRole(Role.ROLE_SEQUENCER, "fbristow").objectService
				.create(singleEndSequenceFile);
		logger.trace("Finished saving the file.");

		assertNotNull("ID wasn't assigned.", sequencingObject.getId());

		// figure out what the version number of the sequence file is (should be
		// 2; the file wasn't gzipped, but fastqc will have modified it.)
		SequencingObject readObject = null;
		do {
			readObject = asRole(Role.ROLE_ADMIN, "admin").objectService.read(sequencingObject.getId());
			sf = readObject.getFiles().iterator().next();
			if (sf.getFileRevisionNumber() < expectedRevisionNumber) {
				logger.info("Still waiting on thread to finish, having a bit of a sleep.");
				Thread.sleep(1000);
			}
		} while (sf.getFileRevisionNumber() < expectedRevisionNumber);
		assertEquals("Wrong version number after processing.", expectedRevisionNumber, sf.getFileRevisionNumber());

		// verify the file checksum was taken properly
		assertEquals("checksum should be equal", CHECKSUM, sf.getUploadSha256());

		AnalysisFastQC analysis = asRole(Role.ROLE_ADMIN, "admin").analysisService
				.getFastQCAnalysisForSequenceFile(readObject, sf.getId());
		assertNotNull("FastQCAnalysis should have been created for the file.", analysis);

		Set<OverrepresentedSequence> overrepresentedSequences = analysis.getOverrepresentedSequences();
		assertNotNull("No overrepresented sequences were found.", overrepresentedSequences);
		assertEquals("Wrong number of overrepresented sequences were found.", 1, overrepresentedSequences.size());
		OverrepresentedSequence overrepresentedSequence = overrepresentedSequences.iterator().next();
		assertEquals("Sequence was not the correct sequence.", SEQUENCE, overrepresentedSequence.getSequence());
		assertEquals("The count was not correct.", 2, overrepresentedSequence.getOverrepresentedSequenceCount());
		assertEquals("The percent was not correct.", new BigDecimal("100.00"), overrepresentedSequence.getPercentage());

		// confirm that the file structure is correct
		Path idDirectory = baseDirectory.resolve(Paths.get(sf.getId().toString()));
		assertTrue("Revision directory doesn't exist.", Files.exists(idDirectory
				.resolve(Paths.get(sf.getFileRevisionNumber().toString(), sequenceFile.getFileName().toString()))));
		// no other files or directories should be beneath the ID directory
		int fileCount = 0;
		Iterator<Path> dir = Files.newDirectoryStream(idDirectory).iterator();
		while (dir.hasNext()) {
			dir.next();
			fileCount++;
		}
		assertEquals("Wrong number of directories beneath the id directory", 3, fileCount);
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testCreateCompressedSequenceFile() throws IOException, InterruptedException {
		final Long expectedRevisionNumber = 4L;
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

		assertNotNull("ID wasn't assigned.", sequencingObject.getId());

		// figure out what the version number of the sequence file is (should be
		// 3; the file was gzipped)
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
		assertEquals("Wrong version number after processing.", expectedRevisionNumber, sf.getFileRevisionNumber());
		assertFalse("File name is still gzipped.", sf.getFile().getFileName().toString().endsWith(".gz"));
		AnalysisFastQC analysis = asRole(Role.ROLE_ADMIN, "admin").analysisService
				.getFastQCAnalysisForSequenceFile(readObject, sf.getId());

		// verify the file checksum was taken properly
		assertEquals("checksum should be equal", ZIPPED_CHECKSUM, sf.getUploadSha256());

		Set<OverrepresentedSequence> overrepresentedSequences = analysis.getOverrepresentedSequences();
		assertNotNull("No overrepresented sequences were found.", overrepresentedSequences);
		assertEquals("Wrong number of overrepresented sequences were found.", 1, overrepresentedSequences.size());
		OverrepresentedSequence overrepresentedSequence = overrepresentedSequences.iterator().next();
		assertEquals("Sequence was not the correct sequence.", SEQUENCE, overrepresentedSequence.getSequence());
		assertEquals("The count was not correct.", 2, overrepresentedSequence.getOverrepresentedSequenceCount());
		assertEquals("The percent was not correct.", new BigDecimal("100.00"), overrepresentedSequence.getPercentage());

		// confirm that the file structure is correct
		String filename = sequenceFile.getFileName().toString();
		filename = filename.substring(0, filename.lastIndexOf('.'));
		Path idDirectory = baseDirectory.resolve(Paths.get(sf.getId().toString()));
		assertTrue("Revision directory doesn't exist.",
				Files.exists(idDirectory.resolve(Paths.get(sf.getFileRevisionNumber().toString(), filename))));
		// no other files or directories should be beneath the ID directory
		int fileCount = 0;
		Iterator<Path> dir = Files.newDirectoryStream(idDirectory).iterator();
		while (dir.hasNext()) {
			dir.next();
			fileCount++;
		}
		assertEquals("Wrong number of directories beneath the id directory", 4, fileCount);
	}

	@Test
	@WithMockUser(username = "fbristow1", roles = "USER")
	public void testReadSequenceFileAsUserWithPermissions() {
		assertNotNull(objectService.read(2L));
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testReadSequenceFileAsUserNoPermissions() {
		objectService.read(2L);
	}

	@Test
	@WithMockUser(username = "fbristow1", roles = "USER")
	public void testReadOptionalProperties() {
		SequencingObject sequencingObject = objectService.read(2L);
		SequenceFile read = sequencingObject.getFileWithId(1L);
		assertEquals("5", read.getOptionalProperty("samplePlate"));
		assertEquals("10", read.getOptionalProperty("sampleWell"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testCoverage() throws IOException, InterruptedException {
		Project project = projectService.read(1L);
		project.setGenomeSize(3L);
		project.setRequiredCoverage(2);

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

		// Wait 5 seconds. file processing should have run by then.
		Thread.sleep(5000);

		Sample readSample = sampleService.read(s.getId());

		List<QCEntry> qcEntries = sampleService.getQCEntriesForSample(readSample);

		assertEquals("should be one qc entry", 1, qcEntries.size());
		QCEntry qcEntry = qcEntries.iterator().next();
		qcEntry.addProjectSettings(project);

		assertTrue("should be coverage entry", qcEntry instanceof CoverageQCEntry);
		assertEquals("qc should have passed", QCEntryStatus.POSITIVE, qcEntry.getStatus());
		assertEquals("should be 6x coverage", "6x", qcEntry.getMessage());

		project.setRequiredCoverage(10);
		project = projectService.update(project);

		// Wait 5 seconds. file processing should have run by then.
		Thread.sleep(5000);

		qcEntries = sampleService.getQCEntriesForSample(readSample);
		assertEquals("should be one qc entry", 1, qcEntries.size());
		qcEntry = qcEntries.iterator().next();
		qcEntry.addProjectSettings(project);
		assertTrue("should be coverage entry", qcEntry instanceof CoverageQCEntry);
		assertEquals("qc should have failed", QCEntryStatus.NEGATIVE, qcEntry.getStatus());
	}
}
