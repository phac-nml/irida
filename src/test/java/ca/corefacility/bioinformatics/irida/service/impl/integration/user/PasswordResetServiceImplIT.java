package ca.corefacility.bioinformatics.irida.service.impl.integration.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiNoGalaxyTestConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiTestDataSourceConfig;
import ca.corefacility.bioinformatics.irida.config.processing.IridaApiTestMultithreadingConfig;
import ca.corefacility.bioinformatics.irida.config.services.IridaApiServicesConfig;
import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;

/**
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiServicesConfig.class,
		IridaApiNoGalaxyTestConfig.class, IridaApiTestDataSourceConfig.class, IridaApiTestMultithreadingConfig.class })
@ActiveProfiles("test")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/service/impl/user/PasswordResetServiceImplIT.xml")
@DatabaseTearDown("/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class PasswordResetServiceImplIT {
	@Autowired
	private PasswordResetService passwordResetService;
	@Autowired
	private UserService userService;

	@Before
	public void setup() {
		AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken("nobody", "nobody",
				ImmutableList.of(Role.ROLE_ANONYMOUS));
		SecurityContextHolder.getContext().setAuthentication(anonymousToken);
	}

	@Test
	public void testCreatePasswordReset() {
		PasswordReset pw1 = pw();
		passwordResetService.create(pw1);
		PasswordReset pw2 = passwordResetService.read(pw1.getId());
		if (pw2 == null) {
			fail("Failed to store and retrieve a PasswordReset to the database");
		}
		assertEquals("User should be equal", pw1.getUser(), pw2.getUser());
	}

	@Test(expected = EntityNotFoundException.class)
	public void testEnsureOnlyOneResetPerUser() {
		PasswordReset pw1 = passwordResetService.create(pw());
		passwordResetService.create(pw());
		passwordResetService.read(pw1.getId());
	}

	@Test(expected = EntityNotFoundException.class)
	public void testDeletePasswordReset() {
		PasswordReset pr = passwordResetService.read("12213-123123-123123-12312");
		assertNotNull(pr);
		passwordResetService.delete("12213-123123-123123-12312");
		passwordResetService.read("12213-123123-123123-12312");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testCannotUpdateAPasswordReset() {
		PasswordReset pr = passwordResetService.read("12213-123123-123123-12312");
		Map<String, Object> change = new HashMap<>();
		User u = userService.loadUserByEmail("manager@nowhere.com");
		change.put("user_id", u.getId());
		passwordResetService.update(pr.getId(), change);
	}

	private PasswordReset pw() {
		User user = userService.loadUserByEmail("manager@nowhere.com");
		return new PasswordReset(user);
	}
}
