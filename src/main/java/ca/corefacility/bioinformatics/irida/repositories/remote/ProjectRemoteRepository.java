package ca.corefacility.bioinformatics.irida.repositories.remote;

import java.net.URI;
import java.util.List;

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

import ca.corefacility.bioinformatics.irida.repositories.remote.model.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.RemoteProject;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.ResourceWrapper;

@Component
public class ProjectRemoteRepository {
	ParameterizedTypeReference<ListResourceWrapper<RemoteProject>> listTypeReference;
	ParameterizedTypeReference<ResourceWrapper<RemoteProject>> objectTypeReference;
	
	private URI baseURI;
	private OAuth2RestTemplate restTemplate;
	//ResourceOwnerPasswordResourceDetails det;
	AuthorizationCodeResourceDetails adet;
	
	private URI projectsURI;
	
	/*public ProjectRemoteRepository(){
		AuthorizationCodeAccessTokenProvider b;
		
		//det = new ResourceOwnerPasswordResourceDetails();
		adet = new AuthorizationCodeResourceDetails();
		
		adet.setClientId("linker");
		adet.setClientSecret("linkerSecret");
		adet.setAccessTokenUri(baseURI.resolve("oauth/token").toString());
		adet.setUserAuthorizationUri("http://localhost:8080/login");
		restTemplate = new OAuth2RestTemplate(adet);
		
		
		
		listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<RemoteProject>>() {};
		objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<RemoteProject>>() {};
	}*/
	
	public ProjectRemoteRepository(OAuth2RestTemplate restTemplate){
		this.restTemplate = restTemplate;
		listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<RemoteProject>>() {};
		objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<RemoteProject>>() {};
	}
	
	public List<RemoteProject> list(){
		OAuth2AccessToken accessToken = restTemplate.getAccessToken();
		
		System.out.println("My access token is " + accessToken.getValue());
		System.out.println("Requesting from " + projectsURI);
		ResponseEntity<String> forEntity = restTemplate.getForEntity(projectsURI, String.class);
		
		System.out.println(forEntity.getBody());

		ResponseEntity<ListResourceWrapper<RemoteProject>> exchange = restTemplate.exchange(projectsURI, HttpMethod.GET, HttpEntity.EMPTY, listTypeReference);
		ListResourceWrapper<RemoteProject> body = exchange.getBody();
		for(RemoteProject p : body.getResource().getResources()){
			System.out.println(p.getName());
		}
		
		return body.getResource().getResources();
	}
	
	public void setBaseURI(URI baseURI){
		this.baseURI = baseURI;
		projectsURI = baseURI.resolve("/projects");
	}
}
