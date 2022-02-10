package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;
import ca.corefacility.bioinformatics.irida.service.workflow.WorkflowNamedParametersService;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * Tests for named parameter service.
 */
@ServiceIntegrationTest
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/WorkflowNamedParametersServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class WorkflowNamedParametersServiceImplIT {

	@Autowired
	private WorkflowNamedParametersService namedParametersService;

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetNamedParametersForWorkflow() {
		final List<IridaWorkflowNamedParameters> namedParameters = namedParametersService
				.findNamedParametersForWorkflow(UUID.fromString("e47c1a8b-4ccd-4e56-971b-24c384933f44"));
		assertNotNull(namedParameters, "Should have loaded *some* parameters for the workflow.");
		assertEquals(2, namedParameters.size(), "Should have 2 named parameters for the worklflow.");
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetAllNamedParameters() {
		final Iterable<IridaWorkflowNamedParameters> namedParameters = namedParametersService.findAll();
		final Set<UUID> uuids = new HashSet<>();
		for (final IridaWorkflowNamedParameters param : namedParameters) {
			uuids.add(param.getWorkflowId());
		}
		assertEquals(2, uuids.size(), "Should be 2 unique IDs.");
	}
}
