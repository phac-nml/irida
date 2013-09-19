package ca.corefacility.bioinformatics.irida.repositories;

import ca.corefacility.bioinformatics.irida.model.OverrepresentedSequence;
import ca.corefacility.bioinformatics.irida.model.Project;
import ca.corefacility.bioinformatics.irida.model.Sample;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.joins.impl.SampleSequenceFileJoin;

import java.util.List;

/**
 * A repository to store information about sequence files. This repository will
 * not directly store the file, just metadata
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 */
public interface SequenceFileRepository extends CRUDRepository<Long, SequenceFile> {

	/**
	 * Get the {@link SequenceFile}s associated with a project
	 * 
	 * @param project
	 *            The project to get the files for
	 * @return a list of {@link ProjectSequenceFileJoin} objects
	 * @deprecated Files will no longer be able to be added to a Project
	 */
	public List<ProjectSequenceFileJoin> getFilesForProject(Project project);

	/**
	 * Get the {@link SequenceFile}s associated with a sample
	 * 
	 * @param sample
	 *            The sample to get the files for
	 * @return a list of {@link SampleSequenceFileJoin} objects
	 */
	public List<SampleSequenceFileJoin> getFilesForSample(Sample sample);

	/**
	 * Add a {@link SequenceFile} to a project
	 * 
	 * @param project
	 *            The project to add to
	 * @param file
	 *            The file to add to the project
	 * @return a {@link ProjectSequenceFileJoin} modeling the relationship
	 * @deprecated Files will no longer be able to be added to a Project
	 */
	public ProjectSequenceFileJoin addFileToProject(Project project, SequenceFile file);

	/**
	 * Add a {@link SequenceFile} to a {@link Sample}
	 * 
	 * @param sample
	 *            The sample to add to
	 * @param file
	 *            The file to add to the project
	 * @return a {@link SampleSequenceFileJoin} modeling the relationship
	 */
	public SampleSequenceFileJoin addFileToSample(Sample sample, SequenceFile file);

	/**
	 * Remove a {@link SequenceFile} from a {@link Project}
	 * 
	 * @param project
	 *            The project to remove the file from
	 * @param file
	 *            The file to remove
	 * @deprecated Files will no longer be able to be added to a Project
	 */
	public void removeFileFromProject(Project project, SequenceFile file);

	/**
	 * Remove a {@link SequenceFile} from a {@link Sample}
	 * 
	 * @param sample
	 *            The sample to remove the file from
	 * @param file
	 *            The file to remove
	 */
	public void removeFileFromSample(Sample sample, SequenceFile file);

	/**
	 * Add an {@link OverrepresentedSequence} to a {@link SequenceFile}.
	 * 
	 * @param sequenceFile
	 *            the {@link SequenceFile}.
	 * @param sequence
	 *            the {@link OverrepresentedSequence}.
	 * @return a {@link Join} representing the relationship.
	 */
	public Join<SequenceFile, OverrepresentedSequence> addOverrepresentedSequenceToSequenceFile(
			SequenceFile sequenceFile, OverrepresentedSequence sequence);

}
