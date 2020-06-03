package ca.corefacility.bioinformatics.irida.repositories.remote;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.assembly.UploadedAssembly;
import org.springframework.http.MediaType;

import java.nio.file.Path;

public interface GenomeAssemblyRemoteRepository extends RemoteRepository<UploadedAssembly> {

    public Path downloadRemoteSequenceFile(String uri, RemoteAPI remoteAPI, MediaType... mediaTypes);

    public Path downloadRemoteSequenceFile(String uri, RemoteAPI remoteAPI);
}
