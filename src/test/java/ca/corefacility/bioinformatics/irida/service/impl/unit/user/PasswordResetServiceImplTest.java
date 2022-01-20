package ca.corefacility.bioinformatics.irida.service.impl.unit.user;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Map;

import javax.validation.Validator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import ca.corefacility.bioinformatics.irida.model.user.PasswordReset;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.user.PasswordResetRepository;
import ca.corefacility.bioinformatics.irida.service.impl.user.PasswordResetServiceImpl;
import ca.corefacility.bioinformatics.irida.service.user.PasswordResetService;

import com.google.common.collect.ImmutableMap;

/**
 */
public class PasswordResetServiceImplTest {
	private PasswordResetService passwordResetService;
	private PasswordResetRepository passwordResetRepository;
	private Validator validator;

	@BeforeEach
	public void setUp() {
		validator = mock(Validator.class);
		passwordResetRepository = mock(PasswordResetRepository.class);
		passwordResetService = new PasswordResetServiceImpl(passwordResetRepository, validator);
	}

	@AfterEach
	public void tearDown() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	@Test
	public void testCannotUpdateAPasswordReset() {
		Map<String, Object> properties = ImmutableMap.of("user_id", (Object) "3");
		assertThrows(UnsupportedOperationException.class, () -> {
			passwordResetService.updateFields("1121-1212-1d2d1-df433", properties);
		});
	}

	@Test
	public void testCreatePasswordReset() {
		User user = new User();
		PasswordReset passwordReset = new PasswordReset(user);

		when(passwordResetRepository.findByUser(user)).thenReturn(null);
		when(passwordResetRepository.save(passwordReset)).thenReturn(passwordReset);
		passwordResetService.create(passwordReset);
		verify(passwordResetRepository).save(passwordReset);
	}
}
