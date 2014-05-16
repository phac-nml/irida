package ca.corefacility.bioinformatics.irida.service.impl;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.UserNotInSecurityContextException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.repositories.RemoteApiTokenRepository;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

@Service
public class RemoteAPITokenServiceImpl implements RemoteAPITokenService{
	private static final Logger logger = LoggerFactory.getLogger(RemoteAPITokenServiceImpl.class);

	private RemoteApiTokenRepository tokenRepository;
	private UserRepository userRepository;

	@Autowired
	public RemoteAPITokenServiceImpl(RemoteApiTokenRepository tokenRepository, UserRepository userRepository) {
		super();
		this.tokenRepository = tokenRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	@Override
	public void addToken(RemoteAPIToken token) {
		User user = userRepository.loadUserByUsername(getUserName());
		token.setUser(user);
		removeOldToken(token.getRemoteApi());
		tokenRepository.save(token);
	}

	@Override
	public RemoteAPIToken getToken(RemoteAPI remoteAPI) throws EntityNotFoundException{
		User user = userRepository.loadUserByUsername(getUserName());
		RemoteAPIToken readTokenForApiAndUser = tokenRepository.readTokenForApiAndUser(remoteAPI, user);
		if(readTokenForApiAndUser == null){
			throw new EntityNotFoundException("Couldn't find an OAuth2 token for this API and User");
		}
		return readTokenForApiAndUser;
	}
	

	/**
	 * Get the username of the currently logged in user.
	 * @return String of the username of the currently logged in user
	 * @throws UserNotInSecurityContextException if the currently logged in user could not be read from the security context
	 */
	private String getUserName() throws UserNotInSecurityContextException{
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails details = (UserDetails) authentication.getPrincipal();
			String username = details.getUsername();
			
			return username;
		}
		
		throw new UserNotInSecurityContextException("The currently logged in user could not be read from the SecurityContextHolder");
	}
	
	/**
	 * Remove any old token for this user from the database
	 * @param token
	 */
	@Transactional
	protected void removeOldToken(RemoteAPI api){
		RemoteAPIToken oldToken = null;
		try{
			oldToken = getToken(api);
		}catch(EntityNotFoundException ex){
			logger.debug("No token found for service " + api);
		}
		
		if(oldToken != null){
			tokenRepository.delete(oldToken);
		}
	}
}
