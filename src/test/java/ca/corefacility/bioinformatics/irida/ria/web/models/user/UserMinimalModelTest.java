package ca.corefacility.bioinformatics.irida.ria.web.models.user;

import org.junit.jupiter.api.Test;

import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.models.MinimalModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.ModelKeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserMinimalModelTest {
	@Test
	public void testUserMinimalModel() {
		Long id = 1L;
		String firstName = "firstName";
		String lastName = "lastName";
		String fullName = firstName + " " + lastName;

		User user = mock(User.class);
		when(user.getId()).thenReturn(id);
		when(user.getFirstName()).thenReturn(firstName);
		when(user.getLastName()).thenReturn(lastName);

		UserMinimalModel model = new UserMinimalModel(user);
		assertThat(model).isInstanceOf(MinimalModel.class);
		assertEquals(id, model.getId(), "Id should not be changed");
		assertEquals(ModelKeys.User.label + id, model.getKey(), "Key should be concatenated with id");
		assertEquals(fullName, model.getName(), "Model should have the full user name");
	}
}