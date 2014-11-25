package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.model.event.ProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.SampleAddedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRemovedProjectEvent;
import ca.corefacility.bioinformatics.irida.model.event.UserRoleSetProjectEvent;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.service.ProjectEventService;
import ca.corefacility.bioinformatics.irida.service.ProjectService;

import com.google.common.collect.ImmutableMap;

@Controller
@RequestMapping("/events")
public class ProjectEventsController {
	Map<Class<? extends ProjectEvent>, String> fragmentNames = ImmutableMap.of(UserRoleSetProjectEvent.class,
			"user-role-event", UserRemovedProjectEvent.class, "user-removed-event", SampleAddedProjectEvent.class,
			"sample-added-event");

	@Autowired
	private ProjectEventService eventService;

	@Autowired
	private ProjectService projectService;

	@RequestMapping("/project/{projectId}")
	public String getRecentEventsForProject(@PathVariable Long projectId, Model model) {
		Project project = projectService.read(projectId);

		Page<ProjectEvent> lastTenEventsForProject = eventService.getLastTenEventsForProject(project);
		List<Map<String, Object>> eventInfo = new ArrayList<>();

		for (ProjectEvent e : lastTenEventsForProject) {
			Map<String, Object> info = new HashMap<>();
			info.put("name", fragmentNames.get(e.getClass()));
			info.put("event", e);
			eventInfo.add(info);
		}
		
		model.addAttribute("events", eventInfo);

		return "events/events";
	}
}
