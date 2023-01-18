package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.event.*;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.ria.web.activities.dto.Activity;
import ca.corefacility.bioinformatics.irida.ria.web.dto.list.PagedListResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIActivitiesService;
import ca.corefacility.bioinformatics.irida.service.ProjectEventService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class UIActivitiesServiceTest {
	private UIActivitiesService service;

	private final Sample sample = new Sample("SAMPLE_1");
	private final Project project = new Project("PROJECT_1");

	private final Sample sample2 = new Sample("SAMPLE_2");
	private final Project project2 = new Project("PROJECT_2");

	private final Sample sample3 = new Sample("SAMPLE_3");

	private ProjectEventService eventService;
	private UserService userService;

	User user = new User("somename", "fred@nowhere.ca", " P@12345", "Fred", "Penner", "12221234567");
	User user2 = new User("somename2", "fred2@nowhere.ca", " P@12345", "Fred2", "Penner2", "12221234568");

	@BeforeEach
	public void setUp() {
		ProjectService projectService = Mockito.mock(ProjectService.class);
		eventService = Mockito.mock(ProjectEventService.class);
		MessageSource messageSource = Mockito.mock(MessageSource.class);
		userService = Mockito.mock(UserService.class);
		this.service = new UIActivitiesService(projectService, eventService, messageSource, userService);

		// Create mock data
		project.setId(1L);
		sample.setId(1L);

		project2.setId(2L);
		sample2.setId(2L);

		Mockito.when(projectService.read(1L)).thenReturn(project);
		Mockito.when(projectService.read(2L)).thenReturn(project2);

		user2.setSystemRole(Role.ROLE_ADMIN);
	}

	@Test
	public void testGetActivitiesForProject() {
		Mockito.when(eventService.getEventsForProject(eq(project), any()))
				.thenReturn(createMockProjectEvents(null, project, false));
		PagedListResponse response = service.getActivitiesForProject(1L, 0, Locale.ENGLISH);
		assertEquals(4, response.getTotal(), "Should have 4 events");
		assertEquals("project_sample_added", ((Activity) response.getContent().get(0)).getType(),
				"First should be a project_sample_added event");
		assertEquals("project_user_role_updated", ((Activity) response.getContent().get(1)).getType(),
				"Second should be a project_user_role_updated event");
		assertEquals("project_user_removed", ((Activity) response.getContent().get(2)).getType(),
				"Third should be a project_user_removed event");
		assertEquals("project_user_group_added", ((Activity) response.getContent().get(3)).getType(),
				"Forth should be a project_user_group_added event");

		Mockito.when(eventService.getEventsForProject(eq(project2), any()))
				.thenReturn(createMockProjectEvents(null, project2, false));
		response = service.getActivitiesForProject(2L, 0, Locale.ENGLISH);
		assertEquals(5, response.getTotal(), "Should have 5 events");
		assertEquals("project_sample_added", ((Activity) response.getContent().get(0)).getType(),
				"First should be a project_sample_added event");
		assertEquals("project_user_role_updated", ((Activity) response.getContent().get(1)).getType(),
				"Second should be a project_user_role_updated event");
		assertEquals("project_user_removed", ((Activity) response.getContent().get(2)).getType(),
				"Third should be a project_user_removed event");
		assertEquals("project_user_group_added", ((Activity) response.getContent().get(3)).getType(),
				"Forth should be a project_user_group_added event");
		assertEquals("project_sample_added", ((Activity) response.getContent().get(4)).getType(),
				"Fifth should be a project_sample_added event");
	}

	@Test
	public void testGetAllProjectsRecentActivity() {
		Mockito.when(eventService.getAllProjectsEvents(any())).thenReturn(createMockProjectEvents(user2, null, true));
		PagedListResponse response = service.getAllRecentActivities(0, Locale.ENGLISH);
		assertEquals(9, response.getTotal(), "Should have 9 events");
		assertEquals("project_sample_added", ((Activity) response.getContent().get(0)).getType(),
				"First should be a project_sample_added event");
		assertEquals("project_user_role_updated", ((Activity) response.getContent().get(1)).getType(),
				"Second should be a project_user_role_updated event");
		assertEquals("project_user_removed", ((Activity) response.getContent().get(2)).getType(),
				"Third should be a project_user_removed event");
		assertEquals("project_user_group_added", ((Activity) response.getContent().get(3)).getType(),
				"Fourth should be a project_user_group_added event");
		assertEquals("project_sample_added", ((Activity) response.getContent().get(4)).getType(),
				"Fifth should be a project_sample_added event");
		assertEquals("project_user_role_updated", ((Activity) response.getContent().get(5)).getType(),
				"Sixth should be a project_user_role_updated event");
		assertEquals("project_user_removed", ((Activity) response.getContent().get(6)).getType(),
				"Seventh should be a project_user_removed event");
		assertEquals("project_user_group_added", ((Activity) response.getContent().get(7)).getType(),
				"Eighth should be a project_user_group_added event");
		assertEquals("project_sample_added", ((Activity) response.getContent().get(8)).getType(),
				"Nineth should be a project_sample_added event");
	}

	@Test
	public void testGetUserProjectsRecentActivity() {
		Mockito.when(eventService.getEventsForUser(eq(user), any()))
				.thenReturn(createMockProjectEvents(user, null, false));

		Mockito.when(userService.getUserByUsername("FRED")).thenReturn(user);
		Principal principal = () -> "FRED";
		PagedListResponse response = service.getRecentActivitiesForUser(0, Locale.ENGLISH, principal);
		assertEquals(4, response.getTotal(), "Should have 4 events");
		assertEquals("project_sample_added", ((Activity) response.getContent().get(0)).getType(),
				"First should be a project_sample_added event");
		assertEquals("project_user_role_updated", ((Activity) response.getContent().get(1)).getType(),
				"Second should be a project_user_role_updated event");
		assertEquals("project_user_removed", ((Activity) response.getContent().get(2)).getType(),
				"Third should be a project_user_removed event");
		assertEquals("project_user_group_added", ((Activity) response.getContent().get(3)).getType(),
				"Forth should be a project_user_group_added event");

		Mockito.when(eventService.getEventsForUser(eq(user2), any()))
				.thenReturn(createMockProjectEvents(user2, null, false));
		principal = () -> "FRED2";
		Mockito.when(userService.getUserByUsername("FRED2")).thenReturn(user2);
		response = service.getRecentActivitiesForUser(0, Locale.ENGLISH, principal);
		assertEquals(5, response.getTotal(), "Should have 5 events");
		assertEquals("project_sample_added", ((Activity) response.getContent().get(0)).getType(),
				"First should be a project_sample_added event");
		assertEquals("project_user_role_updated", ((Activity) response.getContent().get(1)).getType(),
				"Second should be a project_user_role_updated event");
		assertEquals("project_user_removed", ((Activity) response.getContent().get(2)).getType(),
				"Third should be a project_user_removed event");
		assertEquals("project_user_group_added", ((Activity) response.getContent().get(3)).getType(),
				"Forth should be a project_user_group_added event");
		assertEquals("project_sample_added", ((Activity) response.getContent().get(4)).getType(),
				"Fifth should be a project_sample_added event");
	}

	private Page<ProjectEvent> createMockProjectEvents(User currUser, Project specificProject,
			boolean adminListingPage) {
		ProjectSampleJoin join = new ProjectSampleJoin(project, sample, true);
		SampleAddedProjectEvent sampleAddedProjectEvent = new SampleAddedProjectEvent(join);

		ProjectUserJoin projectUserJoinUser = new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER);
		UserRoleSetProjectEvent userRoleSetProjectEventUser = new UserRoleSetProjectEvent(projectUserJoinUser);

		UserRemovedProjectEvent userRemovedProjectEvent = new UserRemovedProjectEvent(project, user);

		UserGroup userGroup = new UserGroup("MY GROUP");
		UserGroupProjectJoin userGroupProjectJoin = new UserGroupProjectJoin(project, userGroup,
				ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1);
		UserGroupRoleSetProjectEvent userGroupRoleSetProjectEvent = new UserGroupRoleSetProjectEvent(
				userGroupProjectJoin);

		ProjectSampleJoin join2 = new ProjectSampleJoin(project2, sample2, true);
		SampleAddedProjectEvent sampleAddedProjectEvent2 = new SampleAddedProjectEvent(join2);

		ProjectSampleJoin join3 = new ProjectSampleJoin(project2, sample3, true);
		SampleAddedProjectEvent sampleAddedProjectEvent3 = new SampleAddedProjectEvent(join3);

		ProjectUserJoin projectUserJoinUser2 = new ProjectUserJoin(project2, user2, ProjectRole.PROJECT_USER);
		UserRoleSetProjectEvent userRoleSetProjectEventUser2 = new UserRoleSetProjectEvent(projectUserJoinUser2);

		UserRemovedProjectEvent userRemovedProjectEvent2 = new UserRemovedProjectEvent(project2, user2);

		UserGroup userGroup2 = new UserGroup("MY GROUP 2");
		UserGroupProjectJoin userGroupProjectJoin2 = new UserGroupProjectJoin(project2, userGroup2,
				ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_4);
		UserGroupRoleSetProjectEvent userGroupRoleSetProjectEvent2 = new UserGroupRoleSetProjectEvent(
				userGroupProjectJoin2);

		return new Page<ProjectEvent>() {
			List<ProjectEvent> eventListAll = ImmutableList.of(sampleAddedProjectEvent, userRoleSetProjectEventUser,
					userRemovedProjectEvent, userGroupRoleSetProjectEvent, sampleAddedProjectEvent2,
					userRoleSetProjectEventUser2, userRemovedProjectEvent2, userGroupRoleSetProjectEvent2,
					sampleAddedProjectEvent3);

			List<ProjectEvent> eventListUser = ImmutableList.of(sampleAddedProjectEvent, userRoleSetProjectEventUser,
					userRemovedProjectEvent, userGroupRoleSetProjectEvent);

			List<ProjectEvent> eventListUser2 = ImmutableList.of(sampleAddedProjectEvent2, userRoleSetProjectEventUser2,
					userRemovedProjectEvent2, userGroupRoleSetProjectEvent2, sampleAddedProjectEvent3);

			List<ProjectEvent> project1Events = ImmutableList.of(sampleAddedProjectEvent, userRoleSetProjectEventUser,
					userRemovedProjectEvent, userGroupRoleSetProjectEvent);

			List<ProjectEvent> project2Events = ImmutableList.of(sampleAddedProjectEvent2, userRoleSetProjectEventUser2,
					userRemovedProjectEvent2, userGroupRoleSetProjectEvent2, sampleAddedProjectEvent3);

			@Override
			public int getTotalPages() {
				return 1;
			}

			@Override
			public long getTotalElements() {
				if (currUser != null && currUser.getSystemRole() == Role.ROLE_ADMIN && adminListingPage)
					return eventListAll.size();
				else if (currUser == null && specificProject == project)
					return project1Events.size();
				else if (currUser == null && specificProject == project2)
					return project2Events.size();
				else if (currUser == user)
					return eventListUser.size();
				else
					return eventListUser2.size();
			}

			@Override
			public <U> Page<U> map(Function<? super ProjectEvent, ? extends U> converter) {
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
				if (currUser != null && currUser.getSystemRole() == Role.ROLE_ADMIN && adminListingPage)
					return eventListAll.size();
				else if (currUser == null && specificProject == project)
					return project1Events.size();
				else if (currUser == null && specificProject == project2)
					return project2Events.size();
				else if (currUser == user)
					return eventListUser.size();
				else
					return eventListUser2.size();
			}

			@Override
			public List<ProjectEvent> getContent() {
				if (currUser != null && currUser.getSystemRole() == Role.ROLE_ADMIN && adminListingPage)
					return eventListAll;
				else if (currUser == null && specificProject == project)
					return project1Events;
				else if (currUser == null && specificProject == project2)
					return project2Events;
				else if (currUser == user)
					return eventListUser;
				else
					return eventListUser2;
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
			public Iterator<ProjectEvent> iterator() {
				return null;
			}
		};
	}
}
