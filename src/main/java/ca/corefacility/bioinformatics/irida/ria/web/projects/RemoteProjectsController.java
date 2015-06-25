package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.service.RemoteRelatedProjectService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

@Controller
@RequestMapping("/projects/remote")
public class RemoteProjectsController {
	private static final Logger logger = LoggerFactory.getLogger(RemoteProjectsController.class);

	public static final String REMOTE_PROJECT_VIEW = "remote/project";

	private final ProjectRemoteService projectRemoteService;
	private final RemoteRelatedProjectService remoteRelatedProjectService;

	@Autowired
	public RemoteProjectsController(ProjectRemoteService projectRemoteService,
			RemoteRelatedProjectService remoteRelatedProjectService) {
		this.projectRemoteService = projectRemoteService;
		this.remoteRelatedProjectService = remoteRelatedProjectService;
	}

	/**
	 * Read a remote project by its internal {@link RemoteRelatedProject} ID
	 * 
	 * @param remoteProjectId
	 *            the internal ID of the {@link RemoteRelatedProject}
	 * @return A map containing info about the remote projcet read from the
	 *         remote API
	 */
	@RequestMapping("/ajax/related/{remoteProjectId}")
	@ResponseBody
	public Map<String, Object> readRemoteRelatedProject(@PathVariable Long remoteProjectId) {
		RemoteRelatedProject remoteRelatedProject = remoteRelatedProjectService.read(remoteProjectId);
		logger.trace("Reading remote project from service " + remoteRelatedProject.getRemoteAPI());
		Map<String, Object> map = new HashMap<>();
		Project project = projectRemoteService.read(remoteRelatedProject);

		map.put("id", project.getId());
		map.put("name", project.getName());

		return map;
	}

	/**
	 * Handle an {@link IridaOAuthException} and return a status message
	 * 
	 * @param ex
	 *            The {@link IridaOAuthException} thrown
	 * @return an invalid_token string
	 */
	@ExceptionHandler(IridaOAuthException.class)
	@ResponseBody
	public Map<String, Object> handleOAuthException(IridaOAuthException ex) {
		return ImmutableMap.of("status", "invalid_token");
	}

	/**
	 * Handle a {@link HttpClientErrorException} and if the response was HTTP403
	 * respond with a message rather than throwing an errror
	 * 
	 * @param e
	 *            the thrown HttpClientErrorException
	 * @return "forbidden" for a 403 error, rethrow the exception otherwise
	 */
	@ExceptionHandler(HttpClientErrorException.class)
	@ResponseBody
	public Map<String, Object> handleForbiddenResponse(HttpClientErrorException e) {
		if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
			return ImmutableMap.of("status", "forbidden");
		} else {
			throw e;
		}
	}
}
