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
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;
import ca.corefacility.bioinformatics.irida.model.user.group.UserGroupProjectJoin;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserGroupProjectJoinTest {

	private static final String MESSAGES_BASENAME = "ValidationMessages";
	private Validator validator;

	UserGroup userGroup = new UserGroup("NewGroup");
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
		UserGroupProjectJoin userGroupProjectJoin = new UserGroupProjectJoin(project, userGroup, ProjectRole.PROJECT_OWNER, ProjectMetadataRole.LEVEL_4);

		Set<ConstraintViolation<UserGroupProjectJoin>> violations = validator.validate(userGroupProjectJoin);
		assertEquals(0, violations.size(), "Should have no violations");
	}

	@Test
	public void testInvalidMetadataRoleForProjectOwner() {
		UserGroupProjectJoin userGroupProjectJoin = new UserGroupProjectJoin(project, userGroup, ProjectRole.PROJECT_OWNER, ProjectMetadataRole.LEVEL_1);
		Set<ConstraintViolation<UserGroupProjectJoin>> violations = validator.validate(userGroupProjectJoin);
		assertEquals(1, violations.size(), "Should have 1 violation");

		userGroupProjectJoin = new UserGroupProjectJoin(project, userGroup, ProjectRole.PROJECT_OWNER, ProjectMetadataRole.LEVEL_2);
		violations = validator.validate(userGroupProjectJoin);
		assertEquals(1, violations.size(), "Should have 1 violation");

		userGroupProjectJoin = new UserGroupProjectJoin(project, userGroup, ProjectRole.PROJECT_OWNER, ProjectMetadataRole.LEVEL_3);
		violations = validator.validate(userGroupProjectJoin);
		assertEquals(1, violations.size(), "Should have 1 violation");
	}

	@Test
	public void testNoMetadataRoleForProjectOwner() {
		UserGroupProjectJoin userGroupProjectJoin = new UserGroupProjectJoin(project, userGroup, ProjectRole.PROJECT_OWNER, null);

		Set<ConstraintViolation<UserGroupProjectJoin>> violations = validator.validate(userGroupProjectJoin);
		assertEquals(1, violations.size(), "Should have 1 violation");
	}

	@Test
	public void testValidMetadataRoleForProjectCollaborator() {
		UserGroupProjectJoin userGroupProjectJoin = new UserGroupProjectJoin(project, userGroup, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_1);
		Set<ConstraintViolation<UserGroupProjectJoin>> violations = validator.validate(userGroupProjectJoin);
		assertEquals(0, violations.size(), "Should have no violations");

		userGroupProjectJoin = new UserGroupProjectJoin(project, userGroup, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_2);
		violations = validator.validate(userGroupProjectJoin);
		assertEquals(0, violations.size(), "Should have no violations");

		userGroupProjectJoin = new UserGroupProjectJoin(project, userGroup, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_3);
		violations = validator.validate(userGroupProjectJoin);
		assertEquals(0, violations.size(), "Should have no violations");

		userGroupProjectJoin = new UserGroupProjectJoin(project, userGroup, ProjectRole.PROJECT_USER, ProjectMetadataRole.LEVEL_4);
		violations = validator.validate(userGroupProjectJoin);
		assertEquals(0, violations.size(), "Should have no violations");
	}

	@Test
	public void testNoMetadataRoleForProjectCollaborator() {
		UserGroupProjectJoin userGroupProjectJoin = new UserGroupProjectJoin(project, userGroup, ProjectRole.PROJECT_USER, null);

		Set<ConstraintViolation<UserGroupProjectJoin>> violations = validator.validate(userGroupProjectJoin);
		assertEquals(1, violations.size(), "Should have 1 violation");
	}
}
