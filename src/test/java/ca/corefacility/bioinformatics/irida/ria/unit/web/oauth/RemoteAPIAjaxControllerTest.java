package ca.corefacility.bioinformatics.irida.ria.unit.web.oauth;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.oauth.RemoteAPIAjaxController;
import ca.corefacility.bioinformatics.irida.ria.web.rempoteapi.dto.RemoteAPITableModel;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Testing for {@link RemoteAPIAjaxController}
 */
public class RemoteAPIAjaxControllerTest {
	private RemoteAPIService remoteAPIService;
	private ProjectRemoteService projectRemoteService;
	private RemoteAPIAjaxController controller;

	private final RemoteAPI REMOTE_API_01 = new RemoteAPI("Toronto", "http://toronto.nowhere", "", "toronto", "123456");
	private final RemoteAPI REMOTE_API_02 = new RemoteAPI("Washington", "http://washington.nowhere", "", "washington",
			"654321");

	@Before
	public void init() {
		remoteAPIService = mock(RemoteAPIService.class);
		projectRemoteService = mock(ProjectRemoteService.class);
		controller = new RemoteAPIAjaxController(remoteAPIService, projectRemoteService);

		Page<RemoteAPI> remoteAPIPage = new Page<>() {
			@Override
			public int getTotalPages() {
				return 10;
			}

			@Override
			public long getTotalElements() {
				return 104;
			}

			@Override
			public <U> Page<U> map(Function<? super RemoteAPI, ? extends U> function) {
				return null;
			}

			@Override
			public int getNumber() {
				return 0;
			}

			@Override
			public int getSize() {
				return 0;
			}

			@Override
			public int getNumberOfElements() {
				return 104;
			}

			@Override
			public List<RemoteAPI> getContent() {
				return List.of(REMOTE_API_01);
			}

			@Override
			public boolean hasContent() {
				return true;
			}

			@Override
			public Sort getSort() {
				return null;
			}

			@Override
			public boolean isFirst() {
				return false;
			}

			@Override
			public boolean isLast() {
				return false;
			}

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public boolean hasPrevious() {
				return false;
			}

			@Override
			public Pageable nextPageable() {
				return null;
			}

			@Override
			public Pageable previousPageable() {
				return null;
			}

			@Override
			public Iterator<RemoteAPI> iterator() {
				return null;
			}
		};

		when(remoteAPIService.search(any(), anyInt(), anyInt(), any(Sort.Direction.class),
				any(String[].class))).thenReturn(remoteAPIPage);
		when(remoteAPIService.read(1L)).thenReturn(REMOTE_API_01);
		when(remoteAPIService.read(2L)).thenReturn(REMOTE_API_02);
		when(projectRemoteService.getServiceStatus(REMOTE_API_01)).thenReturn(true);
		when(projectRemoteService.getServiceStatus(REMOTE_API_02)).thenThrow(
				new IridaOAuthException("", REMOTE_API_02));
	}

	@Test
	public void getAjaxAPIListTest() {
		TableRequest request = new TableRequest();
		request.setSortColumn("label");
		request.setSortDirection("ascend");
		request.setCurrent(0);
		request.setPageSize(10);

		TableResponse<RemoteAPITableModel> response = controller.getAjaxAPIList(request);
		verify(remoteAPIService, times(1)).search(any(), anyInt(), anyInt(), any(Sort.Direction.class),
				any(String[].class));
		assertEquals("Should have 1 Remote API", 1, response.getDataSource()
				.size());
	}

	@Test
	public void checkAPIStatusTest() {
		ResponseEntity<String> responseEntity1 = controller.checkAPIStatus(1L);
		assertEquals("Should have a valid token", responseEntity1.getBody(),
				RemoteAPIAjaxController.VALID_OAUTH_CONNECTION);
		assertEquals("Should have a 'OK' HttpStatus", HttpStatus.OK, responseEntity1.getStatusCode());

		ResponseEntity<String> responseEntity2 = controller.checkAPIStatus(2L);
		assertEquals("Should have a invalid token", responseEntity2.getBody(),
				RemoteAPIAjaxController.INVALID_OAUTH_TOKEN);
		assertEquals("Should have a 'OK' HttpStatus", HttpStatus.OK, responseEntity2.getStatusCode());

	}
}
