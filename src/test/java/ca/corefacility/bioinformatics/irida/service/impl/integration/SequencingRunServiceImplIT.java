package ca.corefacility.bioinformatics.irida.service.impl.integration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
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

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for SequencingRunServiceImplIT.
 */
@ServiceIntegrationTest
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

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "USER")
	public void testAddSequenceFileToMiseqRunAsUser() throws IOException, InterruptedException {
		assertThrows(AccessDeniedException.class, () -> {
			testAddSequenceFileToMiseqRun();
		});
	}

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "MANAGER")
	public void testAddSequenceFileToMiseqRunAsManager() throws IOException, InterruptedException {
		assertThrows(AccessDeniedException.class, () -> {
			testAddSequenceFileToMiseqRun();
		});
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

		Set<SequencingObject> sequencingObjectsForSequencingRun = objectService.getSequencingObjectsForSequencingRun(
				saved);
		assertTrue(sequencingObjectsForSequencingRun.contains(so), "Saved miseq run should have seqence file");

		int maxWait = 20;
		int waits = 0;

		if (SecurityContextHolder.getContext()
				.getAuthentication()
				.getAuthorities()
				.stream()
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

			assertNotNull(analysis, "FastQC analysis should have been created for uploaded file.");
		}
	}

	@Test
	@WithMockUser(username = "sequencer", password = "password1", roles = "SEQUENCER")
	public void testCreateMiseqRunAsSequencer() {
		SequencingRun mr = new SequencingRun(LayoutType.PAIRED_END, "miseq");
		SequencingRun returned = miseqRunService.create(mr);
		assertNotNull(returned.getId(), "Created run was not assigned an ID.");
	}

	@Test
	@WithMockUser(username = "tech", password = "password1", roles = "TECHNICIAN")
	public void testReadMiseqRunAsTech() {
		SequencingRun mr = miseqRunService.read(1L);
		assertNotNull(mr.getId(), "Created run was not assigned an ID.");
	}

	@Test
	@WithMockUser(username = "sequencer", password = "password1", roles = "SEQUENCER")
	public void testReadMiseqRunAsSequencer() {
		SequencingRun mr = miseqRunService.read(1L);
		assertNotNull(mr.getId(), "Created run was not assigned an ID.");
	}

	@Test
	@WithMockUser(username = "user", password = "password1", roles = "USER")
	public void testCreateMiseqRunAsUser() {
		SequencingRun mr = new SequencingRun(LayoutType.PAIRED_END, "miseq");
		SequencingRun create = miseqRunService.create(mr);
		assertEquals(create.getUser().getUsername(), "user");
	}

	@Test
	@WithMockUser(username = "user", password = "password1", roles = "USER")
	public void testUpdateMiseqRunAsUserFail() {
		// run 2 is not owned by "user"
		SequencingRun mr = miseqRunService.read(2L);
		mr.setDescription("different description");
		assertThrows(AccessDeniedException.class, () -> {
			miseqRunService.update(mr);
		});
	}

	@Test
	@WithMockUser(username = "tech", password = "password1", roles = "TECHNICIAN")
	public void testUpdateMiseqRunAsTechFail() {
		// run 2 is not owned by "user"
		SequencingRun mr = miseqRunService.read(2L);
		mr.setDescription("different description");
		assertThrows(AccessDeniedException.class, () -> {
			miseqRunService.update(mr);
		});
	}

	@Test
	@WithMockUser(username = "tech", password = "password1", roles = "TECHNICIAN")
	public void testDeleteMiseqRunAsTechFail() {
		// run 2 is not owned by "user"
		SequencingRun mr = miseqRunService.read(2L);
		assertThrows(AccessDeniedException.class, () -> {
			miseqRunService.delete(mr.getId());
		});
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
		SequencingRun mr = new SequencingRun(LayoutType.PAIRED_END, "miseq");
		miseqRunService.create(mr);
	}

	@Test
	@WithMockUser(username = "user", password = "password1", roles = "USER")
	public void testFindAll() {
		Iterable<SequencingRun> findAll = miseqRunService.findAll();

		List<SequencingRun> runs = Lists.newArrayList(findAll);
		assertEquals(1, runs.size(), "user should be able to see 1 run");
		SequencingRun run = runs.iterator().next();
		assertEquals(Long.valueOf(1), run.getId(), "id should be 1");
	}

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "ADMIN")
	public void testFindAllAdmin() {
		Iterable<SequencingRun> findAll = miseqRunService.findAll();

		List<SequencingRun> runs = Lists.newArrayList(findAll);
		assertEquals(5, runs.size(), "user should be able to see all 5 runs");
	}

	@Test
	@WithMockUser(username = "tech", password = "password1", roles = "TECHNICIAN")
	public void testFindAllTech() {
		Iterable<SequencingRun> findAll = miseqRunService.findAll();

		List<SequencingRun> runs = Lists.newArrayList(findAll);
		assertEquals(5, runs.size(), "technician should be able to see all 5 runs");
	}

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "ADMIN")
	public void testDeleteCascade() {
		assertTrue(objectService.exists(3L), "Sequence file should exist before");
		assertTrue(objectService.exists(1L), "file pair should exist before");
		miseqRunService.delete(2L);
		assertFalse(objectService.exists(3L), "Sequence file should be deleted on cascade");
		assertFalse(objectService.exists(1L), "file pair should not exist after");
		assertTrue(objectService.exists(6L), "file 7 should not be deleted because it's in an analysis");
	}

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "ADMIN")
	public void testDeleteCascadeToSample() {
		assertTrue(objectService.exists(2L), "Sequence file should exist before");
		miseqRunService.delete(3L);
		assertFalse(objectService.exists(2L), "Sequence file should be deleted on cascade");
		assertFalse(objectService.exists(4L), "Sequence file should be deleted on cascade");
		assertFalse(objectService.exists(5L), "Sequence file should be deleted on cascade");
		assertFalse(sampleService.exists(2L), "Sample should be deleted on cascade");
		assertTrue(sampleService.exists(1L), "This sample should not be removed");
	}

	@Test
	@WithMockUser(username = "fbristow", password = "password1", roles = "ADMIN")
	@Disabled
	public void testDeleteFilesOnFilesystem() throws IOException {
		SequencingRun miseqRun = miseqRunService.read(1L);
		Path sequenceFile = Files.createTempFile(null, null);
		Files.write(sequenceFile, FASTQ_FILE_CONTENTS);
		SequenceFile sf = new SequenceFile(sequenceFile);
		SequencingObject so = new SingleEndSequenceFile(sf);
		so = objectService.create(so);
		miseqRunService.addSequencingObjectToSequencingRun(miseqRun, so);
		assertTrue(Files.exists(sf.getFile()), "Sequence file should exist on the file system");
		miseqRunService.delete(1L);
		assertFalse(Files.exists(sf.getFile()), "Sequence file should not exist on the file system");
	}

	/**
	 * This test simulates a bug that happens from the REST API when uploading sequence files to samples, where a new
	 * sequence file is created, then detached from a transaction.
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

		assertNotNull(analysis, "FastQC analysis should have been created for sequence file.");
	}
}
