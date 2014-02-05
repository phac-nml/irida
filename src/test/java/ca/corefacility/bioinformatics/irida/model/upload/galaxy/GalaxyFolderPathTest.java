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

public class GalaxyFolderPathTest {
	
	private static final String MESSAGES_BASENAME = "ca.corefacility.bioinformatics.irida.validation.ValidationMessages";
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
		GalaxyFolderPath name = new GalaxyFolderPath(null);
		
		Set<ConstraintViolation<GalaxyFolderPath>> constraintViolations
			= validator.validate(name);

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("galaxy.path.notnull"), constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void testShortName() {
		GalaxyFolderPath name = new GalaxyFolderPath("a");
		
		Set<ConstraintViolation<GalaxyFolderPath>> constraintViolations
			= validator.validate(name);

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("galaxy.path.size"), constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void testValidName() {
		GalaxyFolderPath name = new GalaxyFolderPath("Abc123 _-.\"'/");
		
		Set<ConstraintViolation<GalaxyFolderPath>> constraintViolations
			= validator.validate(name);

		assertEquals(0, constraintViolations.size());
	}
	
	@Test
	public void testInvalidName() {
		GalaxyFolderPath name = new GalaxyFolderPath("Abc123 _-.\"'\\<");
		
		Set<ConstraintViolation<GalaxyFolderPath>> constraintViolations
			= validator.validate(name);

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("galaxy.path.invalid"), constraintViolations.iterator().next().getMessage());
	}
}
