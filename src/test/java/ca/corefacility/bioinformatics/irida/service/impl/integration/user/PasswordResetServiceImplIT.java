package ca.corefacility.bioinformatics.irida.service.impl.integration.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

import ca.corefacility.bioinformatics.irida.annotation.ServiceIntegrationTest;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

@ServiceIntegrationTest
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/user/PasswordResetServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class PasswordResetServiceImplIT {
	@Autowired
	private PasswordResetService passwordResetService;
	@Autowired
	private UserService userService;

	@Test
	@WithMockUser(username = "tester", roles = "ADMIN")
	public void testCreatePasswordReset() {
		PasswordReset pw1 = pw();
		passwordResetService.create(pw1);
		PasswordReset pw2 = passwordResetService.read(pw1.getId());
		if (pw2 == null) {
			fail("Failed to store and retrieve a PasswordReset to the database");
		}
		assertEquals(pw1.getUser(), pw2.getUser(), "User should be equal");
	}

	@Test
	@WithMockUser(username = "tester", roles = "ADMIN")
	public void testEnsureOnlyOneResetPerUser() {
		PasswordReset pw1 = passwordResetService.create(pw());
		passwordResetService.create(pw());
		assertThrows(EntityNotFoundException.class, () -> {
			passwordResetService.read(pw1.getId());
		});
	}

	@Test
	@WithMockUser(username = "tester", roles = "ADMIN")
	public void testDeletePasswordReset() {
		PasswordReset pr = passwordResetService.read("12213-123123-123123-12312");
		assertNotNull(pr);
		passwordResetService.delete("12213-123123-123123-12312");
		assertThrows(EntityNotFoundException.class, () -> {
			passwordResetService.read("12213-123123-123123-12312");
		});
	}

	@Test
	@WithMockUser(username = "tester", roles = "ADMIN")
	public void testCannotUpdateAPasswordReset() {
		PasswordReset pr = passwordResetService.read("12213-123123-123123-12312");
		Map<String, Object> change = new HashMap<>();
		User u = userService.loadUserByEmail("manager@nowhere.com");
		change.put("user_id", u.getId());
		assertThrows(UnsupportedOperationException.class, () -> {
			passwordResetService.updateFields(pr.getId(), change);
		});
	}

	private PasswordReset pw() {
		User user = userService.loadUserByEmail("manager@nowhere.com");
		return new PasswordReset(user);
	}
}
