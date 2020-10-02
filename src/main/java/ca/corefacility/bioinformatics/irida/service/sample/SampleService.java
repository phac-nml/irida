package ca.corefacility.bioinformatics.irida.service.sample;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.SequenceFileAnalysisException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.QCEntry;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequencingObjectJoin;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.service.CRUDService;

/**
 * A service class for working with samples.
 * 
 */
public interface SampleService extends CRUDService<Long, Sample> {

	/**
	 * Get a specific instance of a {@link Sample} that belongs to a
	 * {@link Project}. If the {@link Sample} is not associated to the
	 * {@link Project} (i.e., no relationship is shared between the
	 * {@link Sample} and {@link Project}, then an
	 * {@link EntityNotFoundException} will be thrown.
	 * 
	 * @param project
	 *            the {@link Project} to get the {@link Sample} for.
	 * @param identifier
	 *            the identifier of the {@link Sample}
	 * @return the {@link ProjectSampleJoin} describing the relationship between projet and sample
	 * @throws EntityNotFoundException
	 *             if no relationship exists between {@link Sample} and
	 *             {@link Project}.
	 */
	public ProjectSampleJoin getSampleForProject(Project project, Long identifier) throws EntityNotFoundException;
	
	/**
	 * Find a {@link Sample} assocaited with a {@link SequencingObject}
	 * 
	 * @param seqObject
	 *            the {@link SequencingObject} to get the {@link Sample} for
	 * @return the {@link SampleSequencingObjectJoin} describing the
	 *         relationship
	 */
	public SampleSequencingObjectJoin getSampleForSequencingObject(SequencingObject seqObject);

	/**
	 * Get the list of {@link Sample} that belongs to a specific project.
	 * 
	 * @param project
	 *            the {@link Project} to get samples for.
	 * @return the collection of samples for the {@link Project}.
	 */
	public List<Join<Project, Sample>> getSamplesForProject(Project project);

	/**
	 * Get a shallow listing of the {@link Sample}s in a {@link Project}.  Note: This method will not return any
	 * metadata or associated objects.
	 *
	 * @param project The {@link Project} to get samples for
	 * @return a List of {@link Sample}
	 */
	public List<Sample> getSamplesForProjectShallow(Project project);

	/**
	 * Get a list of {@link Sample} in a {@link Project} given some Sample ids.
	 * @param project {@link Project} to get samples for.
	 * @param sampleIds List of {@link Sample} ids.
	 * @return List of Samples from a {@link Project}.
	 */
	List<Sample> getSamplesInProject(Project project, List<Long> sampleIds);
		
	/**
	 * Get a list of the organism fields stored for all {@link Sample}s in a
	 * {@link Project}
	 * 
	 * @param project
	 *            the {@link Project} to get sample organisms for
	 * @return a list of sample organisms
	 */
	public List<String> getSampleOrganismsForProject(Project project);
	
	/**
	 * Get the number of {@link Sample}s for a given {@link Project}. This
	 * method will be faster than getSamplesForProjects
	 * 
	 * @param project
	 *            The project to get samples for
	 * @return The number of {@link Sample}s in a given {@link Project}
	 */
	public Long getNumberOfSamplesForProject(Project project);

	/**
	 * Get the {@link Sample}s for a {@link Project} in page form
	 * 
	 * @param project
	 *            The project to read from
	 * @param name
	 *            The sample name to search
	 * @param page
	 *            The page number
	 * @param size
	 *            The size of the page
	 * @param order
	 *            The order of the page
	 * @param sortProperties
	 *            The properties to sort on
	 * @return A {@link Page} of {@link Join}s between {@link Project} and
	 *         {@link Sample}
	 */
	public Page<ProjectSampleJoin> getSamplesForProjectWithName(Project project, String name, int page, int size,
			Direction order, String... sortProperties);

	/**
	 * Get the {@link Sample} for the given ID
	 * 
	 * @param project
	 *            the {@link Project} that the {@link Sample} belongs to.
	 * @param sampleName
	 *            The name for the requested sample
	 * @return A {@link Sample} with the given ID
	 */
	public Sample getSampleBySampleName(Project project, String sampleName);
	
	/**
	 * Remove a {@link SequencingObject} from a given {@link Sample}. This will
	 * delete the {@link SampleSequencingObjectJoin} object
	 * 
	 * @param sample
	 *            {@link Sample} to remove sequences from
	 * @param object
	 *            {@link SequencingObject} to remove
	 */
	public void removeSequencingObjectFromSample(Sample sample, SequencingObject object);

	/**
	 * Merge multiple samples into one. Merging samples copies the
	 * {@link SequenceFile} references from the set of samples into one sample.
	 * The collection of samples in <code>toMerge</code> are marked as deleted.
	 * All samples must be associated with the specified project. The
	 * relationship between each sample in <code>toMerge</code> and the project
	 * p will be deleted.
	 * 
	 * @param p
	 *            the {@link Project} that all samples must belong to.
	 * @param mergeInto
	 *            the {@link Sample} to merge other samples into.
	 * @param toMerge
	 *            the collection of {@link Sample} to merge.
	 * @return the completely merged {@link Sample} (the persisted version of
	 *         <code>mergeInto</code>).
	 */
	public Sample mergeSamples(Project p, Sample mergeInto, Collection<Sample> toMerge);

	/**
	 * Given a sample gets the total number of bases in all sequence files in
	 * this sample.
	 * 
	 * @param sample
	 *            The sample to find the total number of bases.
	 * @return The total number of bases in all sequence files in this sample.
	 * @throws SequenceFileAnalysisException
	 *             If there was an error getting FastQC analyses for a sequence
	 *             file.
	 */
	public Long getTotalBasesForSample(Sample sample)
			throws SequenceFileAnalysisException;


	/**
	 * Given the length of a reference file, estimate the total coverage for
	 * this sample.
	 * 
	 * @param sample
	 *            The sample to estimate coverage for.
	 * 
	 * @param referenceFileLength
	 *            The length of the reference file in bases.
	 * @return The estimate coverage of all sequence data in this sample.
	 * @throws SequenceFileAnalysisException
	 *             If there was an error getting FastQC analyses for a sequence
	 *             file.
	 */
	public Double estimateCoverageForSample(Sample sample,
			long referenceFileLength) throws SequenceFileAnalysisException;
	
	/**
	 * Given a {@link ReferenceFile}, estimate the total coverage for
	 * this sample.
	 * 
	 * @param sample
	 *            The sample to estimate coverage for.
	 * 
	 * @param referenceFile
	 *            The {@link ReferenceFile} to estimate coverage for.
	 * @return The estimate coverage of all sequence data in this sample.
	 * @throws SequenceFileAnalysisException
	 *             If there was an error getting FastQC analyses for a sequence
	 *             file.
	 */
	public Double estimateCoverageForSample(Sample sample,
			ReferenceFile referenceFile) throws SequenceFileAnalysisException;

	/**
	 * Get a {@link Page} of {@link ProjectSampleJoin} for samples from 1 or more projects based on filtering criteria.
	 *
	 * @param projects
	 * 		{@link List} of {@link Project} the {@link Sample}s must be found within.
	 * @param sampleNames
	 * 		{@link List} of {@link String} of Sample names to search
	 * @param sampleName
	 * 	    {@link String} exact name of a specific {@link Sample}
	 * @param searchTerm
	 * 		{@link String} search term to search for.
	 * @param organism
	 * 		{@link String} organism ter to search for.
	 * @param minDate
	 * 		{@link Date} minimum date the sample was modified.
	 * @param maxDate
	 * 		{@link Date} maximum date the sample was modified.
	 * @param currentPage
	 * 		{@link Integer} the current page the table is on.
	 * @param pageSize
	 * 		{@link Integer} the number of {@link ProjectSampleJoin} in the {@link Page}.
	 * @param sort
	 * 		{@link Sort} chained sort definitions to sort page by.
	 *
	 * @return a {@link Page} of {@link ProjectSampleJoin} that are filtered and sorted.
	 */
	public Page<ProjectSampleJoin> getFilteredSamplesForProjects(List<Project> projects, List<String> sampleNames,
			String sampleName, String searchTerm, String organism,
			Date minDate, Date maxDate, int currentPage, int pageSize, Sort sort);

	/**
	 * Get a list of all {@link Sample}s associated with a given
	 * {@link AnalysisSubmission}
	 *
	 * @param submission
	 *            the {@link AnalysisSubmission}
	 * @return a Collection of {@link Sample}
	 */
	public Collection<Sample> getSamplesForAnalysisSubmission(AnalysisSubmission submission);
	
	/**
	 * Find all the {@link QCEntry} associated with {@link SequencingObject}s in
	 * a given {@link Sample}
	 * 
	 * @param sample
	 *            the {@link Sample} to get {@link QCEntry} for
	 * @return a list of {@link QCEntry}
	 */
	public List<QCEntry> getQCEntriesForSample(Sample sample);

	/**
	 * Search all {@link Sample}s in projects the current logged in user has
	 * access to
	 * 
	 * @param query
	 *            the query string to search
	 * @param page
	 *            which page to return
	 * @param count
	 *            the number of entities to return
	 * @param sort
	 *            how to sort the result
	 * @return a page of {@link ProjectSampleJoin}
	 */
	public Page<ProjectSampleJoin> searchSamplesForUser(String query, final Integer page, final Integer count,
			final Sort sort);

	/**
	 * Search all {@link Sample}s in the database on the given query
	 * 
	 * @param query
	 *            the query string to search
	 * @param page
	 *            which page to return
	 * @param count
	 *            the number of entities to return
	 * @param sort
	 *            how to sort the result
	 * @return a page of {@link ProjectSampleJoin}
	 */
	public Page<ProjectSampleJoin> searchAllSamples(String query, final Integer page, final Integer count,
			final Sort sort);

	/**
	 * Get count of samples created in the time period
	 * @return An {@link Long} count of samples created
	 */
	public Long getSamplesCreated(Date createdDate);
}
