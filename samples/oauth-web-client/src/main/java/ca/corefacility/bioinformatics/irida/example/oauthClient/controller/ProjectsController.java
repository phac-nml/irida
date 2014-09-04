package ca.corefacility.bioinformatics.irida.example.oauthClient.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;
import ca.corefacility.bioinformatics.irida.service.remote.ProjectRemoteService;
import ca.corefacility.bioinformatics.irida.service.remote.model.RemoteProject;

@Controller
@Scope("session")
public class ProjectsController {
	private static final Logger logger = LoggerFactory.getLogger(ProjectsController.class);

	// the repository we're communicating with
	private ProjectRemoteService repo;
	// a service to read information about remote apis
	private RemoteAPIService apiService;

	@Autowired
	public ProjectsController(ProjectRemoteService repo, RemoteAPIService apiRepo) {
		this.repo = repo;
		this.apiService = apiRepo;
	}

	/**
	 * Get a list of projects for a given service
	 * 
	 * @param apiId
	 *            the ID of the {@link RemoteAPI} we're talking to
	 * @return A model for a list of projects
	 * @throws Exception
	 */
	@RequestMapping("/remote/{remoteId}/projects")
	public ModelAndView getData(@PathVariable("remoteId") Long apiId) {

		RemoteAPI remoteAPI = apiService.read(apiId);

		logger.debug("Listing from " + remoteAPI);

		List<RemoteProject> list = repo.list(remoteAPI);

		ModelAndView modelAndView = new ModelAndView("data");
		modelAndView.addObject("service", remoteAPI);
		modelAndView.addObject("data", list);
		modelAndView.addObject("apiId", apiId);
		return modelAndView;
	}

	/**
	 * Get a specific project
	 * 
	 * @param id
	 *            the ID of the project to read
	 * @param apiId
	 *            the id of the API to communicate with
	 * @return a modlel for a project
	 */
	@RequestMapping("/remote/{remoteId}/projects/{id}")
	public ModelAndView readData(@PathVariable("id") Long id, @PathVariable("remoteId") Long apiId) {

		RemoteAPI remoteAPI = apiService.read(apiId);

		logger.debug("reading " + id + " from " + remoteAPI);
		RemoteProject read = repo.read(id, remoteAPI);

		ModelAndView modelAndView = new ModelAndView("onedata");
		modelAndView.addObject("service", remoteAPI);
		modelAndView.addObject("data", read);
		modelAndView.addObject("apiId", id);
		return modelAndView;
	}
}
