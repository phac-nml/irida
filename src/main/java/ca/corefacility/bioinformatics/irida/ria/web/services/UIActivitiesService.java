package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.enums.UserGroupRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.*;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.ria.web.activities.ActivityType;
import ca.corefacility.bioinformatics.irida.ria.web.activities.dto.Activity;
import ca.corefacility.bioinformatics.irida.ria.web.activities.dto.ActivityItem;
import ca.corefacility.bioinformatics.irida.ria.web.dto.list.ListItem;
import ca.corefacility.bioinformatics.irida.ria.web.dto.list.PagedListResponse;
import ca.corefacility.bioinformatics.irida.service.ProjectEventService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.google.common.collect.ImmutableList;

/**
 * UI service for converting events into UI activities.
 */
@Component
public class UIActivitiesService {
	private final ProjectService projectService;
	private final ProjectEventService projectEventService;
	private final MessageSource messageSource;
	private final UserService userService;

	@Autowired
	public UIActivitiesService(ProjectService projectService, ProjectEventService projectEventService,
			MessageSource messageSource, UserService userService) {
		this.projectService = projectService;
		this.projectEventService = projectEventService;
		this.messageSource = messageSource;
		this.userService = userService;
	}

	/**
	 * Get a page of activities for a project
	 *
	 * @param projectId Identifier for the current project
	 * @param page      Current page of activities being asked for
	 * @param locale    Current users locale
	 * @return The page of activities plus the total amount of activities
	 */
	public PagedListResponse getActivitiesForProject(Long projectId, int page, Locale locale) {
		Project project = projectService.read(projectId);
		final int DEFAULT_PAGE_SIZE = 10;
		Page<ProjectEvent> events = projectEventService.getEventsForProject(project,
				PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.Direction.DESC, "createdDate"));
		List<ListItem> activities = events.getContent()
				.stream()
				.map(event -> createProjectActivity(event, locale))
				.collect(Collectors.toList());
		return new PagedListResponse(events.getTotalElements(), activities);
	}

	/**
	 * Get a page of recent activities for a user
	 *
	 * @param page      Current page of activities being asked for
	 * @param locale    Current users locale
	 * @param principal The currently logged on user
	 * @return The page of activities plus the total amount of activities
	 */
	public PagedListResponse getRecentActivitiesForUser(int page, Locale locale, Principal principal) {
		final int DEFAULT_PAGE_SIZE = 10;
		String userName = principal.getName();
		User user = userService.getUserByUsername(userName);

		Page<ProjectEvent> events = projectEventService.getEventsForUser(user,
					PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.Direction.DESC, "createdDate"));


		List<ListItem> activities = events.getContent()
				.stream()
				.map(event -> createRecentActivity(event, locale))
				.collect(Collectors.toList());
		return new PagedListResponse(events.getTotalElements(), activities);
	}

	/**
	 * Get a specific page of recent activities for all projects for admin
	 *
	 * @param page   Current page of activities being asked for
	 * @param locale Current users locale
	 * @return List of activities and the total number of activities
	 */
	public PagedListResponse getAllRecentActivities(int page, Locale locale) {
		final int DEFAULT_PAGE_SIZE = 10;
		Page<ProjectEvent> events = projectEventService.getAllProjectsEvents(
				PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.Direction.DESC, "createdDate"));

		List<ListItem> activities = events.getContent()
				.stream()
				.map(event -> createRecentActivity(event, locale))
				.collect(Collectors.toList());
		return new PagedListResponse(events.getTotalElements(), activities);
	}

	/**
	 * Format an event into a UI project activity based on the type of event
	 *
	 * @param event  the event that occurred
	 * @param locale current users locale
	 * @return the UI formatted activity
	 */
	private Activity createProjectActivity(ProjectEvent event, Locale locale) {
		if (event instanceof UserRoleSetProjectEvent) {
			UserRoleSetProjectEvent type = (UserRoleSetProjectEvent) event;
			User user = type.getUser();
			String sentence = messageSource.getMessage("server.ProjectActivity.user_role_updated", new Object[] {},
					locale);
			ActivityItem userItem = new ActivityItem("/users/" + user.getId(), user.getLabel());
			ActivityItem roleItem = new ActivityItem(null, messageSource.getMessage("projectRole." + type.getRole()
					.toString(), new Object[] {}, locale));
			return new Activity(type.getId(), ActivityType.PROJECT_USER_ROLE.label, sentence, event.getCreatedDate(),
					ImmutableList.of(userItem, roleItem));
		} else if (event instanceof UserRemovedProjectEvent) {
			UserRemovedProjectEvent type = (UserRemovedProjectEvent) event;
			User user = type.getUser();
			ActivityItem userItem = new ActivityItem("/users/" + user.getId(), user.getLabel());
			String sentence = messageSource.getMessage("server.ProjectActivity.user_removed", new Object[] {}, locale);
			return new Activity(event.getId(), ActivityType.PROJECT_USER_REMOVED.label, sentence,
					event.getCreatedDate(), ImmutableList.of(userItem));

		} else if (event instanceof SampleAddedProjectEvent) {
			SampleAddedProjectEvent type = (SampleAddedProjectEvent) event;
			Sample sample = type.getSample();
			Project project = type.getProject();
			ActivityItem sampleItem = new ActivityItem("/projects/" + project.getId() + "/samples/" + sample.getId(),
					sample.getLabel());
			String sentence = messageSource.getMessage("server.ProjectActivity.sample_added", new Object[] {}, locale);
			return new Activity(type.getId(), ActivityType.PROJECT_SAMPLE_ADDED.label, sentence, event.getCreatedDate(),
					ImmutableList.of(sampleItem));
		} else if (event instanceof SampleRemovedProjectEvent) {
			SampleRemovedProjectEvent type = (SampleRemovedProjectEvent) event;
			String sampleName = type.getSampleName();
			ActivityItem sampleItem = new ActivityItem(null, sampleName);
			String sentence = messageSource.getMessage("server.ProjectActivity.sample_removed", new Object[] {}, locale);
			return new Activity(type.getId(), ActivityType.PROJECT_SAMPLE_REMOVED.label, sentence, event.getCreatedDate(),
					ImmutableList.of(sampleItem));
		}
		else if (event instanceof DataAddedToSampleProjectEvent) {
			DataAddedToSampleProjectEvent type = (DataAddedToSampleProjectEvent) event;
			Sample sample = type.getSample();
			Project project = type.getProject();
			ActivityItem sampleItem = new ActivityItem(
					"/projects/" + project.getId() + "/samples/" + sample.getId() + "/sequenceFiles",
					sample.getLabel());
			String sentence = messageSource.getMessage("server.ProjectActivity.sample_data_added", new Object[] {},
					locale);
			return new Activity(event.getId(), ActivityType.PROJECT_SAMPLE_DATA_ADDED.label, sentence,
					event.getCreatedDate(), ImmutableList.of(sampleItem));
		} else if (event instanceof UserGroupRoleSetProjectEvent) {
			UserGroupRoleSetProjectEvent type = (UserGroupRoleSetProjectEvent) event;
			UserGroup group = type.getUserGroup();
			ActivityItem groupItem = new ActivityItem("/groups/" + group.getId(), group.getLabel());
			String sentence = messageSource.getMessage("server.ProjectActivity.user_group_added", new Object[] {},
					locale);
			return new Activity(event.getId(), ActivityType.PROJECT_USER_GROUP_ADDED.label, sentence,
					event.getCreatedDate(), ImmutableList.of(groupItem));
		} else if (event instanceof UserGroupRemovedProjectEvent) {
			UserGroupRemovedProjectEvent type = (UserGroupRemovedProjectEvent) event;
			UserGroup group = type.getUserGroup();
			ActivityItem groupItem = new ActivityItem("/groups/" + group.getId(), group.getLabel());
			String sentence = messageSource.getMessage("server.ProjectActivity.user_group_removed", new Object[] {},
					locale);
			return new Activity(event.getId(), ActivityType.PROJECT_USER_GROUP_REMOVED.label, sentence,
					event.getCreatedDate(), ImmutableList.of(groupItem));
		}
		return null;
	}

	/**
	 * Format an event into a UI recent activity based on the type of event
	 *
	 * @param event  the event that occurred
	 * @param locale current users locale
	 * @return the UI formatted activity
	 */
	private Activity createRecentActivity(ProjectEvent event, Locale locale) {
		if (event instanceof UserRoleSetProjectEvent) {
			UserRoleSetProjectEvent type = (UserRoleSetProjectEvent) event;
			User user = type.getUser();
			Project project = type.getProject();
			String sentence = messageSource.getMessage("server.RecentActivity.user_role_updated", new Object[] {},
					locale);
			ActivityItem userItem = new ActivityItem("/users/" + user.getId(), user.getLabel());
			ActivityItem roleItem = new ActivityItem(null, messageSource.getMessage("projectRole." + type.getRole()
					.toString(), new Object[] {}, locale));
			ActivityItem projectItem = new ActivityItem("/projects/" + project.getId(), project.getLabel());
			return new Activity(type.getId(), ActivityType.PROJECT_USER_ROLE.label, sentence, event.getCreatedDate(),
					ImmutableList.of(userItem, roleItem, projectItem));
		} else if (event instanceof UserRemovedProjectEvent) {
			UserRemovedProjectEvent type = (UserRemovedProjectEvent) event;
			User user = type.getUser();
			Project project = type.getProject();
			ActivityItem userItem = new ActivityItem("/users/" + user.getId(), user.getLabel());
			ActivityItem projectItem = new ActivityItem("/projects/" + project.getId(), project.getLabel());
			String sentence = messageSource.getMessage("server.RecentActivity.user_removed", new Object[] {}, locale);
			return new Activity(event.getId(), ActivityType.PROJECT_USER_REMOVED.label, sentence,
					event.getCreatedDate(), ImmutableList.of(userItem, projectItem));
		} else if (event instanceof SampleAddedProjectEvent) {
			SampleAddedProjectEvent type = (SampleAddedProjectEvent) event;
			Sample sample = type.getSample();
			Project project = type.getProject();
			ActivityItem sampleItem = new ActivityItem("/projects/" + project.getId() + "/samples/" + sample.getId(),
					sample.getLabel());
			ActivityItem projectItem = new ActivityItem("/projects/" + project.getId(), project.getLabel());
			String sentence = messageSource.getMessage("server.RecentActivity.sample_added", new Object[] {}, locale);
			return new Activity(type.getId(), ActivityType.PROJECT_SAMPLE_ADDED.label, sentence, event.getCreatedDate(),
					ImmutableList.of(sampleItem, projectItem));
		} else if (event instanceof SampleRemovedProjectEvent) {
			SampleRemovedProjectEvent type = (SampleRemovedProjectEvent) event;
			String sampleName = type.getSampleName();
			Project project = type.getProject();
			ActivityItem sampleItem = new ActivityItem(null, sampleName);
			ActivityItem projectItem = new ActivityItem("/projects/" + project.getId(), project.getLabel());
			String sentence = messageSource.getMessage("server.RecentActivity.sample_removed", new Object[] {}, locale);
			return new Activity(type.getId(), ActivityType.PROJECT_SAMPLE_REMOVED.label, sentence, event.getCreatedDate(),
					ImmutableList.of(sampleItem, projectItem));
		}else if (event instanceof DataAddedToSampleProjectEvent) {
			DataAddedToSampleProjectEvent type = (DataAddedToSampleProjectEvent) event;
			Sample sample = type.getSample();
			Project project = type.getProject();
			ActivityItem sampleItem = new ActivityItem(
					"/projects/" + project.getId() + "/samples/" + sample.getId() + "/sequenceFiles",
					sample.getLabel());
			String sentence = messageSource.getMessage("server.RecentActivity.sample_data_added", new Object[] {},
					locale);
			return new Activity(event.getId(), ActivityType.PROJECT_SAMPLE_DATA_ADDED.label, sentence,
					event.getCreatedDate(), ImmutableList.of(sampleItem));
		} else if (event instanceof UserGroupRoleSetProjectEvent) {
			UserGroupRoleSetProjectEvent type = (UserGroupRoleSetProjectEvent) event;
			UserGroup group = type.getUserGroup();
			Project project = type.getProject();
			ActivityItem groupItem = new ActivityItem("/groups/" + group.getId(), group.getLabel());
			ActivityItem projectItem = new ActivityItem("/projects/" + project.getId(), project.getLabel());
			String sentence = messageSource.getMessage("server.RecentActivity.user_group_added", new Object[] {},
					locale);
			return new Activity(event.getId(), ActivityType.PROJECT_USER_GROUP_ADDED.label, sentence,
					event.getCreatedDate(), ImmutableList.of(groupItem, projectItem));
		} else if (event instanceof UserGroupRemovedProjectEvent) {
			UserGroupRemovedProjectEvent type = (UserGroupRemovedProjectEvent) event;
			UserGroup group = type.getUserGroup();
			Project project = type.getProject();
			ActivityItem groupItem = new ActivityItem("/groups/" + group.getId(), group.getLabel());
			ActivityItem projectItem = new ActivityItem("/projects/" + project.getId(), project.getLabel());
			String sentence = messageSource.getMessage("server.RecentActivity.user_group_removed", new Object[] {},
					locale);
			return new Activity(event.getId(), ActivityType.PROJECT_USER_GROUP_REMOVED.label, sentence,
					event.getCreatedDate(), ImmutableList.of(groupItem, projectItem));
		}
		return null;
	}
}
