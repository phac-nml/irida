package ca.corefacility.bioinformatics.irida.ria.web.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.security.permissions.project.ProjectOwnerPermission;
import ca.corefacility.bioinformatics.irida.security.permissions.sample.UpdateSamplePermission;

/**
 * Component to standardize user permissions for samples on the
 * linelist page.
 */
@Component
public class ProjectPermissions {
	private UpdateSamplePermission updateSamplePermission;
	private ProjectOwnerPermission projectOwnerPermission;

	@Autowired
	public ProjectPermissions(UpdateSamplePermission updateSamplePermission,
			ProjectOwnerPermission projectOwnerPermission) {
		this.updateSamplePermission = updateSamplePermission;
		this.projectOwnerPermission = projectOwnerPermission;
	}

	/**
	 * Test to see if the use can modify a specific {@link Sample}.
	 *
	 * @param sample {@link Sample}
	 * @return {@link Boolean} whether the user can modify the sample.
	 */
	public boolean canModifySample(Sample sample) {
		// This would be a manager on a project that owns that sample.
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		return updateSamplePermission.isAllowed(authentication, sample);
	}

	/**
	 * Test to see if the user can modify the entire {@link Project}.
	 *
	 * @param project {@link Project}
	 * @return {@link Boolean} whether the user can modify the project.
	 */
	public boolean canModifyProject(Project project) {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		return projectOwnerPermission.isAllowed(authentication, project);
	}
}
