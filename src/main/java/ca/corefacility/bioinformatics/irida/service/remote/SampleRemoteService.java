package ca.corefacility.bioinformatics.irida.service.remote;

import java.util.List;

import org.springframework.data.domain.Page;

import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteSample;

/**
 * Service for reading {@link RemoteSample}s
 * 
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public interface SampleRemoteService extends RemoteService<RemoteSample> {
	/**
	 * Get the {@link RemoteSample}s that exist in a {@link RemoteProject}
	 * 
	 * @param project
	 *            The {@link RemoteProject} to get samples from
	 * @return A List of {@link RemoteSample}s
	 */
	public List<RemoteSample> getSamplesForProject(RemoteProject project);

	/**
	 * Search the {@link RemoteSample}s that exist in a {@link RemoteProject}
	 * 
	 * @param project
	 *            The {@link RemoteProject} the samples are in
	 * @param search
	 *            The search term
	 * @param page
	 *            The page number
	 * @param size
	 *            The page size
	 * @return A Page of {@link RemoteSample}s
	 */
	public Page<RemoteSample> searchSamplesForProject(RemoteProject project, String search, int page, int size);
}
