package ca.corefacility.bioinformatics.irida.ria.unit.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.ria.security.IridaPostAuthenticationFailureHandler;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class IridaPostAuthenticationFailureHandlerTest {
	private IridaPostAuthenticationFailureHandler handler;
	private PasswordResetService resetService;
	private UserService userService;

	@BeforeEach
	public void setUp() {
		resetService = mock(PasswordResetService.class);
		userService = mock(UserService.class);
		handler = new IridaPostAuthenticationFailureHandler(resetService, userService);

	}

	@Test
	public void testOnAuthenticationFailure() throws IOException, ServletException {
		String username = "tom";
		User user = new User();
		PasswordReset reset = new PasswordReset(user);
		String expectedRedirect = "/password_reset/" + reset.getId() + "?expired=true";

		AuthenticationException exception = new CredentialsExpiredException("Credentials expired");

		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);

		when(request.getParameter("username")).thenReturn(username);
		when(request.getContextPath()).thenReturn("");
		when(userService.getUserByUsername(username)).thenReturn(user);
		when(resetService.create(any(PasswordReset.class))).thenReturn(reset);

		handler.onAuthenticationFailure(request, response, exception);

		verify(request).getParameter("username");
		verify(userService).getUserByUsername(username);
		verify(resetService).create(any(PasswordReset.class));

		ArgumentCaptor<String> redirectCaptor = ArgumentCaptor.forClass(String.class);
		verify(response).sendRedirect(redirectCaptor.capture());
		String redirect = redirectCaptor.getValue();
		assertEquals(expectedRedirect, redirect);
	}

	@Test
	public void testOnAuthenticationFailureWithOtherException() throws IOException, ServletException {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);

		AuthenticationException exception = new DisabledException("disabled");

		handler.onAuthenticationFailure(request, response, exception);
		verifyNoInteractions(userService);
		verifyNoInteractions(resetService);
	}
}
