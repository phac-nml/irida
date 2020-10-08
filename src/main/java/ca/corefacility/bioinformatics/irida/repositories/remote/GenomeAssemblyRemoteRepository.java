package ca.corefacility.bioinformatics.irida.repositories.remote;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.assembly.UploadedAssembly;

import org.springframework.http.MediaType;

import java.nio.file.Path;

/**
 * Repository for synchronizing {@link ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly} from a {@link RemoteAPI}
 */
public interface GenomeAssemblyRemoteRepository extends RemoteRepository<UploadedAssembly> {

	/**
	 * Download a {@link ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly} fom the given {@link RemoteAPI}
	 *
	 * @param uri        URI of the assembly to download
	 * @param remoteAPI  the {@link RemoteAPI} for the request
	 * @param mediaTypes the {@link MediaType} to use for the request
	 * @return a {@link Path} to the downloaded file
	 */
	public Path downloadRemoteAssembly(String uri, RemoteAPI remoteAPI, MediaType... mediaTypes);

	/**
	 * Download a {@link ca.corefacility.bioinformatics.irida.model.assembly.GenomeAssembly} fom the given {@link RemoteAPI} with a default {@link MediaType}
	 *
	 * @param uri       URI of the assembly to download
	 * @param remoteAPI the {@link RemoteAPI} for the request
	 * @return a {@link Path} to the downloaded file
	 */
	public Path downloadRemoteAssembly(String uri, RemoteAPI remoteAPI);
}
