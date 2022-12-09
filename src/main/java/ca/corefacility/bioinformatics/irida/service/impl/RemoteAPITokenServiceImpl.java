package ca.corefacility.bioinformatics.irida.service.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Date;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.corefacility.bioinformatics.irida.exceptions.EntityNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthProblemException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.model.user.User;
import ca.corefacility.bioinformatics.irida.repositories.RemoteApiTokenRepository;
import ca.corefacility.bioinformatics.irida.repositories.user.UserRepository;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;

/**
 * Service implementation for storing and reading remote api tokens
 */
@Service
public class RemoteAPITokenServiceImpl implements RemoteAPITokenService {
	private static final Logger logger = LoggerFactory.getLogger(RemoteAPITokenServiceImpl.class);

	private static final long ONE_SECOND_IN_MS = 1000;

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

			String refreshTokenValue = token.getRefreshToken();

			if (refreshTokenValue != null) {
				RefreshToken refreshToken = new RefreshToken(refreshTokenValue);

				URI serviceTokenLocation = UriBuilder.fromUri(api.getServiceURI()).path("oauth").path("token").build();

				ClientAuthentication clientAuth = new ClientSecretBasic(new ClientID(api.getClientId()),
						new Secret(api.getClientSecret()));

				TokenRequest tokenRequest = new TokenRequest(serviceTokenLocation, clientAuth,
						new RefreshTokenGrant(refreshToken));

				TokenResponse tokenResponse = TokenResponse.parse(tokenRequest.toHTTPRequest().send());

				if (!tokenResponse.indicatesSuccess()) {
					// We got an error response...
					TokenErrorResponse errorResponse = tokenResponse.toErrorResponse();
					logger.error("Updating token by refresh token failed", errorResponse.getErrorObject().toString());
				} else {

					AccessTokenResponse accessTokenResponse = tokenResponse.toSuccessResponse();

					token = buildTokenFromResponse(accessTokenResponse, api);

					delete(api);
					token = create(token);

					logger.trace("Token for api " + api + " updated by refresh token.");

				}
			} else {
				logger.trace("No refresh token for api " + api + ". Cannot update access token.");
			}
		} catch (EntityNotFoundException ex) {
			logger.debug("Token not found for api " + api + ".  Cannot update access token.");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	 * @throws IOException
	 */
	@Transactional
	public RemoteAPIToken createTokenFromAuthCode(AuthorizationCode authcode, RemoteAPI remoteAPI, URI tokenRedirect)
			throws ParseException {
		String serviceURI = remoteAPI.getServiceURI();

		// Build the token location for this service
		URI serviceTokenLocation = UriBuilder.fromUri(serviceURI).path("oauth").path("token").build();
		logger.trace("Remote token location: " + serviceTokenLocation);

		ClientAuthentication clientAuth = new ClientSecretBasic(new ClientID(remoteAPI.getClientId()),
				new Secret(remoteAPI.getClientSecret()));

		// Create the token request form the given auth code
		TokenRequest tokenRequest = new TokenRequest(serviceTokenLocation, clientAuth,
				new AuthorizationCodeGrant(authcode, tokenRedirect));

		// execute the request
		TokenResponse tokenResponse;
		try {
			tokenResponse = TokenResponse.parse(tokenRequest.toHTTPRequest().send());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IridaOAuthProblemException("message");
		}

		if (!tokenResponse.indicatesSuccess()) {
			// We got an error response...
			TokenErrorResponse errorResponse = tokenResponse.toErrorResponse();
		}

		AccessTokenResponse accessTokenResponse = tokenResponse.toSuccessResponse();

		// create the OAuth2 token and store it
		RemoteAPIToken token = buildTokenFromResponse(accessTokenResponse, remoteAPI);

		return create(token);
	}

	/**
	 * Get the username of the currently logged in user.
	 * 
	 * @return String of the username of the currently logged in user
	 */
	private String getUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && (authentication.getPrincipal() instanceof UserDetails
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

	private RemoteAPIToken buildTokenFromResponse(AccessTokenResponse accessTokenResponse, RemoteAPI remoteAPI) {
		// read the response for the access token
		AccessToken accessToken = accessTokenResponse.getTokens().getAccessToken();

		// Handle Refresh Tokens
		RefreshToken refreshToken = accessTokenResponse.getTokens().getRefreshToken();

		// check the token expiry
		Long expiresIn = accessToken.getLifetime();
		Long currentTime = System.currentTimeMillis();
		Date expiry = new Date(currentTime + (expiresIn * ONE_SECOND_IN_MS));
		logger.trace("Token expiry: " + expiry);

		// create the OAuth2 token
		RemoteAPIToken token;
		if (refreshToken != null) {
			token = new RemoteAPIToken(accessToken.getValue(), refreshToken.getValue(), remoteAPI, expiry);
		} else {
			token = new RemoteAPIToken(accessToken.getValue(), remoteAPI, expiry);
		}

		return token;
	}
}
