package ca.corefacility.bioinformatics.irida.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.Role;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.service.UserService;

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

	@Test
	// should throw the exception to the caller instead of swallowing it.
	public void testBadUsername() {
		String username = "superwrongusername";
		when(userRepository.getUserByUsername(username)).thenThrow(new EntityNotFoundException("not found"));
		try {
			userService.getUserByUsername(username);
			fail();
		} catch (EntityNotFoundException e) {
		} catch (Throwable e) {
			fail();
		}
	}

	@Test
	public void testBadPasswordCreate() {
		// a user should not be persisted with a bad password (like password1)
		String username = "fbristow";
		String password = "password1";
		String passwordEncoded = "$2a$10$vMzhJFdyM72NnnWIoMSbUecHRxZDtCE1fdiPfjfjT1WD0fISDXOX2";
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
		Long luid = new Long(1111);
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
	public void testLoadUserByUsername() {
		User user = user();
		user.setSystemRole(new Role("ROLE_USER"));
		String username = user.getUsername();
		String password = user.getPassword();

		when(userRepository.getUserByUsername(username)).thenReturn(user);

		UserDetails userDetails = userService.loadUserByUsername(username);

		assertEquals(username, userDetails.getUsername());
		assertEquals(password, userDetails.getPassword());
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
