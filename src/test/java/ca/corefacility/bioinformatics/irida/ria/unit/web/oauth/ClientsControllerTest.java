package ca.corefacility.bioinformatics.irida.ria.unit.web.oauth;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.ui.ExtendedModelMap;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesParams;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesResponse;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;
import ca.corefacility.bioinformatics.irida.ria.web.oauth.ClientsController;
import ca.corefacility.bioinformatics.irida.service.IridaClientDetailsService;

public class ClientsControllerTest {
	private IridaClientDetailsService clientDetailsService;
	private ClientsController controller;
	private MessageSource messageSource;
	private Locale locale;

	@Before
	public void setUp() {
		clientDetailsService = mock(IridaClientDetailsService.class);
		messageSource = mock(MessageSource.class);
		controller = new ClientsController(clientDetailsService, messageSource);
		locale = LocaleContextHolder.getLocale();
	}

	@Test
	public void testGetClientsPage() {
		String clientsPage = controller.getClientsPage();
		assertEquals(ClientsController.CLIENTS_PAGE, clientsPage);
	}

	@Test
	public void testRead() {
		Long clientId = 1L;
		ExtendedModelMap model = new ExtendedModelMap();
		IridaClientDetails iridaClientDetails = new IridaClientDetails();
		iridaClientDetails.setId(clientId);

		when(clientDetailsService.read(clientId)).thenReturn(iridaClientDetails);

		String detailsPage = controller.read(clientId, model);

		assertEquals(ClientsController.CLIENT_DETAILS_PAGE, detailsPage);
		assertEquals(model.get("client"), iridaClientDetails);
		assertTrue(model.containsAttribute("grants"));

		verify(clientDetailsService).read(clientId);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetAjaxClientList() {
		IridaClientDetails client1 = new IridaClientDetails();
		client1.setId(1L);
		IridaClientDetails client2 = new IridaClientDetails();
		client2.setId(2L);
		Page<IridaClientDetails> clientPage = new PageImpl<>(Lists.newArrayList(client1, client2));

		when(clientDetailsService.search(any(Specification.class), any(Pageable.class))).thenReturn(clientPage);

		DataTablesParams params = new DataTablesParams(1, 10, 1, "", Sort.by(Sort.Direction.ASC, "createdDate"), new HashMap<>());
		DataTablesResponse response = controller.getAjaxClientsList(params);

		List<DataTablesResponseModel> models = response.getData();
		assertEquals(2, models.size());

		verify(clientDetailsService).search(any(Specification.class), any(Pageable.class));
	}

	@Test
	public void testGetAddClientPage() {
		ExtendedModelMap model = new ExtendedModelMap();

		String addClientPage = controller.getAddClientPage(model);

		assertEquals(ClientsController.ADD_CLIENT_PAGE, addClientPage);
		assertTrue(model.containsAttribute("errors"));
		assertTrue(model.containsAttribute("given_tokenValidity"));
	}

	@Test
	public void testPostCreateClient() {
		IridaClientDetails client = new IridaClientDetails();
		client.setId(1L);
		ExtendedModelMap model = new ExtendedModelMap();
		String scope_read = "read";
		String scope_write = "";

		when(clientDetailsService.create(client)).thenReturn(client);

		String postCreateClient = controller.postCreateClient(client, scope_read, scope_write, "", "", "", model, locale);

		assertEquals("redirect:/clients/1", postCreateClient);
		verify(clientDetailsService).create(client);
	}

	@Test
	public void testPostCreateClientError() {
		IridaClientDetails client = new IridaClientDetails();
		client.setId(1L);
		ExtendedModelMap model = new ExtendedModelMap();
		Locale locale = LocaleContextHolder.getLocale();
		String scope_read = "read";
		String scope_write = "";

		DataIntegrityViolationException ex = new DataIntegrityViolationException(
				"Error: " + IridaClientDetails.CLIENT_ID_CONSTRAINT_NAME);

		when(clientDetailsService.create(client)).thenThrow(ex);

		String postCreateClient = controller.postCreateClient(client, scope_read, scope_write, "", "", "", model, locale);

		assertEquals(ClientsController.ADD_CLIENT_PAGE, postCreateClient);
		assertTrue(model.containsAttribute("errors"));
		@SuppressWarnings("unchecked")
		Map<String, String> errors = (Map<String, String>) model.get("errors");
		assertTrue(errors.containsKey("clientId"));

		verify(clientDetailsService).create(client);
	}

	@Test
	public void testGetEditPage() {
		IridaClientDetails client = new IridaClientDetails();
		client.setAuthorizedGrantTypes(ImmutableSet.of("password"));
		client.setScope(ImmutableSet.of("read"));
		client.setAutoApprovableScopes(ImmutableSet.of(""));
		Long id = 1L;
		client.setId(id);
		ExtendedModelMap model = new ExtendedModelMap();

		when(clientDetailsService.read(id)).thenReturn(client);

		String editPage = controller.getEditPage(id, model);

		assertEquals(ClientsController.EDIT_CLIENT_PAGE, editPage);
		assertTrue(model.containsAttribute("client"));
		assertTrue(model.containsAttribute("given_scope_read"));
	}

	@Test
	public void testSubmitEditClient() {
		IridaClientDetails client = new IridaClientDetails();
		Long id = 1L;
		client.setId(id);
		ExtendedModelMap model = new ExtendedModelMap();
		String scope_read = "read";

		when(clientDetailsService.read(id)).thenReturn(client);

		String postCreateClient = controller.postEditClient(id, 0, "", scope_read, "", "", "", "", "", 0, "", model, locale);

		assertEquals("redirect:/clients/1", postCreateClient);
		ArgumentCaptor<IridaClientDetails> captor = ArgumentCaptor.forClass(IridaClientDetails.class);
		verify(clientDetailsService).update(captor.capture());

		IridaClientDetails updated = captor.getValue();

		Set<String> scope = updated.getScope();
		assertTrue(scope.contains(scope_read));
	}

	@Test
	public void testSubmitEditClientError() {
		IridaClientDetails client = new IridaClientDetails();
		Long id = 1L;
		client.setId(id);
		client.setAutoApprovableScopes(ImmutableSet.of(""));
		ExtendedModelMap model = new ExtendedModelMap();

		when(clientDetailsService.read(id)).thenReturn(client);
		DataIntegrityViolationException ex = new DataIntegrityViolationException(
				"Error: " + IridaClientDetails.CLIENT_ID_CONSTRAINT_NAME);

		when(clientDetailsService.update(any(IridaClientDetails.class))).thenThrow(ex);

		String postCreateClient = controller.postEditClient(id, 0, "", "", "", "", "", "", "", 0, "", model, locale);
		assertEquals(ClientsController.EDIT_CLIENT_PAGE, postCreateClient);
	}

	@Test
	public void testSubmitEditWithClientSecretUpdate() {
		IridaClientDetails client = new IridaClientDetails();
		String originalSecret = "original";
		Long id = 1L;
		client.setId(id);
		client.setClientSecret(originalSecret);
		ExtendedModelMap model = new ExtendedModelMap();

		when(clientDetailsService.read(id)).thenReturn(client);

		String postCreateClient = controller.postEditClient(id, 0, "", "", "", "", "", "", "", 0, "true", model, locale);

		assertEquals("redirect:/clients/1", postCreateClient);
		ArgumentCaptor<IridaClientDetails> captor = ArgumentCaptor.forClass(IridaClientDetails.class);
		verify(clientDetailsService).update(captor.capture());

		IridaClientDetails value = captor.getValue();
		assertNotEquals("Secret should be different", originalSecret, value.getClientSecret());
	}

	@Test
	public void testRemoveClient() {
		Long id = 1L;

		String removeClient = controller.removeClient(id);

		assertEquals("redirect:/clients", removeClient);

		verify(clientDetailsService).delete(id);
	}
}
