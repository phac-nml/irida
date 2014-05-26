package ca.corefacility.bioinformatics.irida.example.oauthClient.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ca.corefacility.bioinformatics.irida.model.RemoteAPI;
import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;

@Controller
public class HomeController {

	@Autowired
	RemoteAPIService remoteService;

	@RequestMapping(value = "/")
	public ModelAndView test(HttpServletResponse response) throws IOException {
		return new ModelAndView("home");
	}

	@RequestMapping(value = "/choose")
	public ModelAndView choose() {
		Iterable<RemoteAPI> list = remoteService.findAll();
		ModelAndView modelAndView = new ModelAndView("choose");
		modelAndView.addObject("apiList", list);

		return modelAndView;
	}
}
