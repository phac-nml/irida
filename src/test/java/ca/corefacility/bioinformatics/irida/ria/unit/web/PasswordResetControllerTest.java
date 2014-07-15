package ca.corefacility.bioinformatics.irida.ria.unit.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.PasswordResetController;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

/**
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class PasswordResetControllerTest {
	private UserService userService;
	private PasswordResetService passwordResetService;
	private MessageSource messageSource;
	private PasswordResetController controller;

	@Before
	public void setUp() {
		userService = mock(UserService.class);
		passwordResetService = mock(PasswordResetService.class);
		messageSource = mock(MessageSource.class);

		controller = new PasswordResetController(userService, passwordResetService, messageSource);
	}

	@Test
	public void testGetResetPage() {
		User user = new User(1l, "tom", null, null, null, null, null);
		PasswordReset passwordReset = new PasswordReset(user);
		String resetId = passwordReset.getId();
		ExtendedModelMap model = new ExtendedModelMap();

		when(passwordResetService.read(resetId)).thenReturn(passwordReset);

		String resetPage = controller.getResetPage(resetId, model);
		assertEquals("user/password_reset", resetPage);
		assertTrue(model.containsKey("errors"));
		assertTrue(model.containsKey("passwordReset"));
		assertTrue(model.containsKey("user"));

		verify(passwordResetService).read(resetId);
	}

	@Test
	public void testSubmitPasswordReset() {
		User user = new User(1l, "tom", null, null, null, null, null);
		PasswordReset passwordReset = new PasswordReset(user);
		String resetId = passwordReset.getId();
		String password = "Password1";
		ExtendedModelMap model = new ExtendedModelMap();

		when(passwordResetService.read(resetId)).thenReturn(passwordReset);

		String sendNewPassword = controller.sendNewPassword(resetId, password, password, model,
				LocaleContextHolder.getLocale());

		assertEquals("user/password_reset_success", sendNewPassword);
		assertTrue(model.containsKey("user"));
		assertEquals("User should not be logged in after resetting password", null, SecurityContextHolder.getContext()
				.getAuthentication());

		verify(passwordResetService).read(resetId);
		verify(userService).changePassword(user.getId(), password);
		verify(passwordResetService).delete(resetId);
	}

	@Test
	public void testSubmitPasswordNoMatch() {
		User user = new User(1l, "tom", null, null, null, null, null);
		PasswordReset passwordReset = new PasswordReset(user);
		String resetId = passwordReset.getId();
		String password = "Password1";
		ExtendedModelMap model = new ExtendedModelMap();

		when(passwordResetService.read(resetId)).thenReturn(passwordReset);

		String sendNewPassword = controller.sendNewPassword(resetId, password, "not the same", model,
				LocaleContextHolder.getLocale());

		assertEquals("user/password_reset", sendNewPassword);
		assertTrue(model.containsKey("errors"));

		verify(passwordResetService, times(2)).read(resetId);
	}

	@Test
	public void testSubmitPasswordViolation() {
		User user = new User(1l, "tom", null, null, null, null, null);
		PasswordReset passwordReset = new PasswordReset(user);
		String resetId = passwordReset.getId();
		String password = "Password1";
		ExtendedModelMap model = new ExtendedModelMap();

		ConstraintViolationException constraintViolationException = new ConstraintViolationException("bad password",
				null);

		when(passwordResetService.read(resetId)).thenReturn(passwordReset);
		when(userService.changePassword(user.getId(), password)).thenThrow(constraintViolationException);

		String sendNewPassword = controller.sendNewPassword(resetId, password, password, model,
				LocaleContextHolder.getLocale());

		assertEquals("user/password_reset", sendNewPassword);
		assertTrue(model.containsKey("errors"));

		verify(passwordResetService, times(2)).read(resetId);
	}
}
