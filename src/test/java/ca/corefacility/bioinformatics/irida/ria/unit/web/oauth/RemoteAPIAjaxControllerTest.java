package ca.corefacility.bioinformatics.irida.ria.unit.web.oauth;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.RemoteAPIAjaxController;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.rempoteapi.dto.RemoteAPITableModel;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIRemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Testing for {@link RemoteAPIAjaxController}
 */
public class RemoteAPIAjaxControllerTest {
	private RemoteAPIService remoteAPIService;
	private UIRemoteAPIService uiRemoteAPIService;
	private UserService userService;
	private RemoteAPIAjaxController controller;

	private final RemoteAPI REMOTE_API_01 = new RemoteAPI("Toronto", "http://toronto.nowhere", "", "toronto", "123456");
	private final RemoteAPI REMOTE_API_02 = new RemoteAPI("Washington", "http://washington.nowhere", "", "washington",
			"654321");
	private static final String USER_NAME = "testme";

	@BeforeEach
	public void init() {
		remoteAPIService = mock(RemoteAPIService.class);
		uiRemoteAPIService = mock(UIRemoteAPIService.class);
		userService = mock(UserService.class);
		MessageSource messageSource = mock(MessageSource.class);
		controller = new RemoteAPIAjaxController(remoteAPIService, uiRemoteAPIService, messageSource, userService);

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

		when(remoteAPIService.search(any(), anyInt(), anyInt(), any(Sort.Direction.class), any(String.class)))
				.thenReturn(remoteAPIPage);
		when(remoteAPIService.read(1L)).thenReturn(REMOTE_API_01);
		when(remoteAPIService.read(2L)).thenReturn(REMOTE_API_02);
	}

	@Test
	public void getAjaxAPIListTest() {
		TableRequest request = new TableRequest();
		request.setSortColumn("label");
		request.setSortDirection("ascend");
		request.setCurrent(1);
		request.setPageSize(10);

		Long userId = 1L;
		User puser = new User(userId, USER_NAME, null, null, null, null, null);
		puser.setSystemRole(Role.ROLE_USER);
		when(userService.getUserByUsername(puser.getUsername())).thenReturn(puser);

		Authentication auth = new UsernamePasswordAuthenticationToken(puser, null);
		SecurityContextHolder.getContext().setAuthentication(auth);

		TableResponse<RemoteAPITableModel> response = controller.getAjaxAPIList(request);
		verify(remoteAPIService, times(1)).search(any(), anyInt(), anyInt(), any(Sort.Direction.class),
				any(String.class));
		assertEquals(1, response.getDataSource().size(), "Should have 1 Remote API");
	}
}