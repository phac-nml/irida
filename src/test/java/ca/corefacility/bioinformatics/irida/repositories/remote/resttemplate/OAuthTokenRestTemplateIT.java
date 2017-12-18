package ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.net.URI;
import java.net.URISyntaxException;

import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.client.MockRestServiceServer;

import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiJdbcDataSourceConfig.class })
@ActiveProfiles("it")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/repositories/remote/resttemplate/OAuthTokenRestTemplateIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class OAuthTokenRestTemplateIT {
	@Autowired
	private RemoteAPITokenService tokenService;
	@Autowired
	private RemoteAPIService apiService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Before
	public void setUp() throws URISyntaxException {
		//put the user in the security context
		User u = new User();
		u.setUsername("tom");
		u.setPassword(passwordEncoder.encode("Password1!"));
		u.setSystemRole(Role.ROLE_USER);

		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, "Password1!",
				ImmutableList.of(Role.ROLE_USER));
		auth.setDetails(u);
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	@Test
	public void testOAuthRequest() throws URISyntaxException {
		RemoteAPI remoteAPI = apiService.read(1L);
		URI serviceURI = new URI(remoteAPI.getServiceURI());
		OAuthTokenRestTemplate restTemplate = new OAuthTokenRestTemplate(tokenService, remoteAPI);

		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);

		String responseMessage = "{ \"message\" : \"all good\" }";

		mockServer.expect(requestTo(serviceURI)).andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(responseMessage, MediaType.APPLICATION_JSON));

		ResponseEntity<String> forEntity = restTemplate.getForEntity(serviceURI, String.class);
		assertNotNull(forEntity);
		assertEquals(responseMessage, forEntity.getBody());
		mockServer.verify();
	}

	@Test(expected = IridaOAuthException.class)
	public void testRequestWithExpiredToken() throws URISyntaxException {
		RemoteAPI remoteAPI = apiService.read(2L);
		URI serviceURI = new URI(remoteAPI.getServiceURI());
		OAuthTokenRestTemplate restTemplate = new OAuthTokenRestTemplate(tokenService, remoteAPI);

		restTemplate.getForEntity(serviceURI, String.class);
	}

	@Test(expected = IridaOAuthException.class)
	public void testRequestWithNoToken() throws URISyntaxException {
		RemoteAPI remoteAPI = apiService.read(3L);
		OAuthTokenRestTemplate restTemplate = new OAuthTokenRestTemplate(tokenService, remoteAPI);
		URI serviceURI = new URI(remoteAPI.getServiceURI());

		restTemplate.getForEntity(serviceURI, String.class);
	}

	@Test(expected = IridaOAuthException.class)
	public void testRequestWithBadToken() throws URISyntaxException {
		RemoteAPI remoteAPI = apiService.read(1L);
		URI serviceURI = new URI(remoteAPI.getServiceURI());
		OAuthTokenRestTemplate restTemplate = new OAuthTokenRestTemplate(tokenService, remoteAPI);

		MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);
		
		mockServer.expect(requestTo(serviceURI)).andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.UNAUTHORIZED));

		restTemplate.getForEntity(serviceURI, String.class);
	}
}
