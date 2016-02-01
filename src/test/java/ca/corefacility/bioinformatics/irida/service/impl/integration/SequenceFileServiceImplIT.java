package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.service.sample.SampleService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

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

}
