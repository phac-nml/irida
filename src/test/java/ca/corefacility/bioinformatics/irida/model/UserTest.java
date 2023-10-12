package ca.corefacility.bioinformatics.irida.model;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import ca.corefacility.bioinformatics.irida.model.user.Role;
import ca.corefacility.bioinformatics.irida.model.user.User;

import javax.validation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing the validation for user objects.
 * 
 */
public class UserTest {

	private static final String MESSAGES_BASENAME = "ValidationMessages";
	private Validator validator;
	private ResourceBundle b;

	@BeforeEach
	public void setUp() {
		b = ResourceBundle.getBundle(MESSAGES_BASENAME);
		Configuration<?> configuration = Validation.byDefaultProvider().configure();
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename(MESSAGES_BASENAME);
		configuration.messageInterpolator(new ResourceBundleMessageInterpolator(new PlatformResourceBundleLocator(
				MESSAGES_BASENAME)));
		ValidatorFactory factory = configuration.buildValidatorFactory();
		validator = factory.getValidator();

	}

	@Test
	public void testNullUsername() {
		User u = new User();
		u.setUsername(null);

		Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "username");

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("user.username.notnull"), constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testEmptyUsername() {
		User u = new User();
		u.setUsername("");
		Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "username");

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("user.username.size"), constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testValidUsername() {
		User u = new User();
		u.setUsername("fbristow");
		Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "username");

		assertTrue(constraintViolations.isEmpty());
	}

	@Test
	public void testShortPassword() {
		User u = new User();
		u.setPassword("Sma1!");
		Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "password");

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("user.password.size"), constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testWeakLowercasePassword() {
		User u = new User();
		u.setPassword("a11!1owercase");

		Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "password");

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("user.password.uppercase"), constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testWeakNoNumbersPassword() {
		User u = new User();
		u.setPassword("NoNumbers!");
		Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "password");

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("user.password.number"), constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testWeakPassword() {
		User u = new User();
		u.setPassword("weak");
		Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "password");
		Set<String> messages = new HashSet<>();
		messages.add(b.getString("user.password.size"));
		messages.add(b.getString("user.password.uppercase"));
		messages.add(b.getString("user.password.number"));
		messages.add(b.getString("user.password.special"));

		assertEquals(4, constraintViolations.size());
		for (ConstraintViolation<User> violation : constraintViolations) {
			assertTrue(messages.contains(violation.getMessage()));
			messages.remove(violation.getMessage());
		}
		assertTrue(messages.isEmpty());
	}

	@Test
	public void testNullEmail() {
		User u = new User();
		u.setEmail(null);
		Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "email");

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("user.email.notnull"), constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testShortEmail() {
		User u = new User();
		u.setEmail("s@s"); // technically valid, too short
		Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "email");

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("user.email.size"), constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testInvalidEmail() {
		User u = new User();
		u.setEmail("a stunningly incorrect e-mail address.");
		Set<ConstraintViolation<User>> constraintViolations = validator.validateProperty(u, "email");

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("user.email.invalid"), constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testNullUserType() {
		User u = new User();
		assertThrows(NullPointerException.class, () -> {
			u.setUserType(null);
		});
	}

	@Test
	public void testValidUser() {
		User u = new User();
		u.setUsername("fbristow");
		u.setEmail("franklin.bristow+plusSymbolsAREValid@phac-aspc.gc.ca");
		u.setPassword("SuperVa1idP4ssw0rd!");
		u.setFirstName("Franklin");
		u.setLastName("Bristow");
		u.setPhoneNumber("7029");
		u.setSystemRole(Role.ROLE_USER);

		Set<ConstraintViolation<User>> constraintViolations = validator.validate(u);

		assertTrue(constraintViolations.isEmpty(), "user is not valid, but must be valid.");
	}

	@Test
	public void testPasswordNoLowerCase() {
		User u = new User();
		u.setUsername("fbristow");
		u.setPassword("NOLOWERCASES12!");
		u.setEmail("fbristow@example.com");
		u.setFirstName("Franklin");
		u.setLastName("Bristow");
		u.setPhoneNumber("7029");
		u.setSystemRole(Role.ROLE_USER);

		Set<ConstraintViolation<User>> constraintViolations = validator.validate(u);
		assertEquals(1, constraintViolations.size(), "wrong number of constraint violations.");
		ConstraintViolation<User> passwordViolation = constraintViolations.iterator().next();
		assertTrue(passwordViolation.getPropertyPath().toString().endsWith("password"),
				"constraint violation is not on password");
	}

	@Test
	public void testCompareTo() throws ParseException {
		// should be able to sort users in ascending order of their modified
		// date
		List<User> users = new ArrayList<>();

		User u1 = new User();
		User u2 = new User();
		User u3 = new User();

		DateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

		u2.setModifiedDate(sf.parse("2011-01-01"));
		u1.setModifiedDate(sf.parse("2012-01-01"));
		u3.setModifiedDate(sf.parse("2013-01-01"));

		// users are in the wrong order
		users.add(u3);
		users.add(u1);
		users.add(u2);

		Collections.sort(users);

		User curr = users.get(0);
		for (int i = 1; i < users.size(); i++) {
			assertTrue(curr.compareTo(users.get(i)) < 0);
		}
	}

	@Test
	public void testEquals() {
		// we want the first user to be created at a different time than the
		// second user. on fast enough computers, if you just let the
		// constructor set the creation date, the creation date might be the
		// same.
		Date created = new Date(1);
		User u1 = new User("username", "email", "password", "firstName", "lastName", "phoneNumber");
		u1.setModifiedDate(created);
		User u2 = new User("username", "email", "password", "firstName", "lastName", "phoneNumber");
		// the two users DO NOT share the same created date, and should
		// therefore be different
		assertFalse(u1.equals(u2), "users should not be equal.");

		u2.setModifiedDate(created);
		u2.setId(u1.getId());
		// now the two users share the same identifier, and should therefore be
		// the same
		assertTrue(u1.equals(u2), "users should be equal.");
	}

	@Test
	public void testEqualsFields() {
		User u1 = new User("username", "email", "password", "firstName", "lastName", "phoneNumber");
		u1.setId(1111L);

		User u2 = new User("username", "email", "password", "firstName", "notequal", "phoneNumber");
		u2.setId(u1.getId());

		assertFalse(u1.equals(u2));
	}
}
