package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;
import ca.corefacility.bioinformatics.irida.service.workflow.WorkflowNamedParametersService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

/**
 * Tests for named parameter service.
 * 
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
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
		assertNotNull("Should have loaded *some* parameters for the workflow.", namedParameters);
		assertEquals("Should have 2 named parameters for the worklflow.", 2, namedParameters.size());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	public void testGetAllNamedParameters() {
		final Iterable<IridaWorkflowNamedParameters> namedParameters = namedParametersService.findAll();
		final Set<UUID> uuids = new HashSet<>();
		for (final IridaWorkflowNamedParameters param : namedParameters) {
			uuids.add(param.getWorkflowId());
		}
		assertEquals("Should be 2 unique IDs.", 2, uuids.size());
	}
}
