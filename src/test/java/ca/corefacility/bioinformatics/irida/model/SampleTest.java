package ca.corefacility.bioinformatics.irida.model;

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

import ca.corefacility.bioinformatics.irida.validators.annotations.ValidProjectName;
import ca.corefacility.bioinformatics.irida.validators.annotations.ValidSampleName;

public class SampleTest {
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
	public void testInvalidSampleName() {
		Sample s = new Sample();
		s.setSampleName("This name has a single quote ' and spaces and a period.");
		s.setExternalSampleId("external");

		Set<ConstraintViolation<Sample>> violations = validator.validate(s);
		assertEquals("Wrong number of violations.", 3, violations.size());
	}

	@Test
	public void testBlacklistedCharactersInSampleName() {
		testBlacklists(ValidProjectName.BLACKLISTED_CHARACTERS);
		testBlacklists(ValidSampleName.BLACKLISTED_CHARACTERS);
	}

	private void testBlacklists(char[] blacklist) {
		for (char c : blacklist) {
			Sample s = new Sample();
			s.setSampleName("ATLEAST3" + c);
			s.setExternalSampleId("ATLEAST3" + c);
			Set<ConstraintViolation<Sample>> violations = validator.validate(s);
			assertEquals("Wrong number of violations.", 2, violations.size());
		}
	}
}
