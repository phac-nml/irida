package ca.corefacility.bioinformatics.irida.ria.web.oauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ca.corefacility.bioinformatics.irida.service.RemoteAPIService;

@Controller
@RequestMapping("/remote_api")
@Scope("session")
public class RemoteAPIController {
	private static final Logger logger = LoggerFactory.getLogger(RemoteAPIController.class);

	public static final String CLIENTS_PAGE = "remote_apis/list";

	private final RemoteAPIService remoteAPIService;

	@Autowired
	public RemoteAPIController(RemoteAPIService remoteAPIService) {
		this.remoteAPIService = remoteAPIService;
	}

	@RequestMapping
	public String list() {

		return CLIENTS_PAGE;
	}

}
