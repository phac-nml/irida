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

import java.util.List;
import java.util.Locale;
import java.util.Map;

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

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.DataTable;
import ca.corefacility.bioinformatics.irida.ria.web.oauth.RemoteAPIController;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;

import com.google.common.collect.Lists;

public class RemoteAPIControllerTest {
	private RemoteAPIController remoteAPIController;
	private RemoteAPIService remoteAPIService;
	private MessageSource messageSource;

	private Locale locale;

	@Before
	public void setUp() {
		remoteAPIService = mock(RemoteAPIService.class);
		messageSource = mock(MessageSource.class);
		remoteAPIController = new RemoteAPIController(remoteAPIService, messageSource);
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

		RemoteAPI api1 = new RemoteAPI("api name", "http://somewhere", "an api", "client1", "secret1");
		api1.setId(1l);
		RemoteAPI api2 = new RemoteAPI("api name 2", "http://nowhere", "another api", "client2", "secret2");
		api2.setId(2l);

		Page<RemoteAPI> apiPage = new PageImpl<>(Lists.newArrayList(api1, api2));

		when(
				remoteAPIService.search(any(Specification.class), eq(page), eq(size), any(Direction.class),
						any(String.class))).thenReturn(apiPage);

		Map<String, Object> ajaxAPIList = remoteAPIController.getAjaxAPIList(page, size, draw, sortColumn, direction,
				searchValue);

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
}
