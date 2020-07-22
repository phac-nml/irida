package ca.corefacility.bioinformatics.irida.ria.web.ajax.projects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}/samples")
public class ProjectSamplesAjaxController {

	@RequestMapping("/add-sample/validate")
	public ResponseEntity<NameValidationResponse> validateNewSampleName(@RequestBody NameValidationRequest request) {
		return ResponseEntity.ok(new NameValidationResponse("error", "NAME EXISIS"));
	}

	private static class NameValidationResponse {
		private final String status;
		private final String help;

		public NameValidationResponse(String status, String help) {
			this.status = status;
			this.help = help;
		}

		public String getStatus() {
			return status;
		}

		public String getHelp() {
			return help;
		}
	}

	private static class NameValidationRequest {
		private String name;

		public NameValidationRequest(String name) {
			this.name = name;
		}

		public NameValidationRequest() {
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
