package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.corefacility.bioinformatics.irida.model.subscription.ProjectSubscription;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxSuccessResponse;
import ca.corefacility.bioinformatics.irida.ria.web.exceptions.UIEntityNotFoundException;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectSubscriptionService;
import ca.corefacility.bioinformatics.irida.ria.web.users.dto.UserProjectDetailsModel;

/**
 * Controller for handling AJAX requests for {@link ProjectSubscription}
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
	 * @param locale    {@link Locale} of the current user
	 * @return text to display to the user about the result of updating a project subscription
	 */
	@RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
	public ResponseEntity<AjaxResponse> updateProjectSubscription(@PathVariable Long id,
			@RequestParam boolean subscribe, Locale locale) {
		try {
			return ResponseEntity.ok(new AjaxSuccessResponse(service.updateProjectSubscription(id, subscribe, locale)));
		} catch (UIEntityNotFoundException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new AjaxErrorResponse(e.getMessage()));
		}
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
