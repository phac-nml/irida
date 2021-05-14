package ca.corefacility.bioinformatics.irida.ria.web.activities;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.ria.web.activities.dto.Activity;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIActivitiesService;

@Controller
@RequestMapping("/ajax/activities")
public class ActivitiesAjaxController {
	private final UIActivitiesService service;

	@Autowired
	public ActivitiesAjaxController(UIActivitiesService service) {
		this.service = service;
	}

	@GetMapping("/project")
	public List<Activity> getProjectActivities(@RequestParam Long projectId, @RequestParam(defaultValue = "10") int size, Locale locale) {
		return service.geActivitiesForProject(projectId, size, locale);
	}

}
