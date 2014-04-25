package ca.corefacility.bioinformatics.irida.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.model.User;
import ca.corefacility.bioinformatics.irida.repositories.UserRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.RemoteApiTokenRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

@Transactional
@Service
public class RemoteAPITokenServiceImpl implements RemoteAPITokenService{
	private RemoteApiTokenRepository tokenRepository;
	private UserRepository userRepository;

	@Autowired
	public RemoteAPITokenServiceImpl(RemoteApiTokenRepository tokenRepository, UserRepository userRepository) {
		super();
		this.tokenRepository = tokenRepository;
		this.userRepository = userRepository;
	}

	@Override
	public void addToken(RemoteAPI remoteAPI, RemoteAPIToken token) {
		User user = userRepository.loadUserByUsername(getUserName());
		token.setUser(user);
		tokenRepository.save(token);
	}

	@Override
	public RemoteAPIToken getToken(RemoteAPI remoteAPI) {
		User user = userRepository.loadUserByUsername(getUserName());
		
		return tokenRepository.readForApiAndUser(remoteAPI, user);
	}
	

	/**
	 * Get the username of the currently logged in user.
	 * @return String of the username of the currently logged in user
	 */
	private String getUserName(){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication.getPrincipal() instanceof UserDetails) {
			UserDetails details = (UserDetails) authentication.getPrincipal();
			String username = details.getUsername();
			
			return username;
		}
		
		return null;
	}
}
