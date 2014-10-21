package ca.corefacility.bioinformatics.irida.ria.web.projects;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteProject;
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.ria.utilities.CacheObject;
import ca.corefacility.bioinformatics.irida.ria.utilities.RemoteObjectCache;
import ca.corefacility.bioinformatics.irida.service.RemoteRelatedProjectService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;

@Controller
@RequestMapping("/projects/remote")
public class RemoteProjectsController {
	private static final Logger logger = LoggerFactory.getLogger(RemoteProjectsController.class);

	public static final String REMOTE_PROJECT_VIEW = "remote/project";

	private final ProjectRemoteService projectRemoteService;
	private final RemoteRelatedProjectService remoteRelatedProjectService;
	private RemoteObjectCache<RemoteProject> projectCache;

	@Autowired
	public RemoteProjectsController(ProjectRemoteService projectRemoteService,
			RemoteRelatedProjectService remoteRelatedProjectService, RemoteObjectCache<RemoteProject> projectCache) {
		this.projectRemoteService = projectRemoteService;
		this.remoteRelatedProjectService = remoteRelatedProjectService;
		this.projectCache = projectCache;
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
		RemoteProject project = projectRemoteService.read(remoteRelatedProject);
		
		Integer cacheId = projectCache.addResource(project, remoteRelatedProject.getRemoteAPI());

		map.put("id", project.getId());
		map.put("name", project.getName());
		map.put("remoteId", cacheId);

		return map;
	}

	@RequestMapping("/{projectCacheId}")
	public String readRemoteProject(@PathVariable Integer projectCacheId, Model model) {
		CacheObject<RemoteProject> readResource = projectCache.readResource(projectCacheId);
		model.addAttribute("project", readResource.getResource());
		model.addAttribute("api", readResource.getAPI());

		return REMOTE_PROJECT_VIEW;
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
	public String handleOAuthException(IridaOAuthException ex) {
		return "invalid_token";
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
	public String handleForbiddenResponse(HttpClientErrorException e) {
		if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
			return "forbidden";
		} else {
			throw e;
		}
	}
}
