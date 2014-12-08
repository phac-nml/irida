package ca.corefacility.bioinformatics.irida.ria.components;

import java.util.List;
import java.util.Set;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;

/**
 * A shopping cart like object for storing {@link Sample}s selected for running
 * an analysis
 * 
 * @author Josh Adam<josh.adam@phac-aspc.gc.ca>
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface Cart {

	/**
	 * Remove an entire project from the cart.
	 *
	 * @param project
	 *            {@link Project} to remove from the cart.
	 */
	public void removeProject(Project project);

	/**
	 * Add a list of samples for a project to the cart. If the project is not in
	 * the cart it should be added to the cart.
	 *
	 * @param project
	 *            The {@link Project} the samples belong to.
	 * @param samples
	 *            A list of {@link Sample} to add to the cart.
	 */
	public void addProjectSample(Project project, Set<Sample> samples);

	/**
	 * Remove a list of samples for a project from the cart. If this removes all
	 * the samples the project should be removed from the cart.
	 *
	 * @param project
	 *            The {@link Project} the samples belong to.
	 * @param samples
	 *            A list of {@link Sample} to remove from the cart.
	 */
	public void removeProjectSample(Project project, Set<Sample> samples);

	/**
	 * Returns a list of samples for a project that are in the cart.
	 *
	 * @param project
	 *            The {@link Project} to get the samples for.
	 *
	 * @return A {@link List} of {@link Sample} belonging to the {@link Project}
	 */
	public Set<Sample> getSelectedSamplesForProject(Project project);

	/**
	 * Get all of the {@link Project}s selected in the {@link Cart}
	 * 
	 * @return a Set of {@link Project}s
	 */
	public Set<Project> getProjects();
}
