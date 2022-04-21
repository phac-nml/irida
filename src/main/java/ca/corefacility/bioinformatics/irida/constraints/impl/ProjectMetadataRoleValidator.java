package ca.corefacility.bioinformatics.irida.constraints.impl;

import ca.corefacility.bioinformatics.irida.constraints.ProjectRoleHasCorrectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectMetadataRole;
import ca.corefacility.bioinformatics.irida.model.enums.ProjectRole;

import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Hibernate validator to validate if a user/usegroup is a manager on a project then their metadata role should be set
 * to the highest level. Otherwise if a user/usergroup is a collaborator on a project then their metadata role can be
 * any of the levels (must not be null).
 */
public class ProjectMetadataRoleValidator implements ConstraintValidator<ProjectRoleHasCorrectMetadataRole, Object> {
	private String firstFieldName;
	private String secondFieldName;

	@Override
	public void initialize(final ProjectRoleHasCorrectMetadataRole constraintAnnotation) {
		firstFieldName = constraintAnnotation.first();
		secondFieldName = constraintAnnotation.second();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			// Get the projectRole and metadata role as ProjectRole and ProjectMetadataRole from strings
			ProjectRole projectRole = ProjectRole.fromString(BeanUtils.getProperty(value, firstFieldName));
			ProjectMetadataRole metadataRole = ProjectMetadataRole.fromString(
					BeanUtils.getProperty(value, secondFieldName));

			return (projectRole.equals(ProjectRole.PROJECT_OWNER) && metadataRole.equals(ProjectMetadataRole.LEVEL_4))
					|| (projectRole.equals(ProjectRole.PROJECT_USER) && metadataRole != null);
		} catch (final Exception e) {
			// ignore
		}
		return true;
	}
}
