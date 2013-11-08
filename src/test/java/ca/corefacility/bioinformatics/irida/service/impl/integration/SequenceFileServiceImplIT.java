package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import ca.corefacility.bioinformatics.irida.config.data.jpa.TestJpaProperties;
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
		IridaApiTestDataSourceConfig.class, TestJpaProperties.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class SequenceFileServiceImplIT {

	private static final String SEQUENCE = "ACGTACGTN";
	private static final byte[] FASTQ_FILE_CONTENTS = ("@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n"
			+ SEQUENCE + "\n+\n?????????").getBytes();
	private static final Logger logger = LoggerFactory.getLogger(SequenceFileServiceImplIT.class);

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
		u.setSystemRole(Role.ROLE_ADMIN);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, "Password1",
				ImmutableList.of(Role.ROLE_ADMIN));
		auth.setDetails(u);
		SecurityContextHolder.getContext().setAuthentication(auth);
		Files.createDirectories(Paths.get("/tmp", "sequence-files"));
	}

	@After
	public void tearDown() throws IOException {
		Files.walkFileTree(Paths.get("/tmp", "sequence-files"), new RecursiveDeleteVisitor());
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SequenceFileServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/SequenceFileServiceImplIT.xml")
	public void testCreateNotCompressedSequenceFile() throws IOException {
		logger.debug("STARTING TEST ON UNCOMPRESSED FILE >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		SequenceFile sf = new SequenceFile();
		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);
		sf.setFile(sequenceFile);

		logger.debug("About to save the file.");
		sf = sequenceFileService.create(sf);
		logger.debug("Finished saving the file.");

		assertNotNull("ID wasn't assigned.", sf.getId());

		// figure out what the version number of the sequence file is (should be
		// 2; the file wasn't gzipped, but fastqc will have modified it.)
		sf = sequenceFileService.read(sf.getId());
		assertEquals("Wrong version number after processing.", Long.valueOf(2), sf.getFileRevisionNumber());

		List<Join<SequenceFile, OverrepresentedSequence>> overrepresentedSequences = overrepresentedSequenceService
				.getOverrepresentedSequencesForSequenceFile(sf);
		assertNotNull("No overrepresented sequences were found.", overrepresentedSequences);
		assertEquals("Wrong number of overrepresented sequences were found.", 1, overrepresentedSequences.size());
		OverrepresentedSequence overrepresentedSequence = overrepresentedSequences.iterator().next().getObject();
		assertEquals("Sequence was not the correct sequence.", SEQUENCE, overrepresentedSequence.getSequence());
		assertEquals("The count was not correct.", 2, overrepresentedSequence.getOverrepresentedSequenceCount());
		assertEquals("The percent was not correct.", new BigDecimal("100.00"), overrepresentedSequence.getPercentage());
	}

	@Test
	@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SequenceFileServiceImplIT.xml")
	@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/service/impl/SequenceFileServiceImplIT.xml")
	public void testCreateCompressedSequenceFile() throws IOException {
		logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< STARTING TEST ON COMPRESSED FILE");

		SequenceFile sf = new SequenceFile();
		Path sequenceFile = Files.createTempFile(null, ".gz");
		OutputStream gzOut = new GZIPOutputStream(Files.newOutputStream(sequenceFile));
		gzOut.write(FASTQ_FILE_CONTENTS);
		gzOut.close();

		sf.setFile(sequenceFile);

		logger.debug("About to save the file.");
		sf = sequenceFileService.create(sf);
		logger.debug("Finished saving the file.");

		assertNotNull("ID wasn't assigned.", sf.getId());

		// figure out what the version number of the sequence file is (should be
		// 2; the file was gzipped)
		// get the MOST RECENT version of the sequence file from the database
		// (it will have been modified outside of the create method.)
		sf = sequenceFileService.read(sf.getId());
		assertEquals("Wrong version number after processing.", Long.valueOf(2L), sf.getFileRevisionNumber());

		List<Join<SequenceFile, OverrepresentedSequence>> overrepresentedSequences = overrepresentedSequenceService
				.getOverrepresentedSequencesForSequenceFile(sf);
		assertNotNull("No overrepresented sequences were found.", overrepresentedSequences);
		assertEquals("Wrong number of overrepresented sequences were found.", 1, overrepresentedSequences.size());
		OverrepresentedSequence overrepresentedSequence = overrepresentedSequences.iterator().next().getObject();
		assertEquals("Sequence was not the correct sequence.", SEQUENCE, overrepresentedSequence.getSequence());
		assertEquals("The count was not correct.", 2, overrepresentedSequence.getOverrepresentedSequenceCount());
		assertEquals("The percent was not correct.", new BigDecimal("100.00"), overrepresentedSequence.getPercentage());
	}
}
