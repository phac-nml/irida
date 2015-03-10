package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
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
 * Tests for {@link GalaxySample}.
 *
 */
public class GalaxySampleTest {
	
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
	
	@Test(expected=NullPointerException.class)
	public void testNullName() {
		new GalaxySample(null, new LinkedList<Path>());
	}
	
	@Test
	public void testEmptyName() {
		GalaxySample sample = new GalaxySample(new GalaxyFolderName(""), new LinkedList<Path>());
		
		Set<ConstraintViolation<GalaxySample>> constraintViolations
			= validator.validate(sample);

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("galaxy.object.size"), constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void testShortName() {
		GalaxySample sample = new GalaxySample(new GalaxyFolderName("a"), new LinkedList<Path>());
		
		Set<ConstraintViolation<GalaxySample>> constraintViolations
			= validator.validate(sample);

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("galaxy.object.size"), constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void testValidName() {
		GalaxySample sample = new GalaxySample(new GalaxyFolderName("Abc123_-"), new LinkedList<Path>());
		
		Set<ConstraintViolation<GalaxySample>> constraintViolations
			= validator.validate(sample);

		assertEquals(0, constraintViolations.size());
	}
	
	@Test
	public void testBlacklistedCharactersInGalaxySample() {
		testBlacklists(ValidProjectName.ValidProjectNameBlacklist.BLACKLIST);
		testBlacklists(ValidSampleName.ValidSampleNameBlacklist.BLACKLIST);
	}

	private void testBlacklists(char[] blacklist) {
		List<Path> sampleFiles = new LinkedList<Path>();
		
		for (char c : blacklist) {
			GalaxySample sample = new GalaxySample(new GalaxyFolderName("Abc123_-" + c), sampleFiles);
			Set<ConstraintViolation<GalaxySample>> violations = validator.validate(sample);
			assertEquals("Wrong number of violations.", 1, violations.size());
		}
	}
}
