package ca.corefacility.bioinformatics.irida.model;

import java.util.Set;

import javax.validation.*;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectUserJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectUserJoinTest {

	private static final String MESSAGES_BASENAME = "ValidationMessages";
	private Validator validator;

	private User user = new User(1L, "jeff", "jeff@somewhere.com", "ABCDEFGHIJ", "Jeff", "Guy", "5678");
	private Project project = new Project("NewProject");

	@BeforeEach
	public void setUp() {
		Configuration<?> configuration = Validation.byDefaultProvider().configure();
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename(MESSAGES_BASENAME);
		configuration.messageInterpolator(new ResourceBundleMessageInterpolator(new PlatformResourceBundleLocator(
				MESSAGES_BASENAME)));
		ValidatorFactory factory = configuration.buildValidatorFactory();
		validator = factory.getValidator();

		project.setId(1L);
	}

	@Test
	public void testValidMetadataRoleForProjectOwner() {
		ProjectUserJoin projectUserJoin = new ProjectUserJoin(project, user, ProjectRole.PROJECT_OWNER, ProjectMetadataRole.LEVEL_4);

		Set<ConstraintViolation<ProjectUserJoin>> violations = validator.validate(projectUserJoin);
		assertEquals(0, violations.size(), "Should have no violations");
	}

	@Test
	public void testInvalidMetadataRoleForProjectOwner() {
		ProjectUserJoin projectUserJoin = new ProjectUserJoin(project, user, ProjectRole.PROJECT_OWNER, ProjectMetadataRole.LEVEL_1);
		Set<ConstraintViolation<ProjectUserJoin>> violations = validator.validate(projectUserJoin);
		assertEquals(1, violations.size(), "Should have 1 violation");

		projectUserJoin = new ProjectUserJoin(project, user, ProjectRole.PROJECT_OWNER, ProjectMetadataRole.LEVEL_2);
		violations = validator.validate(projectUserJoin);
		assertEquals(1, violations.size(), "Should have 1 violation");

		projectUserJoin = new ProjectUserJoin(project, user, ProjectRole.PROJECT_OWNER, ProjectMetadataRole.LEVEL_3);
		violations = validator.validate(projectUserJoin);
		assertEquals(1, violations.size(), "Should have 1 violation");
	}

	@Test
	public void testNoMetadataRoleForProjectOwner() {
		ProjectUserJoin projectUserJoin = new ProjectUserJoin(project, user, ProjectRole.PROJECT_OWNER, null);

		Set<ConstraintViolation<ProjectUserJoin>> violations = validator.validate(projectUserJoin);
		assertEquals(1, violations.size(), "Should have 1 violation");
	}

	@Test
	public void testValidMetadataRoleForProjectCollaborator() {
		ProjectUserJoin projectUserJoin = new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1);
		Set<ConstraintViolation<ProjectUserJoin>> violations = validator.validate(projectUserJoin);
		assertEquals(0, violations.size(), "Should have no violations");

		projectUserJoin = new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_2);
		violations = validator.validate(projectUserJoin);
		assertEquals(0, violations.size(), "Should have no violations");

		projectUserJoin = new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_3);
		violations = validator.validate(projectUserJoin);
		assertEquals(0, violations.size(), "Should have no violations");

		projectUserJoin = new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_4);
		violations = validator.validate(projectUserJoin);
		assertEquals(0, violations.size(), "Should have no violations");
	}

	@Test
	public void testNoMetadataRoleForProjectCollaborator() {
		ProjectUserJoin p = new ProjectUserJoin(project, user, ProjectRole.PROJECT_USER, null);

		Set<ConstraintViolation<ProjectUserJoin>> violations = validator.validate(p);
		assertEquals(1, violations.size(), "Should have 1 violation");
	}

}
