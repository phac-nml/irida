package ca.corefacility.bioinformatics.irida.model.upload.galaxy;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.util.LinkedList;
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

public class GalaxySampleTest {
	
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
	
	@Test(expected=NullPointerException.class)
	public void testNullName() {
		new GalaxySample(null, new LinkedList<Path>());
	}
	
	@Test
	public void testShortName() {
		GalaxySample sample = new GalaxySample(new GalaxyObjectName("a"), new LinkedList<Path>());
		
		Set<ConstraintViolation<GalaxySample>> constraintViolations
			= validator.validate(sample);

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("galaxy.object.size"), constraintViolations.iterator().next().getMessage());
	}
	
	@Test
	public void testValidName() {
		GalaxySample sample = new GalaxySample(new GalaxyObjectName("Abc123 _-.\"'"), new LinkedList<Path>());
		
		Set<ConstraintViolation<GalaxySample>> constraintViolations
			= validator.validate(sample);

		assertEquals(0, constraintViolations.size());
	}
	
	@Test
	public void testInvalidName() {
		GalaxySample sample = new GalaxySample(new GalaxyObjectName("Abc123 _-.\"'<"), new LinkedList<Path>());
		
		Set<ConstraintViolation<GalaxySample>> constraintViolations
			= validator.validate(sample);

		assertEquals(1, constraintViolations.size());
		assertEquals(b.getString("galaxy.object.invalid"), constraintViolations.iterator().next().getMessage());
	}
}
