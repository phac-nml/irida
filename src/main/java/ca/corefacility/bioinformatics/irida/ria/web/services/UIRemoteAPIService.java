package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.model.RemoteAPIToken;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ProjectSyncFrequency;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteStatus;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.remote.CreateRemoteProjectRequest;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.remote.RemoteAPIModel;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.remote.RemoteProjectModel;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.RemoteAPITokenService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

/**
 * UI Service to handle request for Remote APIs
 */
@Component
public class UIRemoteAPIService {
	private final RemoteAPIService remoteAPIService;
	private final RemoteAPITokenService tokenService;
	private final ProjectRemoteService projectRemoteService;
	private final ProjectService projectService;

	private MessageSource messageSource;

	@Autowired
	public UIRemoteAPIService(RemoteAPIService remoteAPIService, RemoteAPITokenService tokenService,
			ProjectRemoteService projectRemoteService, ProjectService projectService, MessageSource messageSource) {
		this.remoteAPIService = remoteAPIService;
		this.tokenService = tokenService;
		this.projectRemoteService = projectRemoteService;
		this.projectService = projectService;
		this.messageSource = messageSource;
	}

	/**
	 * Check the status of a {@link RemoteAPI}
	 *
	 * @param remoteId identifier for a {@link RemoteAPI}
	 * @return expiration {@link Date} of the REmote API Token
	 */
	public Date checkAPIStatus(long remoteId) {
		RemoteAPI api = remoteAPIService.read(remoteId);
		RemoteAPIToken token = tokenService.getToken(api);

		boolean isValidToken = projectRemoteService.getServiceStatus(api);

		if (token.isExpired()) {
			throw new IridaOAuthException("expired token", api);
		} else if (!isValidToken) {
			throw new IridaOAuthException("invalid token", api);
		}
		return token.getExpiryDate();
	}

	/**
	 * Get details about a specific {@link RemoteAPI}
	 *
	 * @param remoteId identifier for the {@link RemoteAPI}
	 * @return {@link RemoteAPIModel} containing the details of the {@link RemoteAPI}
	 */
	public RemoteAPIModel getRemoteApiDetails(long remoteId) {
		RemoteAPI remoteAPI = remoteAPIService.read(remoteId);
		return new RemoteAPIModel(remoteAPI);
	}

	/**
	 * Delete a specific {@link RemoteAPI}
	 *
	 * @param remoteId Identifier for the {@link RemoteAPI} to delete
	 */
	public void deleteRemoteAPI(long remoteId) {
		remoteAPIService.delete(remoteId);
	}

	/**
	 * Get a list of all {@link RemoteAPI}s
	 *
	 * @return A list of {@link RemoteAPI} formatted for the UI
	 */
	public List<RemoteAPIModel> getListOfRemoteApis() {
		Iterable<RemoteAPI> remotes = remoteAPIService.findAll();
		List<RemoteAPIModel> remoteAPIModels = new ArrayList<>();
		remotes.forEach(r -> remoteAPIModels.add(new RemoteAPIModel(r)));
		return remoteAPIModels;
	}

	/**
	 * Get a list of projects available on a remote API
	 *
	 * @param remoteId identifier for a remote api
	 * @return List of projects on a remote API
	 */
	public List<RemoteProjectModel> getProjectsForAPI(Long remoteId) {
		RemoteAPI api = remoteAPIService.read(remoteId);
		List<Project> projects = projectRemoteService.listProjectsForAPI(api);
		return projects.stream().map(RemoteProjectModel::new).collect(Collectors.toUnmodifiableList());
	}

	/**
	 * Add a new Synchronized Remote Project
	 *
	 * @param request Details about the remote project to synchronize
	 * @param locale  Locale of the current user session
	 * @return the result of adding the new remote project
	 */
	public Long createSynchronizedProject(CreateRemoteProjectRequest request, Locale locale) {
		try {
			Project project = projectRemoteService.read(request.getUrl());
			project.setId(null);
			if (request.getFrequency().equals(ProjectSyncFrequency.NEVER)) {
				project.getRemoteStatus().setSyncStatus(RemoteStatus.SyncStatus.UNSYNCHRONIZED);
			} else {
				project.getRemoteStatus().setSyncStatus(RemoteStatus.SyncStatus.MARKED);
			}
			project.setSyncFrequency(request.getFrequency());
			project = projectService.create(project);
			return project.getId();
		} catch (IridaOAuthException e) {
			String errorMessage;
			switch (e.getHttpStatusCode()) {
			case UNAUTHORIZED:
				errorMessage = messageSource.getMessage("IridaOAuthErrorHandler.unauthorized", new Object[] {}, locale);
				throw new IridaOAuthException(errorMessage, e.getHttpStatusCode(),
						remoteAPIService.getRemoteAPIForUrl(request.getUrl()));
			case FORBIDDEN:
				errorMessage = messageSource.getMessage("IridaOAuthErrorHandler.forbidden", new Object[] {}, locale);
				throw new IridaOAuthException(errorMessage, e.getHttpStatusCode(),
						remoteAPIService.getRemoteAPIForUrl(request.getUrl()));
			case NOT_FOUND:
				errorMessage = messageSource.getMessage("IridaOAuthErrorHandler.notFound", new Object[] {}, locale);
				throw new IridaOAuthException(errorMessage, e.getHttpStatusCode(),
						remoteAPIService.getRemoteAPIForUrl(request.getUrl()));
			default:
				errorMessage = e.getMessage();
				throw new IridaOAuthException(errorMessage, e.getHttpStatusCode(),
						remoteAPIService.getRemoteAPIForUrl(request.getUrl()));
			}
		}
	}
}
