package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.service.OverrepresentedSequenceService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.utils.RecursiveDeleteVisitor;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class SequenceFileServiceImplIT {

	private static final String SEQUENCE = "ACGTACGTN";
	private static final byte[] FASTQ_FILE_CONTENTS = ("@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n"
			+ SEQUENCE + "\n+\n?????????").getBytes();
	private static final Logger logger = LoggerFactory.getLogger(SequenceFileServiceImplIT.class);
	private static final Path BASE_DIRECTORY = Paths.get("/tmp", "sequence-files");

	@Autowired
	private SequenceFileService sequenceFileService;
	@Autowired
	private OverrepresentedSequenceService overrepresentedSequenceService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Before
	public void setUp() throws IOException {
		User u = new User();
		u.setUsername("fbristow");
		u.setPassword(passwordEncoder.encode("Password1"));
		u.setSystemRole(Role.ROLE_SEQUENCER);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, "Password1",
				ImmutableList.of(Role.ROLE_SEQUENCER));
		auth.setDetails(u);
		SecurityContextHolder.getContext().setAuthentication(auth);
		Files.createDirectories(BASE_DIRECTORY);
	}

	@After
	public void tearDown() throws IOException {
		Files.walkFileTree(BASE_DIRECTORY, new RecursiveDeleteVisitor());
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SequenceFileServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/SequenceFileServiceImplIT.xml")
	public void testCreateNotCompressedSequenceFile() throws IOException {
		SequenceFile sf = new SequenceFile();
		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);
		sf.setFile(sequenceFile);

		logger.trace("About to save the file.");
		sf = sequenceFileService.create(sf);
		logger.trace("Finished saving the file.");

		assertNotNull("ID wasn't assigned.", sf.getId());

		// figure out what the version number of the sequence file is (should be
		// 2; the file wasn't gzipped, but fastqc will have modified it.)
		sf = asRole(Role.ROLE_ADMIN).sequenceFileService.read(sf.getId());
		assertEquals("Wrong version number after processing.", Long.valueOf(1), sf.getFileRevisionNumber());

		List<Join<SequenceFile, OverrepresentedSequence>> overrepresentedSequences = overrepresentedSequenceService
				.getOverrepresentedSequencesForSequenceFile(sf);
		assertNotNull("No overrepresented sequences were found.", overrepresentedSequences);
		assertEquals("Wrong number of overrepresented sequences were found.", 1, overrepresentedSequences.size());
		OverrepresentedSequence overrepresentedSequence = overrepresentedSequences.iterator().next().getObject();
		assertEquals("Sequence was not the correct sequence.", SEQUENCE, overrepresentedSequence.getSequence());
		assertEquals("The count was not correct.", 2, overrepresentedSequence.getOverrepresentedSequenceCount());
		assertEquals("The percent was not correct.", new BigDecimal("100.00"), overrepresentedSequence.getPercentage());

		// confirm that the file structure is correct
		Path idDirectory = BASE_DIRECTORY.resolve(Paths.get(sf.getId().toString()));
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
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SequenceFileServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/SequenceFileServiceImplIT.xml")
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
		sf = asRole(Role.ROLE_ADMIN).sequenceFileService.read(sf.getId());
		assertEquals("Wrong version number after processing.", Long.valueOf(2L), sf.getFileRevisionNumber());

		List<Join<SequenceFile, OverrepresentedSequence>> overrepresentedSequences = overrepresentedSequenceService
				.getOverrepresentedSequencesForSequenceFile(sf);
		assertNotNull("No overrepresented sequences were found.", overrepresentedSequences);
		assertEquals("Wrong number of overrepresented sequences were found.", 1, overrepresentedSequences.size());
		OverrepresentedSequence overrepresentedSequence = overrepresentedSequences.iterator().next().getObject();
		assertEquals("Sequence was not the correct sequence.", SEQUENCE, overrepresentedSequence.getSequence());
		assertEquals("The count was not correct.", 2, overrepresentedSequence.getOverrepresentedSequenceCount());
		assertEquals("The percent was not correct.", new BigDecimal("100.00"), overrepresentedSequence.getPercentage());

		// confirm that the file structure is correct
		String filename = sequenceFile.getFileName().toString();
		filename = filename.substring(0, filename.lastIndexOf('.'));
		Path idDirectory = BASE_DIRECTORY.resolve(Paths.get(sf.getId().toString()));
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

	private SequenceFileServiceImplIT asRole(Role r) {
		User u = new User();
		u.setUsername("fbristow");
		u.setPassword(passwordEncoder.encode("Password1"));
		u.setSystemRole(r);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, "Password1",
				ImmutableList.of(r));
		auth.setDetails(u);
		SecurityContextHolder.getContext().setAuthentication(auth);
		return this;
	}
}
