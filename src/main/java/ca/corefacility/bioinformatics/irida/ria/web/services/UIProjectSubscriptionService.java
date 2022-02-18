package ca.corefacility.bioinformatics.irida.ria.web.services;

import ca.corefacility.bioinformatics.irida.model.subscription.ProjectSubscription;
import ca.corefacility.bioinformatics.irida.service.ProjectSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A utility class for formatting responses for the project subscriptions page UI.
 */
@Component
public class UIProjectSubscriptionService {
    private final ProjectSubscriptionService projectSubscriptionService;

    @Autowired
    public UIProjectSubscriptionService(ProjectSubscriptionService projectSubscriptionService) {
        this.projectSubscriptionService = projectSubscriptionService;
    }

    /**
     * Update an existing project subscription
     *
     * @param id        - the identifier of the {@link ProjectSubscription}
     * @param subscribe - whether to subscribe or unsubscribe the user to/from the project
     */
    public void updateProjectSubscription(Long id, boolean subscribe) {
        ProjectSubscription projectSubscription = projectSubscriptionService.read(id);
        projectSubscription.setEmailSubscription(subscribe);
        projectSubscriptionService.update(projectSubscription);
    }
}
