package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.ToolExecution;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.BuiltInAnalysisTypes;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/AnalysisServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisServiceImplIT {
	
	private static final String MATRIX_KEY = "matrix";
	private static final String TREE_KEY = "tree";
	private static final String TABLE_KEY = "table";

	@Autowired
	private AnalysisService analysisService;

	@Autowired
	@Qualifier("outputFileBaseDirectory")
	private Path outputFileBaseDirectory;

	private static final String EXECUTION_MANAGER_ID = "execution-manager-id";

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testCreatePhylogenomicsAnalysis() throws IOException {
		Path treePath = Files.createTempFile(null, null);
		Path tablePath = Files.createTempFile(null, null);
		Path matrixPath = Files.createTempFile(null, null);

		Map<String, String> params = new HashMap<>();
		params.put("param", "value");
		ToolExecution toolExecutionTree = new ToolExecution(null, "ls", "1.0", "executionManagerId", params, "/bin/ls -lrth");
		ToolExecution toolExecutionTable = new ToolExecution(null, "ls", "1.0", "executionManagerId", params, "/bin/ls -lrth");
		ToolExecution toolExecutionMatrix = new ToolExecution(null, "ls", "1.0", "executionManagerId", params, "/bin/ls -lrth");

		AnalysisOutputFile tree = new AnalysisOutputFile(treePath, "internal-galaxy-tree-identifier", "", toolExecutionTree);
		AnalysisOutputFile table = new AnalysisOutputFile(tablePath, "internal-galaxy-table-identifier", "", toolExecutionTable);
		AnalysisOutputFile matrix = new AnalysisOutputFile(matrixPath, "internal-galaxy-matrix-identifier", "", toolExecutionMatrix);
		Map<String, AnalysisOutputFile> analysisOutputFiles = new ImmutableMap.Builder<String, AnalysisOutputFile>()
				.put("tree", tree).put("matrix", matrix).put("table", table).build();
		Analysis pipeline = new Analysis(EXECUTION_MANAGER_ID,
				analysisOutputFiles, BuiltInAnalysisTypes.PHYLOGENOMICS);

		// make sure that we're not falsely putting the files into the correct
		// directory in the first place.
		assertFalse("file was stored in the wrong directory.",
				pipeline.getAnalysisOutputFile(TREE_KEY).getFile().startsWith(outputFileBaseDirectory));
		assertFalse("file was stored in the wrong directory.",
				pipeline.getAnalysisOutputFile(MATRIX_KEY).getFile().startsWith(outputFileBaseDirectory));
		assertFalse("file was stored in the wrong directory.",
				pipeline.getAnalysisOutputFile(TABLE_KEY).getFile().startsWith(outputFileBaseDirectory));

		Analysis analysis = analysisService.create(pipeline);

		// make sure that we put the analysis output files into the correct
		// directory.
		assertEquals("returned analysis was of the wrong type.", BuiltInAnalysisTypes.PHYLOGENOMICS,
				analysis.getAnalysisType());
		assertTrue("file was stored in the wrong directory.",
				analysis.getAnalysisOutputFile(TREE_KEY).getFile().startsWith(outputFileBaseDirectory));
		assertTrue("file was stored in the wrong directory.",
				analysis.getAnalysisOutputFile(MATRIX_KEY).getFile().startsWith(outputFileBaseDirectory));
		assertTrue("file was stored in the wrong directory.",
				analysis.getAnalysisOutputFile(TABLE_KEY).getFile().startsWith(outputFileBaseDirectory));
	}
}
