package ca.corefacility.bioinformatics.irida.ria.unit.web.oauth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.ria.utilities.components.DataTable;
import ca.corefacility.bioinformatics.irida.ria.web.oauth.RemoteAPIController;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;

import com.google.common.collect.Lists;

public class RemoteAPIControllerTest {
	private RemoteAPIController remoteAPIController;
	private RemoteAPIService remoteAPIService;

	@Before
	public void setUp() {
		remoteAPIService = mock(RemoteAPIService.class);
		remoteAPIController = new RemoteAPIController(remoteAPIService);
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
}
