package ca.corefacility.bioinformatics.irida.repositories.remote.token;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;

/**
 * An Map implementation of {@link TokenRepository}
 * @author tom
 *
 */
public class InMemoryTokenRepository implements TokenRepository{
	private Map<String,Map<RemoteAPI,String>> tokens;
	
	/**
	 * Create a new InMemoryTokenRepository
	 */
	public InMemoryTokenRepository(){
		tokens = new HashMap<>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addToken(RemoteAPI remoteAPI,String token){
		String username = getUserName();
		tokens.get(username).put(remoteAPI, token);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getToken(RemoteAPI remoteAPI){
		String username = getUserName();
		return tokens.get(username).get(remoteAPI);
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
			
			if(! tokens.containsKey(username)){
				Map<RemoteAPI,String> userMap = new HashMap<>();
				tokens.put(username, userMap);
			}
			
			return username;
		}
		
		return null;
	}
}