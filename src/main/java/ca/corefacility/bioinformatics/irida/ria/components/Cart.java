package ca.corefacility.bioinformatics.irida.ria.components;

import java.util.List;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * @author Josh Adam<josh.adam@phac-aspc.gc.ca>
 */
public interface Cart {
	/**
	 * Add an entire project to the cart.
	 *
	 * @param project
	 * 		{@link Project} to add to the cart.
	 */
	public void addProject(Project project);

	/**
	 * Remove an entire project from the cart.
	 *
	 * @param project
	 * 		{@link Project} to remove from the cart.
	 */
	public void removeProject(Project project);

	/**
	 * Add a list of samples for a project to the cart.  If the project is not in the cart it should be added to the
	 * cart.
	 *
	 * @param project
	 * 		The {@link Project} the samples belong to.
	 * @param samples
	 * 		A list of {@link Sample} to add to the cart.
	 */
	public void addProjectSample(Project project, List<Sample> samples);

	/**
	 * Remove a list of samples for a project from the cart. If this removes all the samples the project should be
	 * removed from the cart.
	 *
	 * @param project
	 * 		The {@link Project} the samples belong to.
	 * @param samples
	 * 		A list of {@link Sample} to remove from the cart.
	 */
	public void removeProjectSample(Project project, List<Sample> samples);

	/**
	 * Returns a list of samples for a project that are in the cart.
	 *
	 * @param project
	 * 		The {@link Project} to get the samples for.
	 *
	 * @return A {@link List} of {@link Sample} belonging to the {@link Project}
	 */
	public List<Sample> getSelectedSamplesForProject(Project project);
}
