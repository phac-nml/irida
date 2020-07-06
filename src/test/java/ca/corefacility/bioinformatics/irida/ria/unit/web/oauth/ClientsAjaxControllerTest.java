package ca.corefacility.bioinformatics.irida.ria.unit.web.oauth;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.ria.web.clients.ClientsAjaxController;
import ca.corefacility.bioinformatics.irida.ria.web.clients.dto.ClientTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.service.IridaClientDetailsService;

import com.google.common.collect.Lists;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

public class ClientsAjaxControllerTest {
	private IridaClientDetailsService clientDetailsService;
	private ClientsAjaxController controller;

	@Before
	public void setUp() {
		clientDetailsService = mock(IridaClientDetailsService.class);
		controller = new ClientsAjaxController(clientDetailsService);
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

		ClientTableRequest params = new ClientTableRequest();
		params.setCurrent(1);
		params.setPageSize(10);
		params.setSortColumn("createdDate");
		params.setSortDirection("ascend");
		TableResponse response = controller.getAjaxClientsList(params);

		assertEquals(2, response.getDataSource().size());

		verify(clientDetailsService).search(any(Specification.class), any(Pageable.class));
	}
}
