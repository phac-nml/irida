package ca.corefacility.bioinformatics.irida.ria.web.ajax.export;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.NcbiExportSubmissionTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.services.UINcbiService;

/**
 * Spring Ajax Controller to handle NCBI requests.
 */
@RestController
@RequestMapping("/ajax/ncbi")
public class NCBIAjaxController {
	private final UINcbiService service;

	@Autowired
	public NCBIAjaxController(UINcbiService service) {
		this.service = service;
	}

	@RequestMapping("/project/{projectId}/list")
	public ResponseEntity<List<NcbiExportSubmissionTableModel>> getNCBIExportsForProject(@PathVariable Long projectId) {
		return ResponseEntity.ok(service.getNCBIExportsForProject(projectId));
	}

	@RequestMapping("/list")
	public ResponseEntity<List<NcbiExportSubmissionTableModel>> getNCBIExportsForAdmin(@RequestBody TableRequest request) {
		return ResponseEntity.ok(service.getNCBIExportsForAdmin(request));
	}
}
