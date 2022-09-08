package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.FieldUpdate;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.UserGroupTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIUserGroupsService;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UIUserGroupsServiceTest {
	/*
	Mock Data
	 */
	private final User USER_1 = new User(1L, "user1", "user1@nowhere.com", "SDF123", "USER", "ONE", "7777");
	private final UserGroup GROUP_1 = new UserGroup("group 1");
	private final UserGroup GROUP_2 = new UserGroup("group 2");
	private final UserGroup GROUP_3 = new UserGroup("group 3");
	private final TableRequest TABLE_REQUEST = new TableRequest(1, 10, "createdDate", "asc", "");
	private final List<UserGroup> GROUPS = ImmutableList.of(GROUP_1, GROUP_2, GROUP_3);
	private UIUserGroupsService service;
	private UserGroupService userGroupService;
	private UserService userService;

	@BeforeEach
	public void setUp() {
		userGroupService = mock(UserGroupService.class);
		userService = mock(UserService.class);
		MessageSource messageSource = mock(MessageSource.class);
		service = new UIUserGroupsService(userGroupService, userService, messageSource);

		/*
		Mock the principal user
		 */
		USER_1.setSystemRole(Role.ROLE_ADMIN);
		Authentication authentication = mock(Authentication.class);
		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(USER_1.getUsername());
		when(userService.getUserByUsername(USER_1.getUsername())).thenReturn(USER_1);

		when(userGroupService.search(any(), any())).thenReturn(getPagedUserGroups());
		when(userGroupService.read(GROUP_1.getId())).thenReturn(GROUP_1);
		when(messageSource.getMessage(anyString(), any(), any())).thenReturn("DONE!");
	}

	@Test
	public void testGetUserGroups() {
		TableResponse<UserGroupTableModel> response = service.getUserGroups(TABLE_REQUEST);
		verify(userGroupService, times(1)).search(any(), any());
		assertEquals(Long.valueOf(3), response.getTotal(), "Should be 3 user groups");
	}

	@Test
	public void testDeleteUserGroup() {
		service.deleteUserGroup(GROUP_1.getId(), Locale.CANADA);
		verify(userGroupService, times(1)).read(GROUP_1.getId());
		verify(userGroupService, times(1)).delete(GROUP_1.getId());
	}

	@Test
	public void testGetUserGroupDetails() {
		service.getUserGroupDetails(GROUP_1.getId());
		verify(userGroupService, times(1)).read(GROUP_1.getId());
		verify(userGroupService, times(1)).getUsersForGroup(GROUP_1);
	}

	@Test
	public void testUpdateUserGroupDetails() {
		FieldUpdate update = new FieldUpdate();
		update.setField("name");
		update.setValue("NEW_NAME");
		service.updateUserGroupDetails(GROUP_1.getId(), update);
		verify(userGroupService, times(1)).update(GROUP_1);
	}

	private Page<UserGroup> getPagedUserGroups() {
		return new Page<>() {
			@Override
			public int getTotalPages() {
				return 0;
			}

			@Override
			public long getTotalElements() {
				return 3;
			}

			@Override
			public <U> Page<U> map(Function<? super UserGroup, ? extends U> converter) {
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
				return 0;
			}

			@Override
			public List<UserGroup> getContent() {
				return GROUPS;
			}

			@Override
			public boolean hasContent() {
				return false;
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
			public Iterator<UserGroup> iterator() {
				return GROUPS.iterator();
			}
		};
	}
}
