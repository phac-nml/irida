package ca.corefacility.bioinformatics.irida.ria.web.linelist;

import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.LineListTableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableRequest;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.services.UILineListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This controller is responsible for AJAX handling in the linelist page, which displays samples along with their
 * metadata.
 */
@RestController
@RequestMapping("/ajax/linelist")
public class LineListAjaxController {
	private final UILineListService uiLineListService;

	@Autowired
	public LineListAjaxController(UILineListService uiLineListService) {
		this.uiLineListService = uiLineListService;
	}

	/**
	 * Get a {@link Page} of {@link Sample}s and their metadata.
	 *
	 * @param projectId {@link Long} identifier for the {@link Project}
	 * @param request   {@link TableRequest}
	 * @return a page of Samples and their metadata.
	 */
	@PostMapping(value = "/entries")
	public ResponseEntity<TableResponse<LineListTableModel>> getProjectSamplesMetadataEntries(
			@RequestParam Long projectId, @RequestBody TableRequest request) {
		return ResponseEntity.ok(uiLineListService.getLineListEntries(projectId, request));
	}
}
