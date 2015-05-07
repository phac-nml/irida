package ca.corefacility.bioinformatics.irida.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.NoPercentageCompleteException;
import ca.corefacility.bioinformatics.irida.model.enums.AnalysisState;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFilePair;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.AnalysisSubmission;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.IridaWorkflowNamedParameters;

/**
 * A service for AnalysisSubmissions.
 * 
 *
 */
public interface AnalysisSubmissionService extends CRUDService<Long, AnalysisSubmission> {

	/**
	 * Given an analysis submission id, gets the state of this analysis.
	 * 
	 * @param analysisSubmissionId
	 *            The id of this analysis.
	 * @return The state of this analysis.
	 * @throws EntityNotFoundException
	 *             If the corresponding analysis cannot be found.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#analysisSubmissionId, 'canReadAnalysisSubmission')")
	public AnalysisState getStateForAnalysisSubmission(Long analysisSubmissionId) throws EntityNotFoundException;
	
	/**
	 * Gets a {@link Set} of {@link AnalysisSubmission}s for a {@link User}.
	 * 
	 * @param user
	 *            The {@link User} to find all submissions for.
	 * @return A {@link Set} of {@link AnalysisSubmission}s for a user.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or authentication.name == #user.username")
	public Set<AnalysisSubmission> getAnalysisSubmissionsForUser(User user);

	/**
	 * Gets a {@link Set} of {@link AnalysisSubmission}s for the current
	 * {@link User}.
	 * 
	 * @return A {@link Set} of {@link AnalysisSubmission}s for the current
	 *         user.
	 */
	@PreAuthorize("hasRole('ROLE_USER')")
	public Set<AnalysisSubmission> getAnalysisSubmissionsForCurrentUser();
	
	/**
	 * Submit {@link AnalysisSubmission} for workflows allowing multiple one
	 * {@link SequenceFile} or {@link SequenceFilePair}
	 *
	 * @param workflow
	 *            {@link IridaWorkflow} that the files will be run on
	 * @param ref
	 *            {@link Long} id for a {@link ReferenceFile}
	 * @param sequenceFiles
	 *            {@link List} of {@link SequenceFile} to run on the workflow
	 * @param sequenceFilePairs
	 *            {@link List} of {@link SequenceFilePair} to run on the
	 *            workflow
	 * @param unnamedParameters
	 *            {@link Map} of parameters specific for the pipeline
	 * @param namedParameters
	 *            the named parameters to use for the workflow.
	 * @param name
	 *            {@link String} the name for the analysis
	 * @return the {@link AnalysisSubmission} created for the files.
	 */
	@PreAuthorize("hasRole('ROLE_USER')")
	public AnalysisSubmission createMultipleSampleSubmission(IridaWorkflow workflow, Long ref,
			List<SequenceFile> sequenceFiles, List<SequenceFilePair> sequenceFilePairs,
			Map<String, String> unnamedParameters, IridaWorkflowNamedParameters namedParameters, String name);

	/**
	 * Submit {@link AnalysisSubmission} for workflows requiring only one
	 * {@link SequenceFile} or {@link SequenceFilePair}
	 *
	 * @param workflow
	 *            {@link IridaWorkflow} that the files will be run on
	 * @param ref
	 *            {@link Long} id for a {@link ReferenceFile}
	 * @param sequenceFiles
	 *            {@link List} of {@link SequenceFile} to run on the workflow
	 * @param sequenceFilePairs
	 *            {@link List} of {@link SequenceFilePair} to run on the
	 *            workflow
	 * @param unnamedParameters
	 *            {@link Map} of parameters specific for the pipeline
	 * @param namedParameters
	 *            the named parameters to use for the workflow.
	 * @param name
	 *            {@link String} the name for the analysis
	 * @return the {@link Collection} of {@link AnalysisSubmission} created for
	 *         the supplied files.
	 */
	@PreAuthorize("hasRole('ROLE_USER')")
	public Collection<AnalysisSubmission> createSingleSampleSubmission(IridaWorkflow workflow, Long ref,
			List<SequenceFile> sequenceFiles, List<SequenceFilePair> sequenceFilePairs,
			Map<String, String> unnamedParameters, IridaWorkflowNamedParameters namedParameters, String name);
	
	/**
	 * Given the id of an {@link AnalysisSubmission} gets the percentage
	 * complete.
	 * 
	 * @param id
	 *            The id of an {@link AnalysisSubmission}.
	 * @return The percentage complete for this {@link AnalysisSubmission}.
	 * @throws NoPercentageCompleteException
	 *             An exception that indicates there is no percentage complete
	 *             for the submission.
	 * @throws ExecutionManagerException
	 *             If there was an issue when contacting the execution manager.
	 * @throws EntityNotFoundException
	 *             If no such corresponding submission exists.
	 */
	@PreAuthorize("hasRole('ROLE_ADMIN') or hasPermission(#id, 'canReadAnalysisSubmission')")
	public float getPercentCompleteForAnalysisSubmission(Long id) throws EntityNotFoundException,
			NoPercentageCompleteException, ExecutionManagerException;
}
