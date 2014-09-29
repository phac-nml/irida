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
import ca.corefacility.bioinformatics.irida.model.remote.RemoteRelatedProject;
import ca.corefacility.bioinformatics.irida.service.RemoteRelatedProjectService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.remote.model.RemoteProject;

@Controller
@RequestMapping("/projects/remote")
public class RemoteProjectsController {
	private static final Logger logger = LoggerFactory.getLogger(RemoteProjectsController.class);

	private final ProjectRemoteService projectRemoteService;
	private final RemoteRelatedProjectService remoteRelatedProjectService;

	@Autowired
	public RemoteProjectsController(ProjectRemoteService projectRemoteService,
			RemoteRelatedProjectService remoteRelatedProjectService) {
		this.projectRemoteService = projectRemoteService;
		this.remoteRelatedProjectService = remoteRelatedProjectService;
	}

	@RequestMapping("/ajax/read/{remoteProjectId}")
	@ResponseBody
	public Map<String, Object> read(@PathVariable Long remoteProjectId) {
		RemoteRelatedProject remoteRelatedProject = remoteRelatedProjectService.read(remoteProjectId);
		logger.trace("Reading remote project from service " + remoteRelatedProject.getRemoteAPI());
		Map<String, Object> map = new HashMap<>();
		RemoteProject project = projectRemoteService.read(remoteRelatedProject);

		map.put("id", project.getId());
		map.put("name", project.getName());

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
