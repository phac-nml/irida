package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.exceptions.ProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIProjectWithoutOwnerException;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.ProjectMemberTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NewMemberRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectMembersService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.web.controller.test.unit.TestDataFactory;

import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UIProjectMembersServiceTest {
	/*
	Default values
	 */
	private final Long PROJECT_ID = 1L;
	private final Project PROJECT = TestDataFactory.constructProject();
	private final User USER_1 = new User(1L, "user1", "user1@nowhere.com", "SDF123", "USER", "ONE", "7777");
	private final User USER_2 = new User(2L, "user2", "user2@nowhere.com", "SDF456", "USER", "TWO", "7777");
	private final User USER_3 = new User(3L, "user3", "user3@nowhere.com", "SDF789", "USER", "TWO", "7777");
	private final TableRequest TABLE_REQUEST = new TableRequest(0, 10, "createdDate", "asc", "");
	private final List<Join<Project, User>> projectUserJoins = ImmutableList.of(
			new ProjectUserJoin(PROJECT, USER_1, ProjectRole.PROJECT_OWNER),
			new ProjectUserJoin(PROJECT, USER_2, ProjectRole.PROJECT_USER));
	private final Locale LOCALE = Locale.CANADA;

	private UIProjectMembersService service;
	private ProjectService projectService;
	private UserService userService;

	@BeforeEach
	public void setUp() throws ProjectWithoutOwnerException {
		projectService = mock(ProjectService.class);
		userService = mock(UserService.class);
		MessageSource messageSource = mock(MessageSource.class);

		service = new UIProjectMembersService(projectService, userService, messageSource);

		when(projectService.read(PROJECT_ID)).thenReturn(PROJECT);
		when(userService.read(USER_1.getId())).thenReturn(USER_1);
		when(userService.read(USER_2.getId())).thenReturn(USER_2);
		when(userService.read(USER_3.getId())).thenReturn(USER_3);

		when(userService.searchUsersForProject(PROJECT, TABLE_REQUEST.getSearch(), TABLE_REQUEST.getCurrent(),
				TABLE_REQUEST.getPageSize(), TABLE_REQUEST.getSort())).thenReturn(getPagedUsersForProject());

		when(messageSource.getMessage("projectRole." + ProjectRole.PROJECT_OWNER.toString(), new Object[] {},
				LOCALE)).thenReturn("Manager");
		when(messageSource.getMessage("projectRole." + ProjectRole.PROJECT_USER.toString(), new Object[] {},
				LOCALE)).thenReturn("Collaborator");

		doThrow(ProjectWithoutOwnerException.class).when(projectService)
				.removeUserFromProject(PROJECT, USER_3);
		doThrow(ProjectWithoutOwnerException.class).when(projectService)
				.updateUserProjectRole(PROJECT, USER_3, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1);
	}

	@Test
	public void testGetProjectMembers() {
		TableResponse<ProjectMemberTableModel> response = service.getProjectMembers(PROJECT_ID, TABLE_REQUEST);
		verify(userService, times(1)).searchUsersForProject(PROJECT, TABLE_REQUEST.getSearch(),
				TABLE_REQUEST.getCurrent(), TABLE_REQUEST.getPageSize(), TABLE_REQUEST.getSort());
		assertEquals(Long.valueOf(2L), response.getTotal());
		assertNotNull(response.getDataSource());
		List<ProjectMemberTableModel> members = response.getDataSource();
		assertEquals(2, members.size(), "Should have 2 members");
	}

	@Test
	public void testRemoveUserFromProject() throws ProjectWithoutOwnerException {
		service.removeUserFromProject(PROJECT_ID, USER_2.getId(), LOCALE);
		verify(projectService, times(1)).removeUserFromProject(PROJECT, USER_2);
	}

	@Test
	public void testRemoveLastManagerFromProject() {
		assertThrows(ProjectWithoutOwnerException.class, () -> {
			service.removeUserFromProject(PROJECT_ID, USER_3.getId(), LOCALE);
		});
	}

	@Test
	public void testUpdateUserProjectRoleOnProject() throws ProjectWithoutOwnerException, UIConstraintViolationException {
		service.updateUserRoleOnProject(PROJECT_ID, USER_2.getId(), ProjectRole.PROJECT_OWNER.toString(), LOCALE);
		verify(projectService, times(1)).read(PROJECT.getId());
		verify(userService, times(1)).read(USER_2.getId());
		verify(projectService, times(1)).updateUserProjectRole(PROJECT, USER_2, ProjectRole.PROJECT_OWNER, ProjectMetadataRole.LEVEL_4);
	}

	@Test
	public void testUpdateUserMetadataRoleOnProject() throws UIConstraintViolationException {
		service.updateUserMetadataRoleOnProject(PROJECT_ID, USER_2.getId(), ProjectMetadataRole.LEVEL_4.toString(), LOCALE);
		verify(projectService, times(1)).read(PROJECT_ID);
		verify(userService, times(1)).read(USER_2.getId());
		verify(projectService, times(1)).updateUserProjectMetadataRole(PROJECT, USER_2, ProjectMetadataRole.LEVEL_4);
	}

	@Test
	public void testUpdateUserRoleOnProjectNoManager() {
		assertThrows(ProjectWithoutOwnerException.class, () -> {
			service.updateUserRoleOnProject(PROJECT_ID, USER_3.getId(), ProjectRole.PROJECT_USER.toString(),
					LOCALE);
		});
	}

	@Test
	public void testAddMemberToProject() {
		NewMemberRequest request = new NewMemberRequest(USER_3.getId(), ProjectRole.PROJECT_USER.toString(),
				ProjectMetadataRole.LEVEL_1.toString());
		service.addMemberToProject(PROJECT_ID, request, LOCALE);
		verify(projectService, times(1)).addUserToProject(PROJECT, USER_3, ProjectRole.PROJECT_USER,
				ProjectMetadataRole.LEVEL_1);
	}

	private Page<Join<Project, User>> getPagedUsersForProject() {
		return new Page<Join<Project, User>>() {
			@Override
			public int getTotalPages() {
				return 0;
			}

			@Override
			public long getTotalElements() {
				return 2;
			}

			@Override
			public <U> Page<U> map(Function<? super Join<Project, User>, ? extends U> converter) {
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
			public List<Join<Project, User>> getContent() {
				return projectUserJoins;
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
			public Iterator<Join<Project, User>> iterator() {
				return projectUserJoins.iterator();
			}
		};
	}
}
