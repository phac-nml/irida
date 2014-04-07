package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import java.net.URI;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.repositories.remote.ProjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.RemoteProject;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.ResourceWrapper;

@Component
public class ProjectRemoteRepositoryImpl extends GenericRemoteRepositoryImpl<RemoteProject> implements ProjectRemoteRepository {


	public ProjectRemoteRepositoryImpl(OAuth2RestTemplate restTemplate){
		super(restTemplate,"projects",new ParameterizedTypeReference<ResourceWrapper<RemoteProject>>() {}, new ParameterizedTypeReference<ListResourceWrapper<RemoteProject>>() {});

	}

}
