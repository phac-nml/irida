package ca.corefacility.bioinformatics.irida.repositories.workflow.integration;

import static org.junit.Assert.*;

import org.junit.Before;
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

import ca.corefacility.bioinformatics.irida.config.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.repositories.workflow.RemoteWorkflowRepository;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
	WithSecurityContextTestExcecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/analysis/AnalysisRepositoryIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class RemoteWorkflowRepositoryIT {

	@Autowired
	private RemoteWorkflowRepository remoteWorkflowRepository;
	
	private String workflowId;
	private RemoteWorkflowPhylogenomics remoteWorkflowPhylogenomics;
	
	/**
	 * Sets up variables for test
	 */
	@Before
	public void setup() {
		workflowId = "5f5";
		String workflowChecksum = "55";
		
		String inputSequenceFilesLabel = "input_sequences";
		String inputReferenceFileLabel = "input_reference";
		
		String outputPhylogeneticTreeName = "tree.txt";
		String outputSnpMatrixName = "matrix.tsv";
		String outputSnpTableName = "table.tsv";
		
		remoteWorkflowPhylogenomics = 
				new RemoteWorkflowPhylogenomics(workflowId, workflowChecksum, inputSequenceFilesLabel,
						inputReferenceFileLabel, outputPhylogeneticTreeName, outputSnpMatrixName,
						outputSnpTableName);
	}
	
	/**
	 * Tests saving a remote workflow and re-loading it.
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testSaveRemoteWorkflowPhylogenomics() {		
		remoteWorkflowRepository.save(remoteWorkflowPhylogenomics);
		
		RemoteWorkflowPhylogenomics savedWorkflow = 
				remoteWorkflowRepository.getByType(workflowId, RemoteWorkflowPhylogenomics.class);
		
		assertEquals(remoteWorkflowPhylogenomics, savedWorkflow);
	}
	
	/**
	 * Tests succeeding to get a remote workflow
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetRemoteWorkflowSuccess() {		
		RemoteWorkflowPhylogenomics workflow = 
				remoteWorkflowRepository.getByType("1", RemoteWorkflowPhylogenomics.class);
		assertNotNull(workflow);
	}
	
	/**
	 * Tests failing to get a remote workflow
	 */
	@Test
	@WithMockUser(username = "aaron", roles = "ADMIN")
	public void testGetRemoteWorkflowFail() {		
		RemoteWorkflowPhylogenomics workflow = 
				remoteWorkflowRepository.getByType("invalid", RemoteWorkflowPhylogenomics.class);
		assertNull(workflow);
	}
}
