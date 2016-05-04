package ca.corefacility.bioinformatics.irida.exceptions;

import ca.corefacility.bioinformatics.irida.model.user.group.UserGroup;

/**
 * Exception thrown when a change to membership of a {@link UserGroup} causes
 * that group to have no owner.
 *
 */
public class UserGroupWithoutOwnerException extends Exception {

	public UserGroupWithoutOwnerException(final String message) {
		super(message);
	}
}
