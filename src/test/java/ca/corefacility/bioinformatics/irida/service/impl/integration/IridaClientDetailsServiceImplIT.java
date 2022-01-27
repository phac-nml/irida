package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.service.impl.IridaClientDetailsServiceImpl;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
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
		assertEquals("testClient", loadClientByClientId.getClientId());
	}

	@Test(expected = NoSuchClientException.class)
	@WithMockUser(username = "anonymous", roles = "ANONYMOUS")
	public void testClientNotExists() {
		clientDetailsService.loadClientByClientId("badClient");
	}
}
