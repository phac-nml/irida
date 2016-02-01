package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SingleEndSequenceFile;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiNoGalaxyTestConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/SequenceFileServiceImplIT.xml")
@DatabaseTearDown(value = "/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml", type = DatabaseOperation.DELETE_ALL)
public class SequencingObjectServiceImplIT {

	private static final String SEQUENCE = "ACGTACGTN";
	private static final byte[] FASTQ_FILE_CONTENTS = ("@testread\n" + SEQUENCE + "\n+\n?????????\n@testread2\n"
			+ SEQUENCE + "\n+\n?????????").getBytes();

	@Autowired
	private SampleService sampleService;

	@Autowired
	private SequenceFileService sequenceFileService;

	@Autowired
	private SequencingObjectService objectService;

	@Autowired
	private SequencingRunService sequencingRunService;

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

		Collection<SampleSequencingObjectJoin> sequenceFilePairsForSample = objectService.getSequencesForSampleOfType(
				s, SequenceFilePair.class);
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
	public void testCreateSequenceFileInSample() throws IOException {
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
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "SEQUENCER")
	public void testGetSequencefilesForSampleAsSequencer() throws IOException {
		Sample s = sampleService.read(1L);
		Collection<SampleSequencingObjectJoin> sequencingObjectsForSample = objectService
				.getSequencingObjectsForSample(s);
		assertEquals(1, sequencingObjectsForSample.size());
	}

	// move
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

	// move
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
}
