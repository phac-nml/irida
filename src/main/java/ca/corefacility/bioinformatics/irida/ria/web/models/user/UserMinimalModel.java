package ca.corefacility.bioinformatics.irida.ria.web.models.user;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.MinimalModel;

public class UserMinimalModel extends MinimalModel {
	public UserMinimalModel(User user) {
		super(user.getId(), user.getFirstName() + " " + user.getLastName());
	}
}
