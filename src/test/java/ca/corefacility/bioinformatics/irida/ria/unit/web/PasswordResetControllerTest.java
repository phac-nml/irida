package ca.corefacility.bioinformatics.irida.ria.unit.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.web.PasswordResetController;
import ca.corefacility.bioinformatics.irida.service.EmailController;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.*;

/**
 *
 */
public class PasswordResetControllerTest {

	private PasswordResetService passwordResetService;

	private PasswordResetController controller;

	@BeforeEach
	public void setUp() {
		passwordResetService = mock(PasswordResetService.class);


		controller = new PasswordResetController(passwordResetService);
	}

	@AfterEach
	public void cleanup() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void testGetResetPage() {
		User user = new User(1L, "tom", null, null, null, null, null);
		PasswordReset passwordReset = new PasswordReset(user);
		String resetId = passwordReset.getId();
		ExtendedModelMap model = new ExtendedModelMap();

		when(passwordResetService.read(resetId)).thenReturn(passwordReset);

		String resetPage = controller.getResetPage(resetId, false, model);
		assertEquals(PasswordResetController.PASSWORD_RESET_PAGE, resetPage);
		assertTrue(model.containsKey("errors"));
		assertTrue(model.containsKey("passwordReset"));
		assertTrue(model.containsKey("user"));

		verify(passwordResetService).read(resetId);
	}
}


