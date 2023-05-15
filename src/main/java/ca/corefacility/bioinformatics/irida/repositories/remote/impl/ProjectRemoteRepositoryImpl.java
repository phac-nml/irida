package ca.corefacility.bioinformatics.irida.repositories.remote.impl;

import com.google.common.collect.ImmutableList;
import ca.corefacility.bioinformatics.irida.exceptions.LinkNotFoundException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.model.remote.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.ProjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate.OAuthTokenRestTemplate;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.user.UserService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ProjectHashResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectsController;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

/**
 * Remote repository for retrieving {@link Project}s from {@link RemoteAPI}s
 */
@Repository
public class ProjectRemoteRepositoryImpl extends RemoteRepositoryImpl<Project> implements ProjectRemoteRepository {

	// the type references for this repo
	private static final ParameterizedTypeReference<ListResourceWrapper<Project>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<Project>>() {
	};
	private static final ParameterizedTypeReference<ResourceWrapper<Project>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<Project>>() {
	};

	private static final ParameterizedTypeReference<ResourceWrapper<ProjectHashResource>> projectHashReference = new ParameterizedTypeReference<ResourceWrapper<ProjectHashResource>>() {
	};

	private RemoteAPITokenService tokenService;

	private static final String HASH_REL = RESTProjectsController.PROJECT_HASH_REL;

	/**
	 * Create a new {@link ProjectRemoteRepositoryImpl} with the given {@link RemoteAPITokenService}
	 *
	 * @param tokenService the {@link RemoteAPITokenService}
	 * @param userService  The {@link UserService} for reading users
	 */
	@Autowired
	public ProjectRemoteRepositoryImpl(RemoteAPITokenService tokenService, UserService userService) {
		super(tokenService, userService, listTypeReference, objectTypeReference);
		this.tokenService = tokenService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer readProjectHash(Project project) {
		if (!project.hasLink(HASH_REL)) {
			throw new LinkNotFoundException("No link for rel: " + HASH_REL);
		}

		RemoteAPI remoteAPI = project.getRemoteStatus().getApi();

		OAuthTokenRestTemplate restTemplate = new OAuthTokenRestTemplate(tokenService, remoteAPI);
		Link link = project.getLink(HASH_REL).map(i -> i).orElse(null);

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(ImmutableList.of(MediaType.APPLICATION_JSON));
		HttpEntity<Object> request = new HttpEntity<Object>(headers);

		ResponseEntity<ResourceWrapper<ProjectHashResource>> exchange = restTemplate.exchange(link.getHref(),
				HttpMethod.GET, request, projectHashReference);

		Integer projectHash = exchange.getBody().getResource().getProjectHash();

		return projectHash;
	}

}
