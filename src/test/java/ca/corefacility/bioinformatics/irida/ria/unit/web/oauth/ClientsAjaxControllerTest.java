package ca.corefacility.bioinformatics.irida.ria.unit.web.oauth;

import ca.corefacility.bioinformatics.irida.ria.web.clients.ClientsAjaxController;
import ca.corefacility.bioinformatics.irida.service.IridaClientDetailsService;

public class ClientsAjaxControllerTest {
	private IridaClientDetailsService clientDetailsService;
	private ClientsAjaxController controller;

//	@Before
//	public void setUp() {
//		clientDetailsService = mock(IridaClientDetailsService.class);
//		controller = new ClientsAjaxController();
//	}
//
//	@SuppressWarnings("unchecked")
//	@Test
//	public void testGetAjaxClientList() {
//		IridaClientDetails client1 = new IridaClientDetails();
//		client1.setId(1L);
//		IridaClientDetails client2 = new IridaClientDetails();
//		client2.setId(2L);
//		Page<IridaClientDetails> clientPage = new PageImpl<>(Lists.newArrayList(client1, client2));
//
//		when(clientDetailsService.search(any(Specification.class), any(Pageable.class))).thenReturn(clientPage);
//
//		ClientTableRequest params = new ClientTableRequest();
//		params.setCurrent(1);
//		params.setPageSize(10);
//		params.setSortColumn("createdDate");
//		params.setSortDirection("ascend");
//		TableResponse response = controller.getAjaxClientsList(params);
//
//		assertEquals(2, response.getDataSource().size());
//
//		verify(clientDetailsService).search(any(Specification.class), any(Pageable.class));
//	}
}
