package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.context.SecurityContextHolder;
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
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;
import ca.corefacility.bioinformatics.irida.service.SequenceFileService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiNoGalaxyTestConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/AnalysisServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisServiceImplIT {

	@Autowired
	private AnalysisService analysisService;

	@Autowired
	private SequenceFileService sequenceFileService;

	@Autowired
	@Qualifier("outputFileBaseDirectory")
	private Path outputFileBaseDirectory;

	private static final String EXECUTION_MANAGER_ID = "execution-manager-id";

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testCreatePhylogenomicsAnalysis() throws IOException {
		Path treePath = Files.createTempFile(null, null);
		Path tablePath = Files.createTempFile(null, null);
		Path matrixPath = Files.createTempFile(null, null);

		AnalysisOutputFile tree = new AnalysisOutputFile(treePath, "internal-galaxy-tree-identifier");
		AnalysisOutputFile table = new AnalysisOutputFile(tablePath, "internal-galaxy-table-identifier");
		AnalysisOutputFile matrix = new AnalysisOutputFile(matrixPath, "internal-galaxy-matrix-identifier");
		Map<String, AnalysisOutputFile> analysisOutputFiles = new ImmutableMap.Builder<String, AnalysisOutputFile>()
				.put("tree", tree).put("matrix", matrix).put("table", table).build();
		AnalysisPhylogenomicsPipeline pipeline = new AnalysisPhylogenomicsPipeline(Sets.newHashSet(),
				EXECUTION_MANAGER_ID, analysisOutputFiles);

		// make sure that we're not falsely putting the files into the correct
		// directory in the first place.
		assertFalse("file was stored in the wrong directory.",
				pipeline.getPhylogeneticTree().getFile().startsWith(outputFileBaseDirectory));
		assertFalse("file was stored in the wrong directory.",
				pipeline.getSnpMatrix().getFile().startsWith(outputFileBaseDirectory));
		assertFalse("file was stored in the wrong directory.",
				pipeline.getSnpTable().getFile().startsWith(outputFileBaseDirectory));

		Analysis analysis = analysisService.create(pipeline);

		// make sure that we put the analysis output files into the correct
		// directory.
		assertTrue("returned analysis was of the wrong type.", analysis instanceof AnalysisPhylogenomicsPipeline);
		AnalysisPhylogenomicsPipeline saved = (AnalysisPhylogenomicsPipeline) analysis;
		assertTrue("file was stored in the wrong directory.",
				saved.getPhylogeneticTree().getFile().startsWith(outputFileBaseDirectory));
		assertTrue("file was stored in the wrong directory.",
				saved.getSnpMatrix().getFile().startsWith(outputFileBaseDirectory));
		assertTrue("file was stored in the wrong directory.",
				saved.getSnpTable().getFile().startsWith(outputFileBaseDirectory));
	}

	@Test
	@WithMockUser(username = "fbristow", roles = "ADMIN")
	public void testGetAnalysesForSequenceFile() throws IOException {
		SequenceFile sf = sequenceFileService.read(1L);

		Path treePath = Files.createTempFile(null, null);
		Path tablePath = Files.createTempFile(null, null);
		Path matrixPath = Files.createTempFile(null, null);

		AnalysisOutputFile tree = new AnalysisOutputFile(treePath, "internal-galaxy-tree-identifier");
		AnalysisOutputFile table = new AnalysisOutputFile(tablePath, "internal-galaxy-table-identifier");
		AnalysisOutputFile matrix = new AnalysisOutputFile(matrixPath, "internal-galaxy-matrix-identifier");
		Map<String, AnalysisOutputFile> analysisOutputFiles = new ImmutableMap.Builder<String, AnalysisOutputFile>()
				.put("tree", tree).put("matrix", matrix).put("table", table).build();
		AnalysisPhylogenomicsPipeline pipeline = new AnalysisPhylogenomicsPipeline(Sets.newHashSet(),
				EXECUTION_MANAGER_ID, analysisOutputFiles);

		Analysis created = analysisService.create(pipeline);

		Set<Analysis> analyses = analysisService.getAnalysesForSequenceFile(sf);

		assertNotNull("Analyses should not be null.", analyses);
		Analysis saved = null;
		for (Analysis a : analyses) {
			if (a.getId().equals(created.getId())) {
				saved = a;
				break;
			}
		}
		assertEquals("analysis in set should be what we just persisted.", pipeline, saved);
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testListAnalysesAsUser() {
		SecurityContextHolder.getContext();
		Page<Analysis> analyses = analysisService.list(1, 1, Direction.DESC);
		assertEquals("number of analyses should be 1.", 1, analyses.getTotalElements());
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	public void testListAnalysesAsUserWithSort() {
		SecurityContextHolder.getContext();
		Page<Analysis> analyses = analysisService.list(1, 1, Direction.DESC, "executionManagerAnalysisId");
		assertEquals("number of analyses should be 1.", 1, analyses.getTotalElements());
	}
}
