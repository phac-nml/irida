package ca.corefacility.bioinformatics.irida.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.RemoteApiTokenRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
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

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@Override
	public void create(RemoteAPIToken token) {
		User user = userRepository.loadUserByUsername(getUserName());
		token.setUser(user);
		
		//if an old token exists, get the old token's info so we can update it
		token=getOldTokenId(token);
		
		tokenRepository.save(token);
	}

	/**
	 * {@inheritDoc}
	 */
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
	 * {@inheritDoc}
	 */
	@Transactional
	@Override
	public void delete(RemoteAPI remoteAPI) throws EntityNotFoundException {
			RemoteAPIToken token = getToken(remoteAPI);
			tokenRepository.delete(token);
	}
	

	/**
	 * Get the username of the currently logged in user.
	 * @return String of the username of the currently logged in user
	 */
	private String getUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			UserDetails details = (UserDetails) authentication.getPrincipal();
			String username = details.getUsername();
			
			return username;
		}
		throw new IllegalStateException("The currently logged in user could not be read from the SecurityContextHolder");
	}
	
	/**
	 * Remove any old token for this user from the database
	 * 
	 * @param apiToken
	 *            the api token to remove.
	 * @return the token that was found.
	 */
	protected RemoteAPIToken getOldTokenId(RemoteAPIToken apiToken){
		RemoteAPIToken oldToken = null;
		try{
			oldToken = getToken(apiToken.getRemoteApi());
			logger.trace("Old token found for service " + apiToken.getRemoteApi());
			apiToken.setId(oldToken.getId());
		}catch(EntityNotFoundException ex){
			logger.trace("No token found for service " + apiToken.getRemoteApi());
		}
		
		return apiToken;
	}
}
