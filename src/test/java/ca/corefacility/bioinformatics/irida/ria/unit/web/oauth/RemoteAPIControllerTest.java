package ca.corefacility.bioinformatics.irida.ria.unit.web.oauth;

import java.net.MalformedURLException;
import java.security.Principal;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.servlet.HandlerMapping;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.oauth.OltuAuthorizationController;
import ca.corefacility.bioinformatics.irida.ria.web.oauth.RemoteAPIController;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class RemoteAPIControllerTest {
	private RemoteAPIController remoteAPIController;
	private RemoteAPIService remoteAPIService;
	private ProjectRemoteService projectRemoteService;
	private UserService userService;
	private OltuAuthorizationController authController;
	private MessageSource messageSource;

	private static final String USER_NAME = "testme";

	private Locale locale;

	@BeforeEach
	public void setUp() {
		remoteAPIService = mock(RemoteAPIService.class);
		messageSource = mock(MessageSource.class);
		projectRemoteService = mock(ProjectRemoteService.class);
		authController = mock(OltuAuthorizationController.class);
		userService = mock(UserService.class);
		remoteAPIController = new RemoteAPIController(remoteAPIService, projectRemoteService, userService,
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

		assertEquals("redirect:/admin/remote_api/1", postCreateClient);
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

	@Test
	public void testConnectToAPI() {
		Long apiId = 1L;
		ExtendedModelMap model = new ExtendedModelMap();
		RemoteAPI client = new RemoteAPI("name", "http://uri", "a description", "id", "secret");
		when(remoteAPIService.read(apiId)).thenReturn(client);
		when(projectRemoteService.getServiceStatus(client)).thenThrow(new IridaOAuthException("invalid token", client));
		assertThrows(IridaOAuthException.class, () -> {
			remoteAPIController.connectToAPI(apiId, model);
		});
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
