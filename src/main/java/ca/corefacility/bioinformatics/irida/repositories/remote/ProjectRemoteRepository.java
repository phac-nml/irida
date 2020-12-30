package ca.corefacility.bioinformatics.irida.repositories.remote;

import ca.corefacility.bioinformatics.irida.model.project.Project;

/**
 * Service for reading Project objects from a remote IRIDA API
 * 
 *
 */
public interface ProjectRemoteRepository extends RemoteRepository<Project> {
    public Integer readProjectHash(Project project);

}
