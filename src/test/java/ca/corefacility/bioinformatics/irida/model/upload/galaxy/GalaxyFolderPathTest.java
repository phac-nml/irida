package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import static org.junit.Assert.assertEquals;

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

/**
 * Tests for {@link GalaxyFolderPath}.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyFolderPathTest {
	
	private static final String MESSAGES_BASENAME = "ca.corefacility.bioinformatics.irida.validation.ValidationMessages";
	private Validator validator;

	@Before
	public void setUp() {
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
		GalaxyFolderPath name = new GalaxyFolderPath(null);
		
		Set<ConstraintViolation<GalaxyFolderPath>> constraintViolations
			= validator.validate(name);

		assertEquals(1, constraintViolations.size());
	}
	
	@Test
	public void testShortName() {
		GalaxyFolderPath name = new GalaxyFolderPath("a");
		
		Set<ConstraintViolation<GalaxyFolderPath>> constraintViolations
			= validator.validate(name);

		assertEquals(1, constraintViolations.size());
	}
	
	@Test
	public void testValidName() {
		GalaxyFolderPath name = new GalaxyFolderPath("Abc123_-/");
		
		Set<ConstraintViolation<GalaxyFolderPath>> constraintViolations
			= validator.validate(name);

		assertEquals(0, constraintViolations.size());
	}
	
	@Test
	public void testInvalidName() {
		GalaxyFolderPath name = new GalaxyFolderPath("Abc123_-/<");
		
		Set<ConstraintViolation<GalaxyFolderPath>> constraintViolations
			= validator.validate(name);

		assertEquals(1, constraintViolations.size());
	}
}
