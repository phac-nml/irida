package ca.corefacility.bioinformatics.irida.web.controller.test.unit.projects;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.subscription.ProjectSubscription;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.projects.ProjectSubscriptionsAjaxController;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectSubscriptionService;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserProjectDetailsModel;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.ProjectSubscriptionService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProjectSubscriptionsAjaxControllerTest {
	private ProjectSubscriptionService projectSubscriptionService;
	private UserService userService;
	private ProjectService projectService;
	private MessageSource messageSource;
	private UIProjectSubscriptionService uiProjectSubscriptionService;
	private ProjectSubscriptionsAjaxController controller;

	private final User USER = new User(1L, "Elsa", "elsa@arendelle.ca", "Password1!", "Elsa", "Oldenburg", "1234");
	private final Project PROJECT = new Project("Project 1");
	private final ProjectSubscription PROJECT_SUBSCRIPTION = new ProjectSubscription(USER, PROJECT, false);
	private final ProjectUserJoin PROJECT_USER_JOIN = new ProjectUserJoin(PROJECT, USER, ProjectRole.PROJECT_USER);
	private final UserGroup USER_GROUP = new UserGroup("Group 1");
	private final UserGroupProjectJoin USER_GROUP_PROJECT_JOIN = new UserGroupProjectJoin(PROJECT, USER_GROUP,
			ProjectRole.PROJECT_OWNER, ProjectMetadataRole.LEVEL_4);
	private final Page<ProjectSubscription> PROJECT_SUBSCRIPTION_PAGE = new Page<>() {
		@Override
		public int getTotalPages() {
			return 1;
		}

		@Override
		public long getTotalElements() {
			return 1;
		}

		@Override
		public <U> Page<U> map(Function<? super ProjectSubscription, ? extends U> function) {
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
		public List<ProjectSubscription> getContent() {
			return List.of(PROJECT_SUBSCRIPTION);
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
		public Iterator<ProjectSubscription> iterator() {
			return null;
		}
	};

	@BeforeEach
	void setUp() {
		projectSubscriptionService = mock(ProjectSubscriptionService.class);
		userService = mock(UserService.class);
		projectService = mock(ProjectService.class);
		messageSource = mock(MessageSource.class);
		uiProjectSubscriptionService = new UIProjectSubscriptionService(projectSubscriptionService, userService,
				projectService, messageSource);
		controller = new ProjectSubscriptionsAjaxController(uiProjectSubscriptionService);

		when(projectSubscriptionService.read(anyLong())).thenReturn(PROJECT_SUBSCRIPTION);
		when(userService.read(anyLong())).thenReturn(USER);
		//		when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Anything can work here");
		when(projectSubscriptionService.getProjectSubscriptionsForUser(any(), anyInt(), anyInt(), any())).thenReturn(
				PROJECT_SUBSCRIPTION_PAGE);
		when(projectService.getProjectUserJoin(any(), any())).thenReturn(PROJECT_USER_JOIN);
		when(projectService.getUserGroupProjectJoins(any(), any())).thenReturn(List.of(USER_GROUP_PROJECT_JOIN));
	}

	@Test
	void updateProjectSubscriptionTest() {
		ResponseEntity<AjaxResponse> response = controller.updateProjectSubscription(USER.getId(), true,
				Locale.ENGLISH);
		assertEquals(response.getStatusCode(), HttpStatus.OK, "Received an 200 OK response");
	}

	@Test
	void getProjectSubscriptionsForUserTest() {
		TableRequest request = new TableRequest();
		request.setSortColumn("id");
		request.setSortDirection("ascend");
		request.setCurrent(0);
		request.setPageSize(10);

		ResponseEntity<TableResponse<UserProjectDetailsModel>> responseEnity = controller.getProjectSubscriptionsForUser(
				USER.getId(), request);
		TableResponse<UserProjectDetailsModel> response = responseEnity.getBody();

		assertEquals(1, response.getTotal(), "Should have the correct number of total entries");
		assertEquals(1, response.getDataSource().size(), "Should have 1 project subscription");

		UserProjectDetailsModel user_project_details = response.getDataSource().get(0);
		assertEquals(PROJECT.getName(), user_project_details.getProjectName(), "Should have the correct project");
		assertEquals(USER_GROUP_PROJECT_JOIN.getProjectRole().toString(), user_project_details.getRoleName(),
				"Should have the correct role");
	}
}
