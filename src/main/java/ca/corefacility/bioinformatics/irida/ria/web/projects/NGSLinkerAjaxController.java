package ca.corefacility.bioinformatics.irida.ria.web.projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.ria.web.projects.dto.LinkerCmdRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.NGSLinkerService;

@RestController
@RequestMapping("/ajax/ngs-linker")
public class NGSLinkerAjaxController {
	private final NGSLinkerService linkerService;


	@Autowired
	public NGSLinkerAjaxController(NGSLinkerService linkerService) {
		this.linkerService = linkerService;
	}

	@RequestMapping("/cmd")
	public ResponseEntity<String> getLinkerCommand(@RequestBody LinkerCmdRequest request) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(linkerService.generateLinkerCommand(request));
	}
}
