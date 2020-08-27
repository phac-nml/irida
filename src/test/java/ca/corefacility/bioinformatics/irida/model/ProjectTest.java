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

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.validators.annotations.ValidProjectName;

public class ProjectTest {
	private static final String MESSAGES_BASENAME = "ValidationMessages";
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
	public void testNullProjectName() {
		Project p = new Project();
		p.setName(null);

		Set<ConstraintViolation<Project>> violations = validator.validate(p);
		assertEquals("Wrong number of violations.", 2, violations.size());
	}

	@Test
	public void testEmptyProjectName() {
		Project p = new Project();
		p.setName("");

		Set<ConstraintViolation<Project>> violations = validator.validate(p);
		assertEquals("Wrong number of violations.", 1, violations.size());
	}

	@Test
	public void testInvalidSampleName() {
		Project p = new Project();
		p.setName("This name has a single quote ' and spaces and a period.");

		Set<ConstraintViolation<Project>> violations = validator.validate(p);
		assertEquals("Wrong number of violations.", 0, violations.size());
	}

	@Test
	public void testBlocklistedCharactersInSampleName() {
		testBlocklists(ValidProjectName.ValidProjectNameBlocklist.BLOCKLIST);
	}

	private void testBlocklists(char[] blocklist) {
		for (char c : blocklist) {
			Project p = new Project();
			p.setName("ATLEAST3" + c);
			Set<ConstraintViolation<Project>> violations = validator.validate(p);
			assertEquals("Wrong number of violations.", 1, violations.size());
		}
	}
}
