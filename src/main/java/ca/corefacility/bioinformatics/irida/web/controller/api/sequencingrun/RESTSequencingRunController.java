package ca.corefacility.bioinformatics.irida.web.controller.api.sequencingrun;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import ca.corefacility.bioinformatics.irida.model.run.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectsController;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

/**
 *
 */
@Controller
@RequestMapping(value = "/api/sequencingrun")
public class RESTSequencingRunController extends RESTGenericController<SequencingRun> {
	private static final Logger logger = LoggerFactory.getLogger(RESTSequencingRunController.class);

	public static final String MISEQ_REL = "sequencingRun/miseq";

	/**
	 * Default constructor. Should not be used.
	 */
	protected RESTSequencingRunController() {
	}

	/**
	 * Constructor for {@link RESTProjectsController}, requires a reference to a
	 * {@link ProjectService}.
	 *
	 * @param service
	 *            the {@link SequencingRunService} to be used by this
	 *            controller.
	 */
	@Autowired
	public RESTSequencingRunController(SequencingRunService service) {
		super(service, SequencingRun.class);

	}

	/**
	 * Exception Handler for {InvalidDefinitionException} this basically
	 * prevents create endpoint for SequencingRun from existing. Since
	 * SequencingRun is an Abstract class it does not have a valid deserializer
	 * so jackson throws a InvalidDefinitionException. We handle that exception
	 * and return a status code of 405 (Method not allowed).
	 */
	@ExceptionHandler(InvalidDefinitionException.class)
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public void handleInvalidDefinitionException() { }

	/**
	 * create a miseq run
	 *
	 * @param representation the run info to create
	 * @param response       http response to add info to
	 * @return the created run
	 */
	@RequestMapping(value = "/miseqrun", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE })
	public ModelMap createMiseqRun(@RequestBody MiseqRun representation, HttpServletResponse response) {
		logger.trace("creating miseq run");
		return create(representation, response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Collection<Link> constructCollectionResourceLinks(ResourceCollection<SequencingRun> list) {
		Collection<Link> links = super.constructCollectionResourceLinks(list);
		links.add(linkTo(methodOn(RESTSequencingRunController.class).createMiseqRun(null, null)).withRel(MISEQ_REL));
		return links;
	}

}
