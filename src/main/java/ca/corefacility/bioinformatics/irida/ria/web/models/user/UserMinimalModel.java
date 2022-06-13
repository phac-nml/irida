package ca.corefacility.bioinformatics.irida.ria.web.models.user;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.MinimalModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.ModelKeys;

/**
 * Minimal model for a {@link User} in the UI.
 */
public class UserMinimalModel extends MinimalModel {
	public UserMinimalModel(User user) {
		super(user.getId(), user.getFirstName() + " " + user.getLastName(), ModelKeys.User.label);
	}
}
