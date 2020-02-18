package ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.ria.web.models.tables.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto.SequencingRunModel;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto.SequencingRunsListRequest;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;

/**
 * Controller to handle AJAX requests for sequencing run data
 */
@RestController
@RequestMapping("/ajax/sequencingRuns")
public class SequencingRunAjaxController {

	private final SequencingRunService sequencingRunService;
	private final MessageSource messageSource;

	@Autowired
	public SequencingRunAjaxController(SequencingRunService sequencingRunService, MessageSource messageSource) {
		this.sequencingRunService = sequencingRunService;
		this.messageSource = messageSource;
	}

	/**
	 * Get the current page contents for a table displaying sequencing runs.
	 *
	 * @param sequencingRunsListRequest {@link SequencingRunsListRequest} specifies what data is required.
	 * @param locale                    {@link Locale}
	 * @return {@link TableResponse}
	 */
	@RequestMapping("/list")
	public TableResponse<SequencingRunModel> listSequencingRuns(@RequestBody SequencingRunsListRequest sequencingRunsListRequest, Locale locale) {
		Page<SequencingRun> list = sequencingRunService.list(sequencingRunsListRequest.getCurrent(),
				sequencingRunsListRequest.getPageSize(), sequencingRunsListRequest.getSort());

		List<SequencingRunModel> runs = new ArrayList<>();
		for (SequencingRun run : list.getContent()) {
			runs.add(new SequencingRunModel(run, messageSource.getMessage(
					"sequencingruns.status." + run.getUploadStatus()
							.toString(), new Object[] {}, locale)));
		}

		return new TableResponse<>(runs, list.getTotalElements());
	}
}
