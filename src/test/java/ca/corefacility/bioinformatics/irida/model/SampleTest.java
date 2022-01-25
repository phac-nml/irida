package ca.corefacility.bioinformatics.irida.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.validators.annotations.ValidProjectName;
import ca.corefacility.bioinformatics.irida.validators.annotations.ValidSampleName;

public class SampleTest {
	private static final String MESSAGES_BASENAME = "ValidationMessages";
	private Validator validator;

	@BeforeEach
	public void setUp() {
		Configuration<?> configuration = Validation.byDefaultProvider()
				.configure();
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename(MESSAGES_BASENAME);
		configuration.messageInterpolator(
				new ResourceBundleMessageInterpolator(new PlatformResourceBundleLocator(MESSAGES_BASENAME)));
		ValidatorFactory factory = configuration.buildValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	public void testNullSampleName() {
		Sample s = new Sample();
		s.setSampleName(null);

		Set<ConstraintViolation<Sample>> violations = validator.validate(s);

		assertEquals(2, violations.size(), "Wrong number of violations.");
	}

	@Test
	public void testEmptySampleName() {
		Sample s = new Sample();
		s.setSampleName("");

		Set<ConstraintViolation<Sample>> violations = validator.validate(s);
		assertEquals(1, violations.size(), "Wrong number of violations.");
	}

	@Test
	public void testInvalidSampleName() {
		Sample s = new Sample();
		s.setSampleName("This name has a single quote ' and spaces and a period.");

		Set<ConstraintViolation<Sample>> violations = validator.validate(s);
		assertEquals(3, violations.size(), "Wrong number of violations.");
	}

	@Test
	public void testBlocklistedCharactersInSampleName() {
		testBlocklists(ValidProjectName.ValidProjectNameBlocklist.BLOCKLIST);
		testBlocklists(ValidSampleName.ValidSampleNameBlocklist.BLOCKLIST);
	}

	private void testBlocklists(char[] blocklist) {
		for (char c : blocklist) {
			Sample s = new Sample();
			s.setSampleName("ATLEAST3" + c);
			Set<ConstraintViolation<Sample>> violations = validator.validate(s);
			assertEquals(1, violations.size(), "Wrong number of violations.");
		}
	}
}
