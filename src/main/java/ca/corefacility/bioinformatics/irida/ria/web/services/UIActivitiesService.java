package ca.corefacility.bioinformatics.irida.ria.web.services;

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

import com.google.common.collect.ImmutableList;

/**
 * UI service for converting events into UI activities.
 */
@Component
public class UIActivitiesService {
	private final ProjectService projectService;
	private final ProjectEventService projectEventService;
	private final MessageSource messageSource;

	@Autowired
	public UIActivitiesService(ProjectService projectService, ProjectEventService projectEventService,
			MessageSource messageSource) {
		this.projectService = projectService;
		this.projectEventService = projectEventService;
		this.messageSource = messageSource;
	}

	/**
	 * Get a page of activites for a project
	 *
	 * @param projectId Identifier for the current project
	 * @param page      Current page of activities being asked for
	 * @param locale    Current users locale
	 * @return The page of activities plus the total amount of activities
	 */
	public PagedListResponse geActivitiesForProject(Long projectId, int page, Locale locale) {
		Project project = projectService.read(projectId);
		Page<ProjectEvent> events = projectEventService.getEventsForProject(project,
				PageRequest.of(page, 10, Sort.Direction.DESC, "createdDate"));
		List<ListItem> activities = events.getContent()
				.stream()
				.map(event -> createActivity(event, locale))
				.collect(Collectors.toList());
		return new PagedListResponse(events.getTotalElements(), activities);
	}

	/**
	 * Format an event into a UI activity based on the type of event
	 *
	 * @param event  the event that occured
	 * @param locale current users locale
	 * @return the UI formatted activity
	 */
	private Activity createActivity(ProjectEvent event, Locale locale) {
		if (event instanceof UserRoleSetProjectEvent) {
			UserRoleSetProjectEvent type = (UserRoleSetProjectEvent) event;
			User user = type.getUser();
			Project project = type.getProject();
			String sentence = messageSource.getMessage("server.ProjectActivity.user_role_updated", new Object[] {},
					locale);
			ActivityItem userItem = new ActivityItem("/users/" + user.getId(), user.getLabel());
			ActivityItem roleItem = new ActivityItem(null, messageSource.getMessage("projectRole." + type.getRole()
					.toString(), new Object[] {}, locale));
			return new Activity(type.getId(), ActivityType.PROJECT_USER_ROLE.label, sentence, event.getCreatedDate(),
					ImmutableList.of(userItem, roleItem));
		} else if (event instanceof UserRemovedProjectEvent) {
			// TODO: This does not seem to be hit?
			UserRemovedProjectEvent type = (UserRemovedProjectEvent) event;
			User user = type.getUser();
			ActivityItem userItem = new ActivityItem("/users/" + user.getId(), user.getLabel());
			String sentence = messageSource.getMessage("server.ProjectActivity.user_removed", new Object[] {}, locale);
			return new Activity(event.getId(), ActivityType.PROJECT_USER_REMOVED.label, sentence, event.getCreatedDate(), ImmutableList.of(userItem));

		} else if (event instanceof SampleAddedProjectEvent) {
			SampleAddedProjectEvent type = (SampleAddedProjectEvent) event;
			Sample sample = type.getSample();
			Project project = type.getProject();
			ActivityItem sampleItem = new ActivityItem("/projects/" + project.getId() + "/samples/" + sample.getId(),
					sample.getLabel());
			String sentence = messageSource.getMessage("server.ProjectActivity.sample_added", new Object[] {}, locale);
			return new Activity(type.getId(), ActivityType.PROJECT_SAMPLE_ADDED.label, sentence, event.getCreatedDate(),
					ImmutableList.of(sampleItem));
		} else if (event instanceof DataAddedToSampleProjectEvent) {
			DataAddedToSampleProjectEvent type = (DataAddedToSampleProjectEvent) event;
			Sample sample = type.getSample();
			Project project = type.getProject();
			ActivityItem sampleItem = new ActivityItem("/projects/" + project.getId() + "/samples/" + sample.getId() + "/sequenceFiles",
					sample.getLabel());
			String sentence = messageSource.getMessage("server.ProjectActivity.sample_data_added", new Object[] {}, locale);
			return new Activity(event.getId(), ActivityType.PROJECT_SAMPLE_DATA_ADDED.label, sentence, event.getCreatedDate(), ImmutableList.of(sampleItem));
		} else if (event instanceof UserGroupRoleSetProjectEvent) {
			UserGroupRoleSetProjectEvent type = (UserGroupRoleSetProjectEvent) event;
			UserGroup group = type.getUserGroup();
			ActivityItem groupItem = new ActivityItem("/groups/" + group.getId(), group.getLabel());
			String role = messageSource.getMessage("projectRole." + type.getRole()
					.toString(), new Object[] {}, locale);
			String sentence = messageSource.getMessage("server.ProjectActivity.user_group_added", new Object[] {}, locale);
			return new Activity(event.getId(), ActivityType.PROJECT_USER_GROUP_ADDED.label, sentence, event.getCreatedDate(), ImmutableList.of(groupItem));
		}else if (event instanceof UserGroupRemovedProjectEvent) {
			UserGroupRemovedProjectEvent type = (UserGroupRemovedProjectEvent) event;
			UserGroup group = type.getUserGroup();
			ActivityItem groupItem = new ActivityItem("/groups/" + group.getId(), group.getLabel());
			String sentence = messageSource.getMessage("server.ProjectActivity.user_group_removed", new Object[] {}, locale);
			return new Activity(event.getId(), ActivityType.PROJECT_USER_GROUP_REMOVED.label, sentence, event.getCreatedDate(), ImmutableList.of(groupItem));
		}
		return null;
	}
}
