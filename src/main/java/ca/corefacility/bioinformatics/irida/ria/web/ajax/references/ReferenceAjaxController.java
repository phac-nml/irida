package ca.corefacility.bioinformatics.irida.ria.web.ajax.references;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxErrorResponse;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.ajax.AjaxResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UIReferenceService;

@RestController
@RequestMapping("/ajax/references")
public class ReferenceAjaxController {
	private final UIReferenceService service;

	@Autowired
	public ReferenceAjaxController(UIReferenceService service) {
		this.service = service;
	}

	@RequestMapping("/add")
	public ResponseEntity<AjaxResponse> addReferenceFile(@RequestParam(value="file")MultipartFile file) {
		try {
			return ResponseEntity.ok(service.addReferenceFile(file));
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new AjaxErrorResponse("__CANNOT UPLOAD FILE___"));
		}
	}
}
