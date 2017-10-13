package ca.corefacility.bioinformatics.irida.repositories.user;

import ca.corefacility.bioinformatics.irida.model.user.User;

import java.util.Date;

public interface UserRepositoryCustom {

	public void updateLogin(User user, Date date);
}
