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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

import ca.corefacility.bioinformatics.irida.config.IridaApiNoGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiNoGalaxyTestConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SequenceFileServiceImplIT.xml")
@DatabaseTearDown(value = "/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml", type = DatabaseOperation.DELETE_ALL)
public class SequenceFileServiceImplIT {

	private static final String SEQUENCE = "ACGTACGTN";
	private static final byte[] FASTQ_FILE_CONTENTS = ("@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n"
			+ SEQUENCE + "\n+\n?????????").getBytes();
	private static final Logger logger = LoggerFactory.getLogger(SequenceFileServiceImplIT.class);

	@Autowired
	private SequenceFileService sequenceFileService;
	@Autowired
	private AnalysisService analysisService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private SampleService sampleService;
	@Autowired
	private SequencingRunService sequencingRunService;

	@Autowired
	@Qualifier("sequenceFileBaseDirectory")
	private Path baseDirectory;

	@Before
	public void setUp() throws IOException {
		Files.createDirectories(baseDirectory);
	}

	private SequenceFileServiceImplIT asRole(Role r, String username) {
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

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "fbristow", roles = "USER")
	public void testReadSequenceFileAsUserNoPermissions() {
		sequenceFileService.read(1L);
	}

	@Test
	@WithMockUser(username = "fbristow1", roles = "USER")
	public void testReadSequenceFileAsUserWithPermissions() {
		sequenceFileService.read(1L);
	}

	@Test
	@WithMockUser(username = "fbristow1", roles = "USER")
	public void testReadOptionalProperties() {
		SequenceFile read = sequenceFileService.read(1L);
		assertEquals("5", read.getOptionalProperty("samplePlate"));
		assertEquals("10", read.getOptionalProperty("sampleWell"));
	}

	@Test
	@WithMockUser(username = "fbristow1", roles = "USER")
	public void testAddAdditionalProperties() throws IOException {
		SequenceFile file = sequenceFileService.read(1L);
		file.addOptionalProperty("index", "111");
		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);
		file.setFile(sequenceFile);
		Map<String, Object> changed = new HashMap<>();
		changed.put("optionalProperties", file.getOptionalProperties());
		changed.put("file", file.getFile());
		sequenceFileService.update(file.getId(), changed);
		SequenceFile reread = sequenceFileService.read(1L);
		assertNotNull(reread.getOptionalProperty("index"));
		assertEquals("111", reread.getOptionalProperty("index"));
	}

	@Test
	@WithMockUser(username = "tom", roles = "SEQUENCER")
	public void testCreateNotCompressedSequenceFile() throws IOException {
		SequenceFile sf = new SequenceFile();
		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);
		sf.setFile(sequenceFile);

		logger.trace("About to save the file.");
		sf = asRole(Role.ROLE_SEQUENCER, "fbristow").sequenceFileService.create(sf);
		logger.trace("Finished saving the file.");

		assertNotNull("ID wasn't assigned.", sf.getId());

		// figure out what the version number of the sequence file is (should be
		// 2; the file wasn't gzipped, but fastqc will have modified it.)
		sf = asRole(Role.ROLE_ADMIN, "tom").sequenceFileService.read(sf.getId());
		assertEquals("Wrong version number after processing.", Long.valueOf(1), sf.getFileRevisionNumber());

		Set<AnalysisFastQC> analyses = asRole(Role.ROLE_ADMIN, "tom").analysisService.getAnalysesForSequenceFile(sf,
				AnalysisFastQC.class);
		assertEquals("Only one analysis should be generated automatically.", 1, analyses.size());
		AnalysisFastQC analysis = analyses.iterator().next();

		Set<OverrepresentedSequence> overrepresentedSequences = analysis.getOverrepresentedSequences();
		assertNotNull("No overrepresented sequences were found.", overrepresentedSequences);
		assertEquals("Wrong number of overrepresented sequences were found.", 1, overrepresentedSequences.size());
		OverrepresentedSequence overrepresentedSequence = overrepresentedSequences.iterator().next();
		assertEquals("Sequence was not the correct sequence.", SEQUENCE, overrepresentedSequence.getSequence());
		assertEquals("The count was not correct.", 2, overrepresentedSequence.getOverrepresentedSequenceCount());
		assertEquals("The percent was not correct.", new BigDecimal("100.00"), overrepresentedSequence.getPercentage());

		// confirm that the file structure is correct
		Path idDirectory = baseDirectory.resolve(Paths.get(sf.getId().toString()));
		assertTrue("Revision directory doesn't exist.", Files.exists(idDirectory.resolve(Paths.get(sf
				.getFileRevisionNumber().toString(), sequenceFile.getFileName().toString()))));
		// no other files or directories should be beneath the ID directory
		int fileCount = 0;
		Iterator<Path> dir = Files.newDirectoryStream(idDirectory).iterator();
		while (dir.hasNext()) {
			dir.next();
			fileCount++;
		}
		assertEquals("Wrong number of directories beneath the id directory", 1, fileCount);
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testCreateCompressedSequenceFile() throws IOException {
		SequenceFile sf = new SequenceFile();
		Path sequenceFile = Files.createTempFile("TEMPORARY-SEQUENCE-FILE", ".gz");
		OutputStream gzOut = new GZIPOutputStream(Files.newOutputStream(sequenceFile));
		gzOut.write(FASTQ_FILE_CONTENTS);
		gzOut.close();

		sf.setFile(sequenceFile);

		logger.trace("About to save the file.");
		sf = sequenceFileService.create(sf);
		logger.trace("Finished saving the file.");

		assertNotNull("ID wasn't assigned.", sf.getId());

		// figure out what the version number of the sequence file is (should be
		// 2; the file was gzipped)
		// get the MOST RECENT version of the sequence file from the database
		// (it will have been modified outside of the create method.)
		sf = asRole(Role.ROLE_ADMIN, "tom").sequenceFileService.read(sf.getId());
		assertEquals("Wrong version number after processing.", Long.valueOf(2L), sf.getFileRevisionNumber());
		assertFalse("File name is still gzipped.", sf.getFile().getFileName().toString().endsWith(".gz"));

		Set<AnalysisFastQC> analyses = asRole(Role.ROLE_ADMIN, "tom").analysisService.getAnalysesForSequenceFile(sf,
				AnalysisFastQC.class);
		assertEquals("Only one analysis should be generated automatically.", 1, analyses.size());
		AnalysisFastQC analysis = analyses.iterator().next();

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
		assertEquals("Wrong number of directories beneath the id directory", 2, fileCount);
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testCreateSequenceFileInSample() throws IOException {
		Sample s = sampleService.read(1L);
		SequenceFile sf = new SequenceFile();
		Path sequenceFile = Files.createTempFile("TEMPORARY-SEQUENCE-FILE", ".gz");
		OutputStream gzOut = new GZIPOutputStream(Files.newOutputStream(sequenceFile));
		gzOut.write(FASTQ_FILE_CONTENTS);
		gzOut.close();

		sf.setFile(sequenceFile);
		sequenceFileService.createSequenceFileInSample(sf, s);

		SequencingRun mr = sequencingRunService.read(1L);
		sequencingRunService.addSequenceFileToSequencingRun(mr, sf);
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testGetSequencefilesForSampleAsSequencer() throws IOException {
		Sample s = sampleService.read(1L);
		List<Join<Sample, SequenceFile>> sequenceFilesForSample = sequenceFileService.getSequenceFilesForSample(s);
		assertEquals(1, sequenceFilesForSample.size());
	}
}
