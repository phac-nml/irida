package ca.corefacility.bioinformatics.irida.security.permissions;

import org.springframework.security.core.Authentication;

public interface BasePermission<DomainObjectType> {

	String getPermissionProvided();
	
	boolean isAllowed(Authentication authentication, Object targetDomainObject);
}
