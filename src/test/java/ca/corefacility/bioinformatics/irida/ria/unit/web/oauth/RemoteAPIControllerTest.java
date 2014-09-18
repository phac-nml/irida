package ca.corefacility.bioinformatics.irida.ria.unit.web.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.DataTable;
import ca.corefacility.bioinformatics.irida.ria.web.oauth.OltuAuthorizationController;
import ca.corefacility.bioinformatics.irida.ria.web.oauth.RemoteAPIController;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.remote.model.RemoteProject;

import com.google.common.collect.Lists;

public class RemoteAPIControllerTest {
	private RemoteAPIController remoteAPIController;
	private RemoteAPIService remoteAPIService;
	private ProjectRemoteService projectRemoteService;
	private RemoteAPITokenService tokenService;
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
		remoteAPIController = new RemoteAPIController(remoteAPIService, projectRemoteService, tokenService,
				authController, messageSource);
		locale = LocaleContextHolder.getLocale();

	}

	@Test
	public void testList() {
		String list = remoteAPIController.list();
		assertEquals(RemoteAPIController.CLIENTS_PAGE, list);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetAjaxList() {
		int page = 0;
		int size = 10;
		int draw = 1;
		int sortColumn = 0;
		String direction = "asc";
		String searchValue = "";
		Principal principal = () -> USER_NAME;

		RemoteAPI api1 = new RemoteAPI("api name", "http://somewhere", "an api", "client1", "secret1");
		api1.setId(1l);
		RemoteAPI api2 = new RemoteAPI("api name 2", "http://nowhere", "another api", "client2", "secret2");
		api2.setId(2l);

		Page<RemoteAPI> apiPage = new PageImpl<>(Lists.newArrayList(api1, api2));

		when(
				remoteAPIService.search(any(Specification.class), eq(page), eq(size), any(Direction.class),
						any(String.class))).thenReturn(apiPage);

		Map<String, Object> ajaxAPIList = remoteAPIController.getAjaxAPIList(page, size, draw, sortColumn, direction,
				searchValue, principal, locale);

		verify(remoteAPIService).search(any(Specification.class), eq(page), eq(size), any(Direction.class),
				any(String.class));

		assertNotNull(ajaxAPIList);
		assertFalse(ajaxAPIList.isEmpty());

		List<List<String>> apiList = (List<List<String>>) ajaxAPIList.get(DataTable.RESPONSE_PARAM_DATA);
		assertEquals(2, apiList.size());

	}

	@Test
	public void testRemoveRemoteAPI() {
		Long id = 1l;

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
		client.setId(1l);
		ExtendedModelMap model = new ExtendedModelMap();

		when(remoteAPIService.create(client)).thenReturn(client);

		String postCreateClient = remoteAPIController.postCreateRemoteAPI(client, model, locale);

		assertEquals("redirect:/remote_api/1", postCreateClient);
		verify(remoteAPIService).create(client);
	}

	@Test
	public void testPostCreateRemoteAPIError() {
		RemoteAPI client = new RemoteAPI("name", "http://uri", "a description", "id", "secret");
		client.setId(1l);
		ExtendedModelMap model = new ExtendedModelMap();
		Locale locale = LocaleContextHolder.getLocale();

		DataIntegrityViolationException ex = new DataIntegrityViolationException("Error: "
				+ RemoteAPI.SERVICE_URI_CONSTRAINT_NAME);

		when(remoteAPIService.create(client)).thenThrow(ex);

		String postCreateClient = remoteAPIController.postCreateRemoteAPI(client, model, locale);

		assertEquals(RemoteAPIController.ADD_API_PAGE, postCreateClient);
		assertTrue(model.containsAttribute("errors"));
		@SuppressWarnings("unchecked")
		Map<String, String> errors = (Map<String, String>) model.get("errors");
		assertTrue(errors.containsKey("serviceURI"));

		verify(remoteAPIService).create(client);
	}

	@Test
	public void testCheckApiStatusActive() {
		Long apiId = 1l;
		RemoteAPI client = new RemoteAPI("name", "http://uri", "a description", "id", "secret");
		ArrayList<RemoteProject> remoteProjects = Lists.newArrayList(new RemoteProject());
		when(remoteAPIService.read(apiId)).thenReturn(client);
		when(projectRemoteService.list(client)).thenReturn(remoteProjects);
		String checkApiStatus = remoteAPIController.checkApiStatus(apiId);

		assertEquals(RemoteAPIController.VALID_OAUTH_CONNECTION, checkApiStatus);

		verify(remoteAPIService).read(apiId);
		verify(projectRemoteService).list(client);
	}

	@Test
	public void testCheckApiStatusInactive() {
		Long apiId = 1l;
		RemoteAPI client = new RemoteAPI("name", "http://uri", "a description", "id", "secret");

		when(remoteAPIService.read(apiId)).thenReturn(client);
		when(projectRemoteService.list(client)).thenThrow(new IridaOAuthException("invalid token", client));

		String checkApiStatus = remoteAPIController.checkApiStatus(apiId);

		assertEquals(RemoteAPIController.INVALID_OAUTH_TOKEN, checkApiStatus);

		verify(remoteAPIService).read(apiId);
		verify(projectRemoteService).list(client);
	}

	@Test(expected = IridaOAuthException.class)
	public void testConnectToAPI() {
		Long apiId = 1l;
		RemoteAPI client = new RemoteAPI("name", "http://uri", "a description", "id", "secret");
		when(remoteAPIService.read(apiId)).thenReturn(client);
		when(projectRemoteService.list(client)).thenThrow(new IridaOAuthException("invalid token", client));
		remoteAPIController.connectToAPI(apiId);
	}

	@Test
	public void testConnectToAPIActiveToken() {
		Long apiId = 1l;
		RemoteAPI client = new RemoteAPI("name", "http://uri", "a description", "id", "secret");
		when(remoteAPIService.read(apiId)).thenReturn(client);
		when(projectRemoteService.list(client)).thenReturn(new ArrayList<>());
		String connectToAPI = remoteAPIController.connectToAPI(apiId);
		assertEquals(RemoteAPIController.PARENT_FRAME_RELOAD_PAGE, connectToAPI);
	}

	@Test
	public void testRead() {
		Long apiId = 1l;
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
		Long apiId = 1l;
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
		when(request.getRequestURI()).thenReturn(redirect);

		RemoteAPI client = new RemoteAPI("name", "http://uri", "a description", "id", "secret");
		IridaOAuthException ex = new IridaOAuthException("msg", client);

		remoteAPIController.handleOAuthException(request, ex);
		verify(authController).authenticate(client, redirect);
	}
}
