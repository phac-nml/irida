package ca.corefacility.bioinformatics.irida.web.controller.api.sequencingrun;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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
	 * Constructor for {@link RESTProjectsController}, requires a reference to a {@link ProjectService}.
	 *
	 * @param service the {@link SequencingRunService} to be used by this controller.
	 */
	@Autowired
	public RESTSequencingRunController(SequencingRunService service) {
		super(service, SequencingRun.class);

	}

	/**
	 * Create a Sequencing run
	 *
	 * @param runType        The type of sequencing run to create
	 * @param representation the run info to create
	 * @param response       HTTP response to add info to
	 * @return the created run
	 */
	@RequestMapping(value = "/{runType}", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ModelMap createSequencingRun(@PathVariable String runType, @RequestBody SequencingRun representation,
			HttpServletResponse response) {
		logger.trace("creating sequencing run");

		//Legacy for ensuring old uploaders pointing to /miseqrun get a sequencer type of 'miseq'
		if (runType.equals("miseqrun")) {
			runType = "miseq";
		}

		representation.setSequencerType(runType);
		return create(representation, response);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Collection<Link> constructCollectionResourceLinks(ResourceCollection<SequencingRun> list) {
		Collection<Link> links = super.constructCollectionResourceLinks(list);

		//Legacy for ensuring old uploaders pointing to /miseqrun get a sequencer type of 'miseq'
		links.add(
				linkTo(methodOn(RESTSequencingRunController.class).createSequencingRun("miseqrun", null, null)).withRel(
						MISEQ_REL));

		return links;
	}

}
