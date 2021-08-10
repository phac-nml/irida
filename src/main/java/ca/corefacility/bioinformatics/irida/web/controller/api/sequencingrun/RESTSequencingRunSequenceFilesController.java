package ca.corefacility.bioinformatics.irida.web.controller.api.sequencingrun;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.service.SequencingObjectService;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;
import ca.corefacility.bioinformatics.irida.web.assembler.resource.ResponseResource;

import com.google.common.net.HttpHeaders;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 *
 */
@Controller
public class RESTSequencingRunSequenceFilesController {

	private SequencingRunService sequencingRunService;
	private SequencingObjectService sequencingObjectService;
	/**
	 * key used in map when adding sequencefile to miseqrun.
	 */
	public static final String SEQUENCEFILE_ID_KEY = "sequenceFileId";

	@Autowired
	public RESTSequencingRunSequenceFilesController(SequencingRunService service,
			SequencingObjectService sequencingObjectService) {
		this.sequencingRunService = service;
		this.sequencingObjectService = sequencingObjectService;
	}

	/**
	 * Add a relationship between a {@link SequencingRun} and a {@link SequenceFile}.
	 *
	 * @param sequencingrunId the id of the run to add sequence file to.
	 * @param representation  the JSON key-value pair that contains the identifier for the sequenceFile
	 * @param response        a reference to the response.
	 * @return a response indicating that the collection was modified.
	 */
	@Operation(operationId = "addSequenceFilesToSequencingRun", summary = "Link a sequencing run with a sequence file", description = "Add a relationship between a sequencing run and a sequence file.", tags = "sequencingrun")
	@RequestMapping(value = "/api/sequencingrun/{sequencingrunId}/sequenceFiles", method = RequestMethod.POST)
	public ResponseResource<SequencingRun> addSequenceFilesToSequencingRun(@PathVariable Long sequencingrunId,
			@RequestBody Map<String, String> representation, HttpServletResponse response) {
		String stringId = representation.get(SEQUENCEFILE_ID_KEY);
		long seqId = Long.parseLong(stringId);
		// first, get the SequenceFile
		SequencingObject sequencingObject = sequencingObjectService.read(seqId);
		// then, get the miseq run
		SequencingRun run = sequencingRunService.read(sequencingrunId);
		// then add the user to the project with the specified role.
		sequencingRunService.addSequencingObjectToSequencingRun(run, sequencingObject);

		Link seqFileLocation = linkTo(RESTSequencingRunController.class).slash(sequencingrunId)
				.slash("sequenceFiles")
				.slash(seqId)
				.withSelfRel();
		run.add(seqFileLocation);
		ResponseResource<SequencingRun> responseObject = new ResponseResource<>(run);
		response.addHeader(HttpHeaders.LOCATION, seqFileLocation.getHref());
		response.setStatus(HttpStatus.CREATED.value());

		return responseObject;
	}
}