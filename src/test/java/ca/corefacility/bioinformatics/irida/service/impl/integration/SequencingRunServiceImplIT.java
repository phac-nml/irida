package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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
import ca.corefacility.bioinformatics.irida.model.SequencingRunEntity;
import ca.corefacility.bioinformatics.irida.model.run.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableMap;

/**
 * Test for SequencingRunServiceImplIT. NOTE: This class uses a separate table
 * reset file at /ca/corefacility/bioinformatics/irida/service/impl/
 * SequencingRunServiceTableReset.xml
 * 
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiNoGalaxyTestConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SequencingRunServiceImplIT.xml")
@DatabaseTearDown({ "/ca/corefacility/bioinformatics/irida/service/impl/SequencingRunServiceTableReset.xml" })
public class SequencingRunServiceImplIT {
	private static final String SEQUENCE = "ACGTACGTN";
	private static final byte[] FASTQ_FILE_CONTENTS = ("@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n"
			+ SEQUENCE + "\n+\n?????????").getBytes();
	@Autowired
	private SequencingRunService miseqRunService;
	@Autowired
	private SequenceFileService sequenceFileService;
	@Autowired
	private SampleService sampleService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AnalysisService analysisService;

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "SEQUENCER")
	public void testAddSequenceFileToMiseqRunAsSequencer() throws IOException {
		testAddSequenceFileToMiseqRun();
	}

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "ADMIN")
	public void testAddSequenceFileToMiseqRunAsAdmin() throws IOException {
		testAddSequenceFileToMiseqRun();
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "fbristow", password = "password1", roles = "USER")
	public void testAddSequenceFileToMiseqRunAsUser() throws IOException {
		testAddSequenceFileToMiseqRun();
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "fbristow", password = "password1", roles = "MANAGER")
	public void testAddSequenceFileToMiseqRunAsManager() throws IOException {
		testAddSequenceFileToMiseqRun();
	}

	private void testAddSequenceFileToMiseqRun() throws IOException {
		SequenceFile sf = sequenceFileService.read(1l);
		SequencingRun miseqRun = miseqRunService.read(1l);
		// we can't actually know a file name in the XML file that we use to
		// populate the database for these tests, so the files don't exist
		// anywhere. Create a new temp file and update that in the database
		// prior to adding a file to a miseq run so that we have something there
		// that we can link to.
		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);
		sf.setFile(sequenceFile);
		sequenceFileService.update(1l, ImmutableMap.of("file", sequenceFile));
		miseqRunService.addSequenceFileToSequencingRun(miseqRun, sf);
		SequencingRun saved = miseqRunService.read(1l);
		SequenceFile savedFile = sequenceFileService.read(1l);
		Set<SequenceFile> sequenceFilesForMiseqRun = sequenceFileService.getSequenceFilesForSequencingRun(saved);
		assertTrue("Saved miseq run should have seqence file", sequenceFilesForMiseqRun.contains(savedFile));

		AnalysisFastQC analysis = savedFile.getFastQCAnalysis();
		assertNotNull("FastQC analysis should have been created for uploaded file.", analysis);
	}

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "ADMIN")
	public void testGetMiseqRunForSequenceFile() {
		SequenceFile sf = sequenceFileService.read(2l);

		try {
			SequencingRun j = miseqRunService.getSequencingRunForSequenceFile(sf);
			assertEquals("Join had wrong miseq run.", Long.valueOf(2l), j.getId());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Test failed for unknown reason.");
		}
	}

	@Test
	@WithMockUser(username = "sequencer", password = "password1", roles = "SEQUENCER")
	public void testCreateMiseqRunAsSequencer() {
		MiseqRun mr = new MiseqRun();
		mr.setWorkflow("Workflow name.");
		SequencingRun returned = miseqRunService.create(mr);
		assertNotNull("Created run was not assigned an ID.", returned.getId());
	}

	@Test
	@WithMockUser(username = "sequencer", password = "password1", roles = "SEQUENCER")
	public void testReadMiseqRunAsSequencer() {
		SequencingRun mr = miseqRunService.read(1L);
		assertNotNull("Created run was not assigned an ID.", mr.getId());
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "user", password = "password1", roles = "USER")
	public void testCreateMiseqRunAsUserFail() {
		MiseqRun mr = new MiseqRun();
		mr.setWorkflow("Workflow name.");
		miseqRunService.create(mr);
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "user", password = "password1", roles = "USER")
	public void testUpdateMiseqRunAsUserFail() {
		SequencingRun mr = miseqRunService.read(1L);
		miseqRunService.update(mr.getId(), ImmutableMap.of("description", "a different description"));
	}

	@Test
	@WithMockUser(username = "sequencer", password = "password1", roles = "SEQUENCER")
	public void testUpdateMiseqRunAsSequencer() {
		String newDescription = "a different description";
		SequencingRun mr = miseqRunService.read(1L);
		SequencingRun update = miseqRunService.update(mr.getId(), ImmutableMap.of("description", newDescription));
		assertEquals(update.getDescription(), newDescription);
	}

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "ADMIN")
	public void testCreateMiseqRunAsAdmin() {
		MiseqRun r = new MiseqRun();
		r.setWorkflow("some workflow");
		miseqRunService.create(r);
	}

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "ADMIN")
	public void testDeleteCascadeToSequenceFile() {
		assertTrue("Sequence file should exist before", sequenceFileService.exists(2L));
		miseqRunService.delete(2L);
		assertFalse("Sequence file should be deleted on cascade", sequenceFileService.exists(2L));
	}

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "ADMIN")
	public void testDeleteCascadeToSample() {
		assertTrue("Sequence file should exist before", sequenceFileService.exists(1L));
		miseqRunService.delete(3L);
		assertFalse("Sequence file should be deleted on cascade", sequenceFileService.exists(1L));
		assertFalse("Sequence file should be deleted on cascade", sequenceFileService.exists(3L));
		assertFalse("Sequence file should be deleted on cascade", sequenceFileService.exists(4L));
		assertFalse("Sample should be deleted on cascade", sampleService.exists(2L));
		assertTrue("This sample should not be removed", sampleService.exists(1L));
	}

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "ADMIN")
	public void testListAllSequencingRuns() {
		Iterable<SequencingRun> findAll = miseqRunService.findAll();
		assertNotNull(findAll);
		boolean foundMiseq = false;
		boolean foundTestEntity = false;
		for (SequencingRun run : findAll) {
			assertNotNull(run);
			if (run instanceof MiseqRun) {
				foundMiseq = true;
			} else if (run instanceof SequencingRunEntity) {
				foundTestEntity = true;
			}
		}

		assertTrue(foundMiseq);
		assertTrue(foundTestEntity);
	}

	/**
	 * This test simulates a bug that happens from the REST API when uploading
	 * sequence files to samples, where a new sequence file is created, then
	 * detached from a transaction.
	 * 
	 * @throws IOException
	 */
	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "ADMIN")
	public void testAddDetachedRunToSequenceFile() throws IOException {
		final String SEQUENCE = "ACGTACGTN";
		final byte[] FASTQ_FILE_CONTENTS = ("@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n" + SEQUENCE + "\n+\n?????????")
				.getBytes();
		Path p = Files.createTempFile(null, null);
		Files.write(p, FASTQ_FILE_CONTENTS);

		SequenceFile sf = new SequenceFile();
		sf.setFile(p);
		Sample sample = sampleService.read(1L);
		SequencingRun run = miseqRunService.read(2L);

		sequenceFileService.createSequenceFileInSample(sf, sample);

		miseqRunService.addSequenceFileToSequencingRun(run, sf);

		AnalysisFastQC analysis = sf.getFastQCAnalysis();
		assertNotNull("FastQC analysis should have been created for sequence file.", analysis);
	}
}
