package ca.corefacility.bioinformatics.irida.ria.web.projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.NGSLinkerCmdRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.NGSLinkerService;

/**
 * Controller to handle asynchronous request for the ngs-linker
 */
@RestController
@RequestMapping("/ajax/ngs-linker")
public class NGSLinkerAjaxController {
	private final NGSLinkerService linkerService;

	@Autowired
	public NGSLinkerAjaxController(NGSLinkerService linkerService) {
		this.linkerService = linkerService;
	}

	/**
	 * Get the command for the ngs-linker script with project and sample information.
	 *
	 * @param request {@link NGSLinkerCmdRequest} containing the project id and any sample ids required.
	 * @return response containing the command to run the linker.
	 */
	@RequestMapping("/cmd")
	public ResponseEntity<String> getLinkerCommand(@RequestBody NGSLinkerCmdRequest request) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(linkerService.generateLinkerCommand(request));
	}
}
