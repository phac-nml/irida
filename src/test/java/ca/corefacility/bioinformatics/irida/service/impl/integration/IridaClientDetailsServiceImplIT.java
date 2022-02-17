package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.test.context.support.WithMockUser;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.service.impl.IridaClientDetailsServiceImpl;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@ServiceIntegrationTest
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/IridaClientDetailsServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class IridaClientDetailsServiceImplIT {
	@Autowired
	private IridaClientDetailsServiceImpl clientDetailsService;

	@Test
	@WithMockUser(username = "anonymous", roles = "ANONYMOUS")
	public void testReadClientDetailsAnonymous() {
		ClientDetails loadClientByClientId = clientDetailsService.loadClientByClientId("testClient");
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
