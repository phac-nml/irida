package ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.unit;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.service.workflow.galaxy.phylogenomics.impl.RemoteWorkflowServicePhylogenomics;

/**
 * Tests for RemoteWorkflowServiceGalaxyPhylogenomics.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class RemoteWorkflowServiceGalaxyPhylogenomicsTest {

	@Mock private RemoteWorkflowPhylogenomics currentWorkflow;
	
	private RemoteWorkflowServicePhylogenomics workflowService;
	
	/**
	 * Sets up variables for test.
	 */
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		workflowService = new RemoteWorkflowServicePhylogenomics(currentWorkflow);
	}
	
	/**
	 * Tests case of getting the current workflow successfully.
	 */
	@Test
	public void testGetCurrentWorkflowSuccess() {
		assertEquals(currentWorkflow, workflowService.getCurrentWorkflow());
	}
}
