package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;
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
			String sentence = messageSource.getMessage("event.user_added", new Object[] {}, locale);
			ActivityItem userItem = new ActivityItem("/users/" + user.getId(), user.getLabel());
			ActivityItem roleItem = new ActivityItem(null, messageSource.getMessage("projectRole." + type.getRole()
					.toString(), new Object[] {}, locale));
			ActivityItem projectItem = new ActivityItem("/projects/" + project.getId(), project.getLabel());
			return new Activity(sentence, event.getCreatedDate(), ImmutableList.of(userItem, roleItem, projectItem));
		}
		return null;
	}
}
