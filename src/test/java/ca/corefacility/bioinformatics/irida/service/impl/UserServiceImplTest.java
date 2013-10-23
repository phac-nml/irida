package ca.corefacility.bioinformatics.irida.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.service.UserService;

import com.google.common.collect.ImmutableMap;

/**
 * Testing the behavior of {@link UserServiceImpl}
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class UserServiceImplTest {

	private UserService userService;
	private UserRepository userRepository;
	private Validator validator;
	private PasswordEncoder passwordEncoder;

	@Before
	public void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		userRepository = mock(UserRepository.class);
		passwordEncoder = mock(PasswordEncoder.class);
		userService = new UserServiceImpl(userRepository, passwordEncoder, validator);
	}

	@Test(expected = EntityNotFoundException.class)
	// should throw the exception to the caller instead of swallowing it.
	public void testBadUsername() {
		String username = "superwrongusername";
		when(userRepository.loadUserByUsername(username)).thenThrow(new EntityNotFoundException("not found"));
		userService.getUserByUsername(username);
	}

	@Test
	public void testBadPasswordCreate() {
		// a user should not be persisted with a bad password (like password1)
		String username = "fbristow";
		String password = "password1";
		String passwordEncoded = "ENCODED_password1";
		String email = "fbristow@gmail.com";
		String firstName = "Franklin";
		String lastName = "Bristow";
		String phoneNumber = "7029";
		User user = new User(username, email, password, firstName, lastName, phoneNumber);
		when(passwordEncoder.encode(password)).thenReturn(passwordEncoded);
		try {
			userService.create(user);
			fail();
		} catch (ConstraintViolationException e) {
			Set<ConstraintViolation<?>> violationSet = e.getConstraintViolations();
			assertEquals(1, violationSet.size());
			ConstraintViolation<?> violation = violationSet.iterator().next();
			assertTrue(violation.getPropertyPath().toString().contains("password"));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testBadPasswordUpdate() {
		// a user should not be persisted with a bad password (like password1)
		String password = "password1";
		String passwordEncoded = "$2a$10$vMzhJFdyM72NnnWIoMSbUecHRxZDtCE1fdiPfjfjT1WD0fISDXOX2";
		Long luid = 1111l;
		Map<String, Object> properties = new HashMap<>();
		properties.put("password", password);

		when(passwordEncoder.encode(password)).thenReturn(passwordEncoded);
		try {
			userService.update(luid, properties);
			fail();
		} catch (ConstraintViolationException e) {
			Set<ConstraintViolation<?>> violationSet = e.getConstraintViolations();
			assertEquals(1, violationSet.size());
			ConstraintViolation<?> violation = violationSet.iterator().next();
			assertTrue(violation.getPropertyPath().toString().contains("password"));
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testPasswordUpdate() {
		final String password = "Password1";
		final String encodedPassword = "ENCODED_" + password;
		final User persisted = user();
		final Long id = persisted.getId();

		Map<String, Object> properties = new HashMap<>();
		properties.put("password", (Object) password);
		//Map<String, Object> encodedPasswordProperties = ImmutableMap.of("password", (Object) encodedPassword);

		when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
		when(userRepository.save(persisted)).thenReturn(persisted);
		when(userRepository.findOne(id)).thenReturn(persisted);
		when(userRepository.exists(id)).thenReturn(true);

		User u = userService.update(id, properties);
		assertEquals("User-type was not returned.", persisted, u);

		verify(passwordEncoder).encode(password);
		verify(userRepository).findOne(id);
		verify(userRepository).save(persisted);
		verify(userRepository).exists(id);
	}

	@Test
	public void updateNoPassword() {
		Map<String, Object> properties = ImmutableMap.of("username", (Object) "updated");

		when(userRepository.exists(1l)).thenReturn(true);
		when(userRepository.findOne(1l)).thenReturn(user());
		userService.update(1l, properties);
		verifyZeroInteractions(passwordEncoder);
	}

	@Test(expected = ConstraintViolationException.class)
	public void testCreateBadPassword() {
		User u = user();
		u.setPassword("not a good password");

		userService.create(u);
	}

	@Test
	public void testCreateGoodPassword() {
		final User u = user();
		final String password = u.getPassword();
		final String encodedPassword = "ENCODED_" + password;

		when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
		when(userRepository.save(u)).thenReturn(u);

		userService.create(u);
		assertEquals("User password was not encoded.", encodedPassword, u.getPassword());

		verify(passwordEncoder).encode(password);
		verify(userRepository).save(u);
	}

	@Test
	public void testLoadUserByUsername() {
		User user = user();
		user.setSystemRole(new Role("ROLE_USER"));
		String username = user.getUsername();
		String password = user.getPassword();

		when(userRepository.loadUserByUsername(username)).thenReturn(user);

		UserDetails userDetails = userService.loadUserByUsername(username);

		assertEquals(username, userDetails.getUsername());
		assertEquals(password, userDetails.getPassword());
	}

	@Test
	public void testUpdatePasswordGoodPassword() {
		String password = "Password1";
		String encodedPassword = password + "_ENCODED";

		when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
		when(userRepository.exists(1l)).thenReturn(true);
		when(userRepository.findOne(1l)).thenReturn(user());

		userService.changePassword(1l, password);

		ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(argument.capture());
		User saved = argument.getValue();
		assertEquals("password field was not encoded.", encodedPassword, saved.getPassword());
	}

	@Test
	public void testUpdatePasswordBadPassword() {
		String password = "arguablynotagoodpassword";

		try {
			userService.changePassword(1l, password);
			fail();
		} catch (ConstraintViolationException e) {
		} catch (Exception e) {
			fail();
		}

		verifyZeroInteractions(userRepository, passwordEncoder);
	}

	private User user() {
		String username = "fbristow";
		String password = "Password1";
		String email = "fbristow@gmail.com";
		String firstName = "Franklin";
		String lastName = "Bristow";
		String phoneNumber = "7029";
		return new User(username, email, password, firstName, lastName, phoneNumber);
	}
}
