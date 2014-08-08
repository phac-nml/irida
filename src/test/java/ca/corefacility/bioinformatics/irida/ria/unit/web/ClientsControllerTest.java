package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.DataTable;
import ca.corefacility.bioinformatics.irida.ria.web.ClientsController;
import ca.corefacility.bioinformatics.irida.service.IridaClientDetailsService;

import com.google.common.collect.Lists;

public class ClientsControllerTest {
	private IridaClientDetailsService clientDetailsService;
	private ClientsController controller;
	private MessageSource messageSource;

	@Before
	public void setUp() {
		clientDetailsService = mock(IridaClientDetailsService.class);
		messageSource = mock(MessageSource.class);
		controller = new ClientsController(clientDetailsService, messageSource);
	}

	@Test
	public void testGetClientsPage() {
		String clientsPage = controller.getClientsPage();
		assertEquals(ClientsController.CLIENTS_PAGE, clientsPage);
	}

	@Test
	public void testRead() {
		Long clientId = 1l;
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
		int page = 0;
		int size = 10;
		int draw = 1;
		int sortColumn = 0;
		String direction = "asc";
		String searchValue = "";
		IridaClientDetails client1 = new IridaClientDetails();
		client1.setId(1l);
		IridaClientDetails client2 = new IridaClientDetails();
		client2.setId(2l);
		Page<IridaClientDetails> clientPage = new PageImpl<>(Lists.newArrayList(client1, client2));

		when(
				clientDetailsService.search(any(Specification.class), eq(page), eq(size), any(Direction.class),
						any(String.class))).thenReturn(clientPage);

		Map<String, Object> ajaxClientList = controller.getAjaxClientList(page, size, draw, sortColumn, direction,
				searchValue);

		assertNotNull(ajaxClientList.get(DataTable.RESPONSE_PARAM_DATA));
		List<List<String>> clientList = (List<List<String>>) ajaxClientList.get(DataTable.RESPONSE_PARAM_DATA);

		assertEquals(2, clientList.size());

		verify(clientDetailsService).search(any(Specification.class), eq(page), eq(size), any(Direction.class),
				any(String.class));

	}
}
