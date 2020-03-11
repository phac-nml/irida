package ca.corefacility.bioinformatics.irida.ria.unit.web.oauth;

import java.net.MalformedURLException;
import java.security.Principal;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.HandlerMapping;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.oauth.OltuAuthorizationController;
import ca.corefacility.bioinformatics.irida.ria.web.oauth.RemoteAPIController;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class RemoteAPIControllerTest {
	private RemoteAPIController remoteAPIController;
	private RemoteAPIService remoteAPIService;
	private ProjectRemoteService projectRemoteService;
	private RemoteAPITokenService tokenService;
	private UserService userService;
	private OltuAuthorizationController authController;
	private MessageSource messageSource;

	private static final String USER_NAME = "testme";

	private Locale locale;

	@Before
	public void setUp() {
		remoteAPIService = mock(RemoteAPIService.class);
		messageSource = mock(MessageSource.class);
		projectRemoteService = mock(ProjectRemoteService.class);
		authController = mock(OltuAuthorizationController.class);
		tokenService = mock(RemoteAPITokenService.class);
		userService = mock(UserService.class);
		remoteAPIController = new RemoteAPIController(remoteAPIService, projectRemoteService, userService, tokenService,
				authController, messageSource);
		locale = LocaleContextHolder.getLocale();

	}

	@Test
	public void testList() {
		ExtendedModelMap model = new ExtendedModelMap();
		Principal principal = () -> USER_NAME;
		User user = new User();
		user.setSystemRole(Role.ROLE_ADMIN);

		when(userService.getUserByUsername(USER_NAME)).thenReturn(user);
		String list = remoteAPIController.list(model, principal);
		assertEquals(RemoteAPIController.CLIENTS_PAGE, list);
	}

	@Test
	public void testRemoveRemoteAPI() {
		Long id = 1L;

		String removeClient = remoteAPIController.removeClient(id);

		assertEquals("redirect:/remote_api", removeClient);

		verify(remoteAPIService).delete(id);
	}

	@Test
	public void testGetAddRemoteAPIPage() {
		ExtendedModelMap model = new ExtendedModelMap();

		String addClientPage = remoteAPIController.getAddRemoteAPIPage(model);

		assertEquals(RemoteAPIController.ADD_API_PAGE, addClientPage);
		assertTrue(model.containsAttribute("errors"));
	}

	@Test
	public void testPostCreateRemoteAPI() {
		RemoteAPI client = new RemoteAPI("name", "http://uri", "a description", "id", "secret");
		client.setId(1L);
		ExtendedModelMap model = new ExtendedModelMap();

		when(remoteAPIService.create(client)).thenReturn(client);

		String postCreateClient = remoteAPIController.postCreateRemoteAPI(client, model, locale);

		assertEquals("redirect:/remote_api/1", postCreateClient);
		verify(remoteAPIService).create(client);
	}

	@Test
	public void testPostCreateRemoteAPIError() {
		RemoteAPI client = new RemoteAPI("name", "http://uri", "a description", "id", "secret");
		client.setId(1L);
		ExtendedModelMap model = new ExtendedModelMap();
		Locale locale = LocaleContextHolder.getLocale();

		DataIntegrityViolationException ex = new DataIntegrityViolationException(
				"Error: " + RemoteAPI.SERVICE_URI_CONSTRAINT_NAME);

		when(remoteAPIService.create(client)).thenThrow(ex);

		String postCreateClient = remoteAPIController.postCreateRemoteAPI(client, model, locale);

		assertEquals(RemoteAPIController.ADD_API_PAGE, postCreateClient);
		assertTrue(model.containsAttribute("errors"));
		@SuppressWarnings("unchecked") Map<String, String> errors = (Map<String, String>) model.get("errors");
		assertTrue(errors.containsKey("serviceURI"));

		verify(remoteAPIService).create(client);
	}

	@Test(expected = IridaOAuthException.class)
	public void testConnectToAPI() {
		Long apiId = 1L;
		ExtendedModelMap model = new ExtendedModelMap();
		RemoteAPI client = new RemoteAPI("name", "http://uri", "a description", "id", "secret");
		when(remoteAPIService.read(apiId)).thenReturn(client);
		when(projectRemoteService.getServiceStatus(client)).thenThrow(new IridaOAuthException("invalid token", client));
		remoteAPIController.connectToAPI(apiId, model);
	}

	@Test
	public void testConnectToAPIActiveToken() {
		Long apiId = 1L;
		ExtendedModelMap model = new ExtendedModelMap();
		RemoteAPI client = new RemoteAPI("name", "http://uri", "a description", "id", "secret");
		when(remoteAPIService.read(apiId)).thenReturn(client);
		when(projectRemoteService.getServiceStatus(client)).thenReturn(true);
		String connectToAPI = remoteAPIController.connectToAPI(apiId, model);
		assertEquals(RemoteAPIController.PARENT_FRAME_RELOAD_PAGE, connectToAPI);
	}

	@Test
	public void testRead() {
		Long apiId = 1L;
		ExtendedModelMap model = new ExtendedModelMap();
		RemoteAPI client = new RemoteAPI("name", "http://uri", "a description", "id", "secret");
		RemoteAPIToken remoteAPIToken = new RemoteAPIToken("xyz", client, new Date());
		when(remoteAPIService.read(apiId)).thenReturn(client);
		when(tokenService.getToken(client)).thenReturn(remoteAPIToken);

		remoteAPIController.read(apiId, model, locale);

		verify(remoteAPIService).read(apiId);
		verify(tokenService).getToken(client);

		assertTrue(model.containsAttribute("remoteApi"));
	}

	@Test
	public void testReadNoToken() {
		Long apiId = 1L;
		ExtendedModelMap model = new ExtendedModelMap();
		RemoteAPI client = new RemoteAPI("name", "http://uri", "a description", "id", "secret");
		when(remoteAPIService.read(apiId)).thenReturn(client);
		when(tokenService.getToken(client)).thenThrow(new EntityNotFoundException("no token"));

		remoteAPIController.read(apiId, model, locale);

		verify(remoteAPIService).read(apiId);
		verify(tokenService).getToken(client);

		assertTrue(model.containsAttribute("remoteApi"));
	}

	@Test
	public void testHandleOAuthException() throws MalformedURLException, OAuthSystemException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		String redirect = "http://request";
		when(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn(redirect);

		RemoteAPI client = new RemoteAPI("name", "http://uri", "a description", "id", "secret");
		IridaOAuthException ex = new IridaOAuthException("msg", client);

		remoteAPIController.handleOAuthException(request, ex);
		verify(authController).authenticate(client, redirect);
	}
}
