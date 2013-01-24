package ca.corefacility.bioinformatics.irida.service;

import ca.corefacility.bioinformatics.irida.model.User;

/**
 * All Service interfaces should extend this interface to inherit common methods
 * relating to creating, reading, updating and deleting objects from persistence.
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public interface UserService extends CRUDService<Long, User> {
    
}
