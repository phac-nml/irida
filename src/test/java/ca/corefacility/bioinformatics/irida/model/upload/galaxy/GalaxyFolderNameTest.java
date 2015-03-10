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
import ca.corefacility.bioinformatics.irida.validators.annotations.ValidSampleName;

/**
 * Tests for {@link GalaxyProjectName}.
 *
 */
public class GalaxyFolderNameTest {
	
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
		GalaxyFolderName name = new GalaxyFolderName(null);
		
		Set<ConstraintViolation<GalaxyFolderName>> constraintViolations
			= validator.validate(name);

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("galaxy.object.notnull"), constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void testShortName() {
		GalaxyFolderName name = new GalaxyFolderName("a");
		
		Set<ConstraintViolation<GalaxyFolderName>> constraintViolations
			= validator.validate(name);

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("galaxy.object.size"), constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void testEmptyName() {
		GalaxyFolderName name = new GalaxyFolderName("");
		
		Set<ConstraintViolation<GalaxyFolderName>> constraintViolations
			= validator.validate(name);

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("galaxy.object.size"), constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void testValidName() {
		GalaxyFolderName name = new GalaxyFolderName("Abc123_-");
		
		Set<ConstraintViolation<GalaxyFolderName>> constraintViolations
			= validator.validate(name);

		assertEquals(0, constraintViolations.size());
	}
	
	@Test
	public void testBlacklistedCharactersInName() {
		testBlacklists(ValidProjectName.ValidProjectNameBlacklist.BLACKLIST);
		testBlacklists(ValidSampleName.ValidSampleNameBlacklist.BLACKLIST);
	}

	private void testBlacklists(char[] blacklist) {
		for (char c : blacklist) {
			GalaxyFolderName s = new GalaxyFolderName("Abc123_-" + c);
			Set<ConstraintViolation<GalaxyFolderName>> violations = validator.validate(s);
			assertEquals("Wrong number of violations.", 1, violations.size());
		}
	}
}
