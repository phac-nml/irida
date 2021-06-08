package ca.corefacility.bioinformatics.irida.ria.web.activities;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIActivitiesService;

/**
 * AJAX controller to handle actitivies
 */
@Controller
@RequestMapping("/ajax/activities")
public class ActivitiesAjaxController {
	private final UIActivitiesService service;

	@Autowired
	public ActivitiesAjaxController(UIActivitiesService service) {
		this.service = service;
	}

	/**
	 * Get a specific page of activities for a project
	 *
	 * @param projectId Identifier for the current project
	 * @param page      Page requested
	 * @param locale    Current users locale
	 * @return List of activities and the total number of activities
	 */
	@GetMapping("/project")
	public ResponseEntity<AjaxResponse> getProjectActivities(@RequestParam Long projectId,
			@RequestParam(defaultValue = "0") int page, Locale locale) {
		return ResponseEntity.ok(service.geActivitiesForProject(projectId, page, locale));
	}

}
