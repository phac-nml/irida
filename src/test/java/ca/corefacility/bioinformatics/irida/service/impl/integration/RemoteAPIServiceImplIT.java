package ca.corefacility.bioinformatics.irida.service.impl.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@SpringBootTest
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class,
		WithSecurityContextTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/RemoteAPIServiceIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class RemoteAPIServiceImplIT {

	@Autowired
	RemoteAPIService remoteAPIService;

	@Test
	@WithMockUser(username = "tom", roles = "USER")
	public void testGetApiByURL() {
		String resourceURL = "http://irida.ca/api/projects/2";
		RemoteAPI apiForUrl = remoteAPIService.getRemoteAPIForUrl(resourceURL);
		assertNotNull(apiForUrl);

		assertTrue(resourceURL.contains(apiForUrl.getServiceURI()));

	}

	@Test
	@WithMockUser(username = "tom", roles = "USER")
	public void testGetApiByURLNoMatch() {
		String resourceURL = "http://somewhereelse.ca/api/projects/2";
		RemoteAPI apiForUrl = remoteAPIService.getRemoteAPIForUrl(resourceURL);
		assertNull(apiForUrl);
	}
}
