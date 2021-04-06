package ca.corefacility.bioinformatics.irida.web.controller.api.sequencingrun;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.service.ProjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResourceCollection;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.RootResource;
import ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController;
import ca.corefacility.bioinformatics.irida.web.controller.api.projects.RESTProjectsController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 *
 */
@Tag(name = "sequencingrun")
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
	 * {@inheritDoc}
	 */
	@Operation(operationId = "listAllSequencingRun", summary = "Lists all sequencing runs",
			description = "Lists all sequencing runs.", tags = "sequencingrun")
	@ApiResponse(responseCode = "200", description = "Returns a list all sequencing runs.",
			content = @Content(schema = @Schema(implementation = SequencingRunsSchema.class)))
	@RequestMapping(method = RequestMethod.GET)
	@Override
	public ModelMap listAllResources() { return super.listAllResources(); }

	/**
	 * {@inheritDoc}
	 */
	@Operation(operationId = "getSequencingRun", summary = "Find a sequencing run",
			description = "Get the sequencing run given the identifier.", tags = "sequencingrun")
	@ApiResponse(responseCode = "200", description = "Returns a sequencing run containing the requested identifier.",
			content = @Content(schema = @Schema(implementation = SequencingRunSchema.class)))
	@RequestMapping(value = "/{identifier}", method = RequestMethod.GET)
	@Override
	public ModelMap getResource(@PathVariable Long identifier) {
		return super.getResource(identifier);
	}

	/**
	 * {@inheritDoc}
	 */
	@Operation(operationId = "createSequencingRun", summary = "Create a new sequencing run",
			description = "Create a new sequencing run.", tags = "sequencingrun")
	@ApiResponse(responseCode = "200", description = "Returns the newly created sequencing run.",
			content = @Content(schema = @Schema(implementation = SequencingRunSchema.class)))
	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@Override
	public ModelMap create(@RequestBody SequencingRun resource, HttpServletResponse response) { return super.create(resource, response); }

	/**
	 * {@inheritDoc}
	 */
	@Operation(operationId = "deleteSequencingRun", summary = "Delete a sequencing run",
			description = "Delete a sequencing run given the identifier.", tags = "sequencingrun")
	@ApiResponse(responseCode = "200", description = "Returns whether the sequencing run was deleted successfully.",
			content = @Content(schema = @Schema(implementation = RootResourceSchema.class)))
	@RequestMapping(value = "/{identifier}", method = RequestMethod.DELETE)
	@Override
	public ModelMap delete(@PathVariable Long identifier) { return super.delete(identifier); }

	/**
	 * {@inheritDoc}
	 */
	@Operation(operationId = "updateSequencingRun", summary = "Update a sequencing run",
			description = "Update a sequencing run", tags = "sequencingrun")
	@ApiResponse(responseCode = "200", description = "Returns whether the sequencing run was updated successfully.",
			content = @Content(schema = @Schema(implementation = RootResourceSchema.class)))
	@RequestMapping(value = "/{identifier}", method = RequestMethod.PATCH, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@Override
	public ModelMap update(@PathVariable Long identifier, @RequestBody Map<String, Object> representation) { return super.update(identifier, representation); }


	/**
	 * Create a Sequencing run
	 *
	 * @param runType        The type of sequencing run to create
	 * @param representation the run info to create
	 * @param response       HTTP response to add info to
	 * @return the created run
	 */
	@Operation(operationId = "createSequencingRun", summary = "Create a sequencing run",
			description = "Create a sequencing run.", tags = "sequencingrun")
	@ApiResponse(responseCode = "200", description = "Returns the modified sequencing run.",
			content = @Content(schema = @Schema(implementation = SequencingRunSchema.class)))
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

	// TODO: revisit these classes that define the response schemas for openapi
	private class SequencingRunSchema {
		public SequencingRun resource;
	}

	private class SequencingRunsSchema {
		public ResourceCollection<SequencingRun> resource;
	}

	private class RootResourceSchema {
		public RootResource resource;
	}
}
