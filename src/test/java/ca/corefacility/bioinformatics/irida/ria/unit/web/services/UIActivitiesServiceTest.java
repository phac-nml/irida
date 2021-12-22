package ca.corefacility.bioinformatics.irida.ria.unit.web.services;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.event.*;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;
import ca.corefacility.bioinformatics.irida.ria.web.activities.dto.Activity;
import ca.corefacility.bioinformatics.irida.ria.web.dto.list.PagedListResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIActivitiesService;
import ca.corefacility.bioinformatics.irida.service.ProjectEventService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

import com.google.common.collect.ImmutableList;

import static org.mockito.ArgumentMatchers.any;

public class UIActivitiesServiceTest {
	private UIActivitiesService service;

	private final Sample sample = new Sample("SAMPLE_1");
	private final Project project = new Project("PROJECT_1");

	@Before
	public void setUp() {
		ProjectService projectService = Mockito.mock(ProjectService.class);
		ProjectEventService eventService = Mockito.mock(ProjectEventService.class);
		MessageSource messageSource = Mockito.mock(MessageSource.class);
		this.service = new UIActivitiesService(projectService, eventService, messageSource);

		// Create mock data
		project.setId(1l);
		sample.setId(1l);

		Mockito.when(projectService.read(1l))
				.thenReturn(project);
		Mockito.when(eventService.getEventsForProject(any(), any()))
				.thenReturn(createMockProjectEvents());
	}

	@Test
	public void testGetActivitiesForProject() {
		PagedListResponse response = service.geActivitiesForProject(1l, 0, Locale.ENGLISH);
		Assert.assertEquals("Should have 4 events", 4, response.getTotal());
		Assert.assertEquals("First should be a project_sample_added event", "project_sample_added", ((Activity)response.getContent().get(0)).getType());
		Assert.assertEquals("Second should be a project_user_role_updated event", "project_user_role_updated", ((Activity)response.getContent().get(1)).getType());
		Assert.assertEquals("Third should be a project_user_removed event", "project_user_removed", ((Activity)response.getContent().get(2)).getType());
		Assert.assertEquals("Forth should be a project_user_group_added event", "project_user_group_added", ((Activity)response.getContent().get(3)).getType());
	}

	private Page<ProjectEvent> createMockProjectEvents() {
		ProjectSampleJoin join = new ProjectSampleJoin(project, sample, true);
		SampleAddedProjectEvent sampleAddedProjectEvent = new SampleAddedProjectEvent(join);

		User user = new User("somename", "fred@nowhere.ca"," P@12345", "Fred", "Penner", "12221234567");
		ProjectUserJoin projectUserJoinUser = new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER);
		UserRoleSetProjectEvent userRoleSetProjectEventUser = new UserRoleSetProjectEvent(projectUserJoinUser);

		UserRemovedProjectEvent userRemovedProjectEvent = new UserRemovedProjectEvent(project, user);

		UserGroup userGroup = new UserGroup("MY GROUP");
		UserGroupProjectJoin userGroupProjectJoin = new UserGroupProjectJoin(project, userGroup,
				ProjectRole.PROJECT_USER);
		UserGroupRoleSetProjectEvent userGroupRoleSetProjectEvent = new UserGroupRoleSetProjectEvent(
				userGroupProjectJoin);

		return new Page<ProjectEvent>() {
			@Override
			public int getTotalPages() {
				return 1;
			}

			@Override
			public long getTotalElements() {
				return 4;
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
				return 4;
			}

			@Override
			public List<ProjectEvent> getContent() {
				return ImmutableList.of(sampleAddedProjectEvent, userRoleSetProjectEventUser, userRemovedProjectEvent, userGroupRoleSetProjectEvent);
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
