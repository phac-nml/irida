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

import ca.corefacility.bioinformatics.irida.exceptions.IridaOAuthException;
import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.remote.model.RemoteProject;

@Controller
@RequestMapping("/projects/remote")
public class RemoteProjectsController {
	private static final Logger logger = LoggerFactory.getLogger(RemoteProjectsController.class);

	private final ProjectRemoteService projectRemoteService;
	private final RemoteAPIService remoteAPIService;

	@Autowired
	public RemoteProjectsController(ProjectRemoteService projectRemoteService, RemoteAPIService remoteAPIService) {
		this.projectRemoteService = projectRemoteService;
		this.remoteAPIService = remoteAPIService;
	}

	@RequestMapping("/ajax/api/{remoteApiId}/project/{projectId}")
	@ResponseBody
	public Map<String, Object> read(@PathVariable Long remoteApiId, @PathVariable Long projectId) {
		RemoteAPI api = remoteAPIService.read(remoteApiId);
		Map<String, Object> map = new HashMap<>();
		try {
			RemoteProject project = projectRemoteService.read(projectId, api);

			map.put("id", project.getId());
			map.put("name", project.getName());
		} catch (HttpClientErrorException e) {
			logger.debug("error", e);
			throw e;
		}

		return map;
	}

	@ExceptionHandler(IridaOAuthException.class)
	@ResponseBody
	public String handleOAuthException() {
		return "invalid_token";
	}

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
