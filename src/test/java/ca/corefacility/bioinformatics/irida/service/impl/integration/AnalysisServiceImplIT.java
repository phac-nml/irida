package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithSecurityContextTestExcecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.Analysis;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisOutputFile;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.AnalysisPhylogenomicsPipeline;
import ca.corefacility.bioinformatics.irida.service.AnalysisService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/AnalysisServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class AnalysisServiceImplIT {

	@Autowired
	private AnalysisService analysisService;
	
	@Test
	public void testCreatePhylogenomicsAnalysis() throws IOException {
		AnalysisPhylogenomicsPipeline pipeline = new AnalysisPhylogenomicsPipeline(null);
		Path treePath = Files.createTempFile(null,  null);
		Path tablePath = Files.createTempFile(null,  null);
		Path matrixPath = Files.createTempFile(null,  null);
		
		AnalysisOutputFile tree = new AnalysisOutputFile(treePath, "internal-galaxy-tree-identifier");
		AnalysisOutputFile table = new AnalysisOutputFile(tablePath, "internal-galaxy-table-identifier");
		AnalysisOutputFile matrix = new AnalysisOutputFile(matrixPath, "internal-galaxy-matrix-identifier");
		
		pipeline.setPhylogeneticTree(tree);
		pipeline.setSnpMatrix(matrix);
		pipeline.setSnpTable(table);
		
		Analysis analysis = analysisService.create(pipeline);
		
		assertTrue("returned analysis was of the wrong type.", analysis instanceof AnalysisPhylogenomicsPipeline);
	}
}
