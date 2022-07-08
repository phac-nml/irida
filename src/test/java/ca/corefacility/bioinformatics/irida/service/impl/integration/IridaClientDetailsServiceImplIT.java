package ca.corefacility.bioinformatics.irida.service.impl.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.test.context.support.WithMockUser;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.service.impl.IridaClientDetailsServiceImpl;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import static org.junit.jupiter.api.Assertions.*;

@ServiceIntegrationTest
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/IridaClientDetailsServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class IridaClientDetailsServiceImplIT {
	@Autowired
	private IridaClientDetailsServiceImpl clientDetailsService;

	@Test
	@WithMockUser(username = "anonymous", roles = "ANONYMOUS")
	public void testReadClientDetailsAnonymous() {
		IridaClientDetails loadClientByClientId = clientDetailsService.loadClientByClientId("testClient");
		assertNotNull(loadClientByClientId);
		assertEquals(loadClientByClientId.getClientId(), "testClient");
	}

	@Test
	@WithMockUser(username = "anonymous", roles = "ANONYMOUS")
	public void testClientNotExists() {
		assertThrows(NoSuchClientException.class, () -> {
			clientDetailsService.loadClientByClientId("badClient");
		});
	}
}
