package ca.corefacility.bioinformatics.irida.ria.unit.web.service;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectGroupTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectGroupsService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserGroupService;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;

import com.google.common.collect.ImmutableList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class UIProjectGroupServiceTest {
	/*
	Default values
	 */
	private final Long PROJECT_ID = 1L;
	private final Project PROJECT = TestDataFactory.constructProject();
	private final TableRequest TABLE_REQUEST = new TableRequest(0, 10, "createdDate", "asc", "");
	private final User USER_1 = new User(1L, "user1", "user1@nowhere.com", "SDF123", "USER", "ONE", "7777");
	private final User USER_2 = new User(2L, "user2", "user2@nowhere.com", "SDF456", "USER", "TWO", "7777");
	private final UserGroup USER_GROUP_1 = new UserGroup("GROUP_1");
	private final List<UserGroupProjectJoin> userGroupJoin = ImmutableList.of(
			new UserGroupProjectJoin(PROJECT, USER_GROUP_1, ProjectRole.PROJECT_USER));

	private UIProjectGroupsService groupsService;
	private ProjectService projectService;
	private UserGroupService userGroupService;

	@Before
	public void setUp() {
		projectService = mock(ProjectService.class);
		userGroupService = mock(UserGroupService.class);
		MessageSource messageSource = mock(MessageSource.class);
		groupsService = new UIProjectGroupsService(projectService, userGroupService, messageSource);

		when(projectService.read(PROJECT_ID)).thenReturn(PROJECT);
		when(userGroupService.getUserGroupsForProject(TABLE_REQUEST.getSearch(), PROJECT, TABLE_REQUEST.getCurrent(),
				TABLE_REQUEST.getPageSize(), TABLE_REQUEST.getSort())).thenReturn(getPagesUserGroupsForProject());
		when(userGroupService.getUserGroupsNotOnProject(PROJECT, "GR")).thenReturn(ImmutableList.of(USER_GROUP_1));
	}

	@Test
	public void testGetProjectGroups() {
		TableResponse<ProjectGroupTableModel> response = groupsService.getProjectGroups(PROJECT_ID, TABLE_REQUEST);
		verify(userGroupService, times(1)).getUserGroupsForProject(TABLE_REQUEST.getSearch(), PROJECT,
				TABLE_REQUEST.getCurrent(), TABLE_REQUEST.getPageSize(), TABLE_REQUEST.getSort());
		assertEquals(Long.valueOf(2L), response.getTotal());
		assertNotNull(response.getDataSource());
	}

	@Test
	public void testSearchAvailableGroups() {
		List<ProjectGroupTableModel> response = groupsService.searchAvailableGroups(PROJECT_ID, "GR");
		assertEquals("Should return 1 project", 1, response.size());
	}

	// TODO: FINISH TESTS HERE!

	private Page<UserGroupProjectJoin> getPagesUserGroupsForProject() {
		return new Page<UserGroupProjectJoin>() {
			@Override
			public int getTotalPages() {
				return 0;
			}

			@Override
			public long getTotalElements() {
				return 2;
			}

			@Override
			public <U> Page<U> map(Function<? super UserGroupProjectJoin, ? extends U> converter) {
				return null;
			}

			@Override
			public int getNumber() {
				return 2;
			}

			@Override
			public int getSize() {
				return 0;
			}

			@Override
			public int getNumberOfElements() {
				return 2;
			}

			@Override
			public List<UserGroupProjectJoin> getContent() {
				return userGroupJoin;
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
				return userGroupJoin.iterator();
			}
		};
	}
}
