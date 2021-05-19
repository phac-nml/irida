package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.event.DataAddedToSampleProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.activities.ActivityType;
import ca.corefacility.bioinformatics.irida.ria.web.activities.dto.Activity;
import ca.corefacility.bioinformatics.irida.ria.web.activities.dto.ActivityItem;
import ca.corefacility.bioinformatics.irida.service.ProjectEventService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

import com.google.common.collect.ImmutableList;

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

	public List<Activity> geActivitiesForProject(Long projectId, int size, Locale locale) {
		Project project = projectService.read(projectId);
		Page<ProjectEvent> page = projectEventService.getEventsForProject(project, PageRequest.of(0, size));
		return page.getContent()
				.stream()
				.map(event -> createActivity(event, locale))
				.collect(Collectors.toList());
	}

	private Activity createActivity(ProjectEvent event, Locale locale) {
		if (event instanceof UserRoleSetProjectEvent) {
			UserRoleSetProjectEvent type = (UserRoleSetProjectEvent) event;
			User user = type.getUser();
			Project project = type.getProject();
			String sentence = messageSource.getMessage("event.project.user_added", new Object[] {}, locale);
			ActivityItem userItem = new ActivityItem("/users/" + user.getId(), user.getLabel());
			ActivityItem roleItem = new ActivityItem(null, messageSource.getMessage("projectRole." + type.getRole()
					.toString(), new Object[] {}, locale));
			return new Activity(type.getId(), ActivityType.PROJECT_USER_ROLE.label, sentence, event.getCreatedDate(),
					ImmutableList.of(userItem, roleItem));
		} else if (event instanceof SampleAddedProjectEvent) {
			SampleAddedProjectEvent type = (SampleAddedProjectEvent) event;
			Sample sample = type.getSample();
			Project project = type.getProject();
			ActivityItem sampleItem = new ActivityItem("/projects/" + project.getId() + "/samples/" + sample.getId(),
					sample.getLabel());
			String sentence = messageSource.getMessage("event.project.sample_added", new Object[] {}, locale);
			return new Activity(type.getId(), ActivityType.PROJECT_SAMPLE_ADDED.label, sentence, event.getCreatedDate(),
					ImmutableList.of(sampleItem));
		} else if (event instanceof DataAddedToSampleProjectEvent) {
			DataAddedToSampleProjectEvent type = (DataAddedToSampleProjectEvent) event;
			Sample sample = type.getSample();
			Project project = type.getProject();
			ActivityItem sampleItem = new ActivityItem("/projects/" + project.getId() + "/samples/" + sample.getId() +"/sequenceFiles",
					sample.getLabel());
			String sentence = messageSource.getMessage("event.project.sample_data_added", new Object[] {}, locale);
			return new Activity(event.getId(), ActivityType.PROJECT_SAMPLE_DATA_ADDED.label, sentence,
					event.getCreatedDate(), ImmutableList.of(sampleItem));
		}
		return null;
	}
}
