package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.IridaClientDetails;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.clients.ClientTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.clients.ClientTableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIClientService;
import ca.corefacility.bioinformatics.irida.service.IridaClientDetailsService;

import com.google.common.collect.Lists;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UIClientServiceTest {
	private IridaClientDetailsService clientDetailsService;
	private UIClientService service;
	private MessageSource messageSource;

	@BeforeEach
	public void setUp() {
		clientDetailsService = mock(IridaClientDetailsService.class);
		messageSource = mock(MessageSource.class);
		service = new UIClientService(clientDetailsService, messageSource);
	}

	@Test
	public void testGetClientList() {
		IridaClientDetails client1 = new IridaClientDetails();
		client1.setId(1L);
		IridaClientDetails client2 = new IridaClientDetails();
		client2.setId(2L);
		Page<IridaClientDetails> clientPage = new PageImpl<>(Lists.newArrayList(client1, client2));

		when(clientDetailsService.search(ArgumentMatchers.<Specification<IridaClientDetails>>any(), any(Pageable.class))).thenReturn(clientPage);

		ClientTableRequest params = new ClientTableRequest();
		params.setCurrent(1);
		params.setPageSize(10);
		params.setSortColumn("createdDate");
		params.setSortDirection("ascend");
		TableResponse<ClientTableModel> response = service.getClientList(params);

		assertEquals(2, response.getDataSource().size());

		verify(clientDetailsService).search(ArgumentMatchers.<Specification<IridaClientDetails>>any(), any(Pageable.class));
	}
}
