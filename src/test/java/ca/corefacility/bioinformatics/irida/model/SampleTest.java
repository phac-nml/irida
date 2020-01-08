package ca.corefacility.bioinformatics.irida.model;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ca.corefacility.bioinformatics.irida.model.sample.MetadataTemplateField;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.MetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sample.metadata.PipelineProvidedMetadataEntry;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.validators.annotations.ValidProjectName;
import ca.corefacility.bioinformatics.irida.validators.annotations.ValidSampleName;

public class SampleTest {
	private static final String MESSAGES_BASENAME = "ValidationMessages";
	private Validator validator;

	@Before
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

		assertEquals("Wrong number of violations.", 2, violations.size());
	}

	@Test
	public void testEmptySampleName() {
		Sample s = new Sample();
		s.setSampleName("");

		Set<ConstraintViolation<Sample>> violations = validator.validate(s);
		assertEquals("Wrong number of violations.", 1, violations.size());
	}

	@Test
	public void testInvalidSampleName() {
		Sample s = new Sample();
		s.setSampleName("This name has a single quote ' and spaces and a period.");

		Set<ConstraintViolation<Sample>> violations = validator.validate(s);
		assertEquals("Wrong number of violations.", 3, violations.size());
	}

	@Test
	public void testBlacklistedCharactersInSampleName() {
		testBlacklists(ValidProjectName.ValidProjectNameBlacklist.BLACKLIST);
		testBlacklists(ValidSampleName.ValidSampleNameBlacklist.BLACKLIST);
	}

	private void testBlacklists(char[] blacklist) {
		for (char c : blacklist) {
			Sample s = new Sample();
			s.setSampleName("ATLEAST3" + c);
			Set<ConstraintViolation<Sample>> violations = validator.validate(s);
			assertEquals("Wrong number of violations.", 1, violations.size());
		}
	}
}
