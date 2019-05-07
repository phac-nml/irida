package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.run.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun.LayoutType;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisFastQC;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

/**
 * Test for SequencingRunServiceImplIT. NOTE: This class uses a separate table
 * reset file at /ca/corefacility/bioinformatics/irida/service/impl/
 * SequencingRunServiceTableReset.xml
 * 
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SequencingRunServiceImplIT.xml")
@DatabaseTearDown({ "/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml" })
public class SequencingRunServiceImplIT {

	private static final Logger logger = LoggerFactory.getLogger(SequencingRunServiceImplIT.class);

	private static final String SEQUENCE = "ACGTACGTN";
	private static final byte[] FASTQ_FILE_CONTENTS = ("@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n"
			+ SEQUENCE + "\n+\n?????????").getBytes();
	@Autowired
	private SequencingRunService miseqRunService;

	@Autowired
	private SampleService sampleService;

	@Autowired
	private SequencingObjectService objectService;

	@Autowired
	private AnalysisService analysisService;

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "SEQUENCER")
	public void testAddSequenceFileToMiseqRunAsSequencer() throws IOException, InterruptedException {
		testAddSequenceFileToMiseqRun();
	}

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "ADMIN")
	public void testAddSequenceFileToMiseqRunAsAdmin() throws IOException, InterruptedException {
		testAddSequenceFileToMiseqRun();
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "fbristow", password = "password1", roles = "USER")
	public void testAddSequenceFileToMiseqRunAsUser() throws IOException, InterruptedException {
		testAddSequenceFileToMiseqRun();
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "fbristow", password = "password1", roles = "MANAGER")
	public void testAddSequenceFileToMiseqRunAsManager() throws IOException, InterruptedException {
		testAddSequenceFileToMiseqRun();
	}

	private void testAddSequenceFileToMiseqRun() throws IOException, InterruptedException {

		SequencingRun miseqRun = miseqRunService.read(1L);
		// we can't actually know a file name in the XML file that we use to
		// populate the database for these tests, so the files don't exist
		// anywhere. Create a new temp file and update that in the database
		// prior to adding a file to a miseq run so that we have something there
		// that we can link to.
		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);
		SequenceFile sf = new SequenceFile(sequenceFile);
		SequencingObject so = new SingleEndSequenceFile(sf);
		so = objectService.create(so);

		miseqRunService.addSequencingObjectToSequencingRun(miseqRun, so);
		SequencingRun saved = miseqRunService.read(1L);

		SequencingObject readObject = objectService.read(so.getId());

		Set<SequencingObject> sequencingObjectsForSequencingRun = objectService
				.getSequencingObjectsForSequencingRun(saved);
		assertTrue("Saved miseq run should have seqence file", sequencingObjectsForSequencingRun.contains(so));

		int maxWait = 20;
		int waits = 0;

		if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
				.anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
			AnalysisFastQC analysis = null;
			do {
				try {
					readObject = objectService.read(so.getId());
					SequenceFile readFile = readObject.getFiles().iterator().next();
					analysis = analysisService.getFastQCAnalysisForSequenceFile(readObject, readFile.getId());
				} catch (final EntityNotFoundException e) {
					waits++;
					if (waits > maxWait) {
						throw new RuntimeException("Waited too long for fastqc to run");
					}
					
					logger.info("Fastqc still isn't finished, sleeping a bit.");
					Thread.sleep(1000);
				}
			} while (analysis == null);

			assertNotNull("FastQC analysis should have been created for uploaded file.", analysis);
		}
	}

	@Test
	@WithMockUser(username = "sequencer", password = "password1", roles = "SEQUENCER")
	public void testCreateMiseqRunAsSequencer() {
		MiseqRun mr = new MiseqRun(LayoutType.PAIRED_END, "workflow");
		SequencingRun returned = miseqRunService.create(mr);
		assertNotNull("Created run was not assigned an ID.", returned.getId());
	}
	
	@Test
	@WithMockUser(username = "tech", password = "password1", roles = "TECHNICIAN")
	public void testReadMiseqRunAsTech() {
		SequencingRun mr = miseqRunService.read(1L);
		assertNotNull("Created run was not assigned an ID.", mr.getId());
	}


	@Test
	@WithMockUser(username = "sequencer", password = "password1", roles = "SEQUENCER")
	public void testReadMiseqRunAsSequencer() {
		SequencingRun mr = miseqRunService.read(1L);
		assertNotNull("Created run was not assigned an ID.", mr.getId());
	}

	@Test
	@WithMockUser(username = "user", password = "password1", roles = "USER")
	public void testCreateMiseqRunAsUser() {
		MiseqRun mr = new MiseqRun(LayoutType.PAIRED_END, "workflow");
		SequencingRun create = miseqRunService.create(mr);
		assertEquals("user", create.getUser().getUsername());
	}

	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "user", password = "password1", roles = "USER")
	public void testUpdateMiseqRunAsUserFail() {
		// run 2 is not owned by "user"
		SequencingRun mr = miseqRunService.read(2L);
		mr.setDescription("different description");
		miseqRunService.update(mr);
	}
	
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "tech", password = "password1", roles = "TECHNICIAN")
	public void testUpdateMiseqRunAsTechFail() {
		// run 2 is not owned by "user"
		SequencingRun mr = miseqRunService.read(2L);
		mr.setDescription("different description");
		miseqRunService.update(mr);
	}
	
	@Test(expected = AccessDeniedException.class)
	@WithMockUser(username = "tech", password = "password1", roles = "TECHNICIAN")
	public void testDeleteMiseqRunAsTechFail() {
		// run 2 is not owned by "user"
		SequencingRun mr = miseqRunService.read(2L);
		miseqRunService.delete(mr.getId());
	}

	@Test
	@WithMockUser(username = "user", password = "password1", roles = "USER")
	public void testUpdateMiseqRunAsUserSuccess() {
		// run 1 is owned by "user" so should be able to update
		SequencingRun mr = miseqRunService.read(1L);
		mr.setDescription("different description");
		miseqRunService.update(mr);
	}

	@Test
	@WithMockUser(username = "sequencer", password = "password1", roles = "SEQUENCER")
	public void testUpdateMiseqRunAsSequencer() {
		String newDescription = "a different description";
		SequencingRun mr = miseqRunService.read(1L);
		mr.setDescription(newDescription);
		
		SequencingRun update = miseqRunService.update(mr);
		assertEquals(update.getDescription(), newDescription);
	}

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "ADMIN")
	public void testCreateMiseqRunAsAdmin() {
		MiseqRun r = new MiseqRun(LayoutType.PAIRED_END, "workflow");
		miseqRunService.create(r);
	}

	@Test
	@WithMockUser(username = "user", password = "password1", roles = "USER")
	public void testFindAll() {
		Iterable<SequencingRun> findAll = miseqRunService.findAll();

		List<SequencingRun> runs = Lists.newArrayList(findAll);
		assertEquals("user should be able to see 1 run", 1, runs.size());
		SequencingRun run = runs.iterator().next();
		assertEquals("id should be 1", new Long(1), run.getId());
	}

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "ADMIN")
	public void testFindAllAdmin() {
		Iterable<SequencingRun> findAll = miseqRunService.findAll();

		List<SequencingRun> runs = Lists.newArrayList(findAll);
		assertEquals("user should be able to see all 5 runs", 5, runs.size());
	}
	
	@Test
	@WithMockUser(username = "tech", password = "password1", roles = "TECHNICIAN")
	public void testFindAllTech() {
		Iterable<SequencingRun> findAll = miseqRunService.findAll();

		List<SequencingRun> runs = Lists.newArrayList(findAll);
		assertEquals("technician should be able to see all 5 runs", 5, runs.size());
	}

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "ADMIN")
	public void testDeleteCascade() {
		assertTrue("Sequence file should exist before", objectService.exists(3L));
		assertTrue("file pair should exist before", objectService.exists(1L));
		miseqRunService.delete(2L);
		assertFalse("Sequence file should be deleted on cascade", objectService.exists(3L));
		assertFalse("file pair should not exist after", objectService.exists(1L));
		assertTrue("file 7 should not be deleted because it's in an analysis", objectService.exists(6L));
	}

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "ADMIN")
	public void testDeleteCascadeToSample() {
		assertTrue("Sequence file should exist before", objectService.exists(2L));
		miseqRunService.delete(3L);
		assertFalse("Sequence file should be deleted on cascade", objectService.exists(2L));
		assertFalse("Sequence file should be deleted on cascade", objectService.exists(4L));
		assertFalse("Sequence file should be deleted on cascade", objectService.exists(5L));
		assertFalse("Sample should be deleted on cascade", sampleService.exists(2L));
		assertTrue("This sample should not be removed", sampleService.exists(1L));
	}

	/**
	 * This test simulates a bug that happens from the REST API when uploading
	 * sequence files to samples, where a new sequence file is created, then
	 * detached from a transaction.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "ADMIN")
	public void testAddDetachedRunToSequenceFile() throws IOException, InterruptedException {
		final String SEQUENCE = "ACGTACGTN";
		final byte[] FASTQ_FILE_CONTENTS = ("@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n" + SEQUENCE
				+ "\n+\n?????????").getBytes();
		Path p = Files.createTempFile(null, null);
		Files.write(p, FASTQ_FILE_CONTENTS);

		SequenceFile sf = new SequenceFile();
		sf.setFile(p);
		SingleEndSequenceFile so = new SingleEndSequenceFile(sf);
		Sample sample = sampleService.read(1L);
		SequencingRun run = miseqRunService.read(2L);

		objectService.createSequencingObjectInSample(so, sample);

		miseqRunService.addSequencingObjectToSequencingRun(run, so);
		AnalysisFastQC analysis = null;
		do {
			try {
				analysis = analysisService.getFastQCAnalysisForSequenceFile(so, sf.getId());
			} catch (final EntityNotFoundException e) {
				logger.info("Fastqc still isn't finished, sleeping a bit.");
				Thread.sleep(1000);
			}
		} while (analysis == null);

		assertNotNull("FastQC analysis should have been created for sequence file.", analysis);
	}
}
