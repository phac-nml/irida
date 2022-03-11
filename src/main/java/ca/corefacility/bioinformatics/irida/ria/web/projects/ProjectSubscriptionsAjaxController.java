package ca.corefacility.bioinformatics.irida.ria.web.projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.subscription.ProjectSubscription;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectSubscriptionService;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserProjectDetailsModel;

/**
 * Controller for handling all AJAX requests for the {@link ProjectSubscription} UIs
 */
@RestController
@RequestMapping("/ajax/subscriptions")
public class ProjectSubscriptionsAjaxController {
	private final UIProjectSubscriptionService service;

	@Autowired
	public ProjectSubscriptionsAjaxController(UIProjectSubscriptionService service) {
		this.service = service;
	}

	/**
	 * Update a {@link ProjectSubscription}
	 *
	 * @param id        the identifier of the {@link ProjectSubscription} to update
	 * @param subscribe whether to subscribe or unsubscribe the user to/from the project
	 */
	@RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
	public void updateProjectSubscription(@PathVariable Long id, @RequestParam boolean subscribe) {
		service.updateProjectSubscription(id, subscribe);
	}

	/**
	 * Get the projects associated with a user
	 *
	 * @param userId  - the id for the user to show project subscriptions for
	 * @param request - details about the current page of the table requested
	 * @return a {@link TableResponse} containing the list of project subscriptions associated with a users.
	 */
	@RequestMapping("/{userId}/user/list")
	public ResponseEntity<TableResponse<UserProjectDetailsModel>> getProjectSubscriptionsForUser(
			@PathVariable("userId") Long userId, @RequestBody TableRequest request) {
		return ResponseEntity.ok(service.getProjectSubscriptionsForUser(userId, request));
	}
}
