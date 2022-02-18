package ca.corefacility.bioinformatics.irida.ria.web.projects;

import ca.corefacility.bioinformatics.irida.ria.web.services.UIProjectSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * @return Map message if the {@link ProjectSubscription} was updated successfully
     */
    @RequestMapping(value = "/{id}/update", method = RequestMethod.POST)
    public void updateProjectSubscription(@PathVariable Long id, @RequestParam boolean subscribe) {
        service.updateProjectSubscription(id, subscribe);
    }
}
