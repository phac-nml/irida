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

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NewMemberRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ProjectUserGroupsTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectUserGroupsService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;

import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UIProjectUserGroupServiceTest {
	/*
	DEFAULT VALUES
	 */
	private final Project PROJECT = TestDataFactory.constructProject();
	private final TableRequest TABLE_REQUEST = new TableRequest(0, 10, "createdDate", "asc", "");
	private final UserGroup USER_GROUP_1 = new UserGroup("G1");
	private final UserGroup USER_GROUP_2 = new UserGroup("G2");
	private final UserGroup USER_GROUP_3 = new UserGroup("G3");
	private final List<UserGroupProjectJoin> PROJECT_USER_GROUP_JOINS = ImmutableList.of(
			new UserGroupProjectJoin(PROJECT, USER_GROUP_1, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1),
			new UserGroupProjectJoin(PROJECT, USER_GROUP_2, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1),
			new UserGroupProjectJoin(PROJECT, USER_GROUP_3, ProjectRole.PROJECT_OWNER, ProjectMetadataRole.LEVEL_4));
	private final List<UserGroup> USER_GROUPS = ImmutableList.of(USER_GROUP_1, USER_GROUP_2, USER_GROUP_3);
	private final Locale LOCALE = Locale.CANADA;
	private UIProjectUserGroupsService service;
	private ProjectService projectService;
	private UserGroupService userGroupService;

	@BeforeEach
	public void setUp() throws Exception {
		projectService = mock(ProjectService.class);
		userGroupService = mock(UserGroupService.class);
		MessageSource messageSource = mock(MessageSource.class);

		service = new UIProjectUserGroupsService(projectService, userGroupService, messageSource);

		// Set up mocks
		when(messageSource.getMessage(anyString(), any(), any())).thenReturn("");
		when(projectService.read(1L)).thenReturn(PROJECT);
		when(userGroupService.read(1L)).thenReturn(USER_GROUP_1);
		when(userGroupService.read(3L)).thenReturn(USER_GROUP_3);
		when(userGroupService.getUserGroupsForProject("", PROJECT, TABLE_REQUEST.getCurrent(),
				TABLE_REQUEST.getPageSize(), TABLE_REQUEST.getSort())).thenReturn(getPagedUserGroupsForProject());
		when(userGroupService.read(USER_GROUP_1.getId())).thenReturn(USER_GROUP_1);
		when(userGroupService.getUserGroupsNotOnProject(PROJECT, "")).thenReturn(USER_GROUPS);

		doThrow(ProjectWithoutOwnerException.class).when(projectService)
				.updateUserGroupProjectRole(PROJECT, USER_GROUP_3, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1);
	}

	@Test
	public void testGetUserGroupsForProject() {
		TableResponse<ProjectUserGroupsTableModel> response = service.getUserGroupsForProject(PROJECT.getId(),
				TABLE_REQUEST);
		verify(userGroupService, times(1)).getUserGroupsForProject("", PROJECT, TABLE_REQUEST.getCurrent(),
				TABLE_REQUEST.getPageSize(), TABLE_REQUEST.getSort());
		assertEquals(Long.valueOf(3L), response.getTotal());
	}

	@Test
	public void testRemoveUserGroupFromProject() throws ProjectWithoutOwnerException {
		service.removeUserGroupFromProject(1L, 1L, LOCALE);
		verify(projectService, times(1)).read(1L);
		verify(userGroupService, times(1)).read(1L);
		verify(projectService, times(1)).removeUserGroupFromProject(PROJECT, USER_GROUP_1);
	}

	@Test
	public void testGetAvailableUserGroupsForProject() {
		List<UserGroup> groups = service.getAvailableUserGroupsForProject(1L, "");
		assertEquals(3, groups.size(), "Should be 3 user groups returned");
		verify(userGroupService, times(1)).getUserGroupsNotOnProject(PROJECT, "");
	}

	@Test
	public void testAddUserGroupToProject() {
		NewMemberRequest newMemberRequest = new NewMemberRequest(1L, ProjectRole.PROJECT_OWNER.toString(),
				ProjectMetadataRole.LEVEL_4.toString());
		service.addUserGroupToProject(1L, newMemberRequest, LOCALE);
		verify(projectService, times(1)).read(1L);
		verify(userGroupService, times(1)).read(1L);
		verify(projectService, times(1)).addUserGroupToProject(PROJECT, USER_GROUP_1, ProjectRole.PROJECT_OWNER,
				ProjectMetadataRole.LEVEL_4);
	}

	@Test
	public void testUpdateUserGroupProjectRole() throws Exception {
		service.updateUserGroupRoleOnProject(1L, 1L, ProjectRole.PROJECT_USER.toString(),  LOCALE);
		verify(projectService, times(1)).read(1L);
		verify(userGroupService, times(1)).read(1L);
		verify(projectService, times(1)).updateUserGroupProjectRole(PROJECT, USER_GROUP_1, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1);
	}

	@Test
	public void testUpdateUserProjectRoleOnProjectNoManager() {
		assertThrows(ProjectWithoutOwnerException.class, () -> {
			service.updateUserGroupRoleOnProject(1L, 3L, ProjectRole.PROJECT_USER.toString(), LOCALE);
		});
	}

	@Test
	public void testUpdateUserGroupMetadataRole() throws Exception {
		service.updateUserGroupMetadataRoleOnProject(1L, 1L,  ProjectMetadataRole.LEVEL_1.toString(), LOCALE);
		verify(projectService, times(1)).read(1L);
		verify(userGroupService, times(1)).read(1L);
		verify(projectService, times(1)).updateUserGroupProjectMetadataRole(PROJECT, USER_GROUP_1, ProjectMetadataRole.LEVEL_1);
	}

	private Page<UserGroupProjectJoin> getPagedUserGroupsForProject() {
		return new Page<>() {
			@Override
			public int getTotalPages() {
				return 0;
			}

			@Override
			public long getTotalElements() {
				return PROJECT_USER_GROUP_JOINS.size();
			}

			@Override
			public <U> Page<U> map(Function<? super UserGroupProjectJoin, ? extends U> converter) {
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
			public List<UserGroupProjectJoin> getContent() {
				return PROJECT_USER_GROUP_JOINS;
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
			public Iterator<UserGroupProjectJoin> iterator() {
				return PROJECT_USER_GROUP_JOINS.iterator();
			}
		};
	}
}
