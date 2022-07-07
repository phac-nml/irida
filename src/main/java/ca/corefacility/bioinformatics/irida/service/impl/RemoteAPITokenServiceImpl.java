package ca.corefacility.bioinformatics.irida.service.impl;

import java.net.URI;
import java.util.Date;

import javax.ws.rs.core.UriBuilder;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.RemoteApiTokenRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

/**
 * Service implementation for storing and reading remote api tokens
 */
@Service
public class RemoteAPITokenServiceImpl implements RemoteAPITokenService {
	private static final Logger logger = LoggerFactory.getLogger(RemoteAPITokenServiceImpl.class);

	private static final long ONE_SECOND_IN_MS = 1000;

	private RemoteApiTokenRepository tokenRepository;
	private UserRepository userRepository;
	private final OAuthClient oauthClient;

	@Autowired
	public RemoteAPITokenServiceImpl(RemoteApiTokenRepository tokenRepository, UserRepository userRepository,
			OAuthClient oauthClient) {
		super();
		this.tokenRepository = tokenRepository;
		this.userRepository = userRepository;
		this.oauthClient = oauthClient;
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@Override
	public RemoteAPIToken create(RemoteAPIToken token) {
		User user = userRepository.loadUserByUsername(getUserName());
		token.setUser(user);

		// if an old token exists, get the old token's info so we can update it
		token = getOldTokenId(token);

		return tokenRepository.save(token);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RemoteAPIToken getToken(RemoteAPI remoteAPI) throws EntityNotFoundException {
		User user = userRepository.loadUserByUsername(getUserName());
		RemoteAPIToken readTokenForApiAndUser = tokenRepository.readTokenForApiAndUser(remoteAPI, user);
		if (readTokenForApiAndUser == null) {
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
	 * {@inheritDoc}
	 */
	@Transactional
	public RemoteAPIToken updateTokenFromRefreshToken(RemoteAPI api) {
		RemoteAPIToken token = null;

		try {
			token = getToken(api);

			String refreshToken = token.getRefreshToken();

			if (refreshToken != null) {
				URI serviceTokenLocation = UriBuilder.fromUri(api.getServiceURI()).path("oauth").path("token").build();

				OAuthClientRequest tokenRequest = OAuthClientRequest.tokenLocation(serviceTokenLocation.toString())
						.setClientId(api.getClientId())
						.setClientSecret(api.getClientSecret())
						.setRefreshToken(refreshToken)
						.setGrantType(GrantType.REFRESH_TOKEN)
						.buildBodyMessage();

				OAuthJSONAccessTokenResponse accessToken = oauthClient.accessToken(tokenRequest);

				token = buildTokenFromResponse(accessToken, api);

				delete(api);
				token = create(token);

				logger.trace("Token for api " + api + " updated by refresh token.");
			} else {
				logger.trace("No refresh token for api " + api + ". Cannot update access token.");
			}
		} catch (EntityNotFoundException ex) {
			logger.debug("Token not found for api " + api + ".  Cannot update access token.");
		} catch (OAuthProblemException | OAuthSystemException ex) {
			logger.error("Updating token by refresh token failed", ex.getMessage());
		}

		return token;
	}

	/**
	 * Get a new token from the given auth code
	 * 
	 * @param authcode      the auth code to create a token for
	 * @param remoteAPI     the remote api to get a token for
	 * @param tokenRedirect a redirect url to get the token from
	 * @return a new token
	 * @throws OAuthSystemException  If building the token request fails
	 * @throws OAuthProblemException If the token request fails
	 */
	@Transactional
	public RemoteAPIToken createTokenFromAuthCode(String authcode, RemoteAPI remoteAPI, String tokenRedirect)
			throws OAuthSystemException, OAuthProblemException {
		String serviceURI = remoteAPI.getServiceURI();

		// Build the token location for this service
		URI serviceTokenLocation = UriBuilder.fromUri(serviceURI).path("oauth").path("token").build();
		logger.trace("Remote token location: " + serviceTokenLocation);

		// Create the token request form the given auth code
		OAuthClientRequest tokenRequest = OAuthClientRequest.tokenLocation(serviceTokenLocation.toString())
				.setClientId(remoteAPI.getClientId())
				.setClientSecret(remoteAPI.getClientSecret())
				.setRedirectURI(tokenRedirect)
				.setCode(authcode)
				.setGrantType(GrantType.AUTHORIZATION_CODE)
				.buildBodyMessage();

		// execute the request
		OAuthJSONAccessTokenResponse accessTokenResponse = oauthClient.accessToken(tokenRequest);

		// read the response for the access token
		String accessToken = accessTokenResponse.getAccessToken();

		// Handle Refresh Tokens
		String refreshToken = accessTokenResponse.getRefreshToken();

		// check the token expiry
		Long expiresIn = accessTokenResponse.getExpiresIn();
		Long currentTime = System.currentTimeMillis();
		Date expiry = new Date(currentTime + (expiresIn * ONE_SECOND_IN_MS));
		logger.trace("Token expiry: " + expiry);

		// create the OAuth2 token and store it
		RemoteAPIToken token = new RemoteAPIToken(accessToken, refreshToken, remoteAPI, expiry);

		return create(token);
	}

	/**
	 * Get the username of the currently logged in user.
	 * 
	 * @return String of the username of the currently logged in user
	 */
	private String getUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && (authentication instanceof UsernamePasswordAuthenticationToken
				|| authentication instanceof JwtAuthenticationToken)) {
			String username = authentication.getName();

			return username;
		}
		throw new IllegalStateException(
				"The currently logged in user could not be read from the SecurityContextHolder");
	}

	/**
	 * Remove any old token for this user from the database
	 * 
	 * @param apiToken the api token to remove.
	 * @return the token that was found.
	 */
	protected RemoteAPIToken getOldTokenId(RemoteAPIToken apiToken) {
		RemoteAPIToken oldToken = null;
		try {
			oldToken = getToken(apiToken.getRemoteApi());
			logger.trace("Old token found for service " + apiToken.getRemoteApi());
			apiToken.setId(oldToken.getId());
		} catch (EntityNotFoundException ex) {
			logger.trace("No token found for service " + apiToken.getRemoteApi());
		}

		return apiToken;
	}

	private RemoteAPIToken buildTokenFromResponse(OAuthJSONAccessTokenResponse accessTokenResponse,
			RemoteAPI remoteAPI) {
		// read the response for the access token
		String accessToken = accessTokenResponse.getAccessToken();

		// Handle Refresh Tokens
		String refreshToken = accessTokenResponse.getRefreshToken();

		// check the token expiry
		Long expiresIn = accessTokenResponse.getExpiresIn();
		Long currentTime = System.currentTimeMillis();
		Date expiry = new Date(currentTime + (expiresIn * ONE_SECOND_IN_MS));
		logger.trace("Token expiry: " + expiry);

		// create the OAuth2 token
		return new RemoteAPIToken(accessToken, refreshToken, remoteAPI, expiry);
	}
}
