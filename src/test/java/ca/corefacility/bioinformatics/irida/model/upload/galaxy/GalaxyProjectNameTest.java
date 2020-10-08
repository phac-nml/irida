package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import static org.junit.Assert.assertEquals;

import java.util.ResourceBundle;
import java.util.Set;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import ca.corefacility.bioinformatics.irida.validators.annotations.ValidProjectName;

/**
 * Tests for {@link GalaxyProjectName}.
 *
 */
public class GalaxyProjectNameTest {

	private static final String MESSAGES_BASENAME = "ValidationMessages";
	private Validator validator;
	private ResourceBundle b;

	@Before
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
		GalaxyProjectName name = new GalaxyProjectName(null);

		Set<ConstraintViolation<GalaxyProjectName>> constraintViolations
			= validator.validate(name);

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("galaxy.object.notnull"), constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testEmptyName() {
		GalaxyProjectName name = new GalaxyProjectName("");

		Set<ConstraintViolation<GalaxyProjectName>> constraintViolations
			= validator.validate(name);

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("galaxy.object.size"), constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testShortName() {
		GalaxyProjectName name = new GalaxyProjectName("a");

		Set<ConstraintViolation<GalaxyProjectName>> constraintViolations
			= validator.validate(name);

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("galaxy.object.size"), constraintViolations.iterator().next().getMessage());
	}

	@Test
	public void testValidName() {
		GalaxyProjectName name = new GalaxyProjectName("Abc123 _-.'");

		Set<ConstraintViolation<GalaxyProjectName>> constraintViolations
			= validator.validate(name);

		assertEquals(0, constraintViolations.size());
	}

	@Test
	public void testBlocklistedCharactersInGalaxyProjectName() {
		testBlocklists(ValidProjectName.ValidProjectNameBlocklist.BLOCKLIST);
	}

	private void testBlocklists(char[] blocklist) {
		for (char c : blocklist) {
			GalaxyProjectName p = new GalaxyProjectName("Abc123 _-.'" + c);
			Set<ConstraintViolation<GalaxyProjectName>> violations = validator.validate(p);
			assertEquals("Wrong number of violations.", 1, violations.size());
		}
	}
}
