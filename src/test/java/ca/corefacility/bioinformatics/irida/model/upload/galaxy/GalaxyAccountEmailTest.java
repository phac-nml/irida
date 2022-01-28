package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * Tests for {@link GalaxyAccountEmail}.
 *
 */
public class GalaxyAccountEmailTest {
	
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
	public void testNullName() {
		GalaxyAccountEmail email = new GalaxyAccountEmail(null);
		
		Set<ConstraintViolation<GalaxyAccountEmail>> constraintViolations
			= validator.validate(email);

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("galaxy.user.email.notnull"), constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void testEmptyName() {
		GalaxyAccountEmail email = new GalaxyAccountEmail("");
		
		Set<ConstraintViolation<GalaxyAccountEmail>> constraintViolations
			= validator.validate(email);

		assertEquals(1, constraintViolations.size());
	}
	
	@Test
	public void testShortName() {
		GalaxyAccountEmail email = new GalaxyAccountEmail("a@b.");
		
		Set<ConstraintViolation<GalaxyAccountEmail>> constraintViolations
			= validator.validate(email);

		assertEquals(2, constraintViolations.size());
	}
	
	@Test
	public void testValidName() {
		GalaxyAccountEmail email = new GalaxyAccountEmail("a.+@B_c.com");
		
		Set<ConstraintViolation<GalaxyAccountEmail>> constraintViolations
			= validator.validate(email);

		assertEquals(0, constraintViolations.size());
	}
	
	@Test
	public void testInvalidName() {
		GalaxyAccountEmail email = new GalaxyAccountEmail("a.+B_c.com");
		
		Set<ConstraintViolation<GalaxyAccountEmail>> constraintViolations
			= validator.validate(email);

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("galaxy.user.email.invalid"), constraintViolations.iterator().next().getMessage());
	}
}
