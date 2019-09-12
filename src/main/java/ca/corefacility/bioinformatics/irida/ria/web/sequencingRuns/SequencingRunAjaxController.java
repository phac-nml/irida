package ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.ria.web.models.TableModel;
import ca.corefacility.bioinformatics.irida.ria.web.models.TableResponse;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto.SequencingRunModel;
import ca.corefacility.bioinformatics.irida.ria.web.sequencingRuns.dto.SequencingRunsListRequest;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;

@RestController
@RequestMapping("/ajax/sequencingRuns")
public class SequencingRunAjaxController {

	private SequencingRunService sequencingRunService;
	private MessageSource messageSource;

	@Autowired
	public SequencingRunAjaxController(SequencingRunService sequencingRunService,
			MessageSource messageSource) {
		this.sequencingRunService = sequencingRunService;
		this.messageSource = messageSource;
	}

	@RequestMapping("/list")
	public TableResponse listSequencingRuns(
			@RequestBody SequencingRunsListRequest sequencingRunsListRequest, Locale locale) {
		Page<SequencingRun> list = sequencingRunService.list(sequencingRunsListRequest.getCurrent(),
				sequencingRunsListRequest.getPageSize(), sequencingRunsListRequest.getSort());

		List<TableModel> runs = list.getContent()
				.stream()
				.map(s -> new SequencingRunModel(s, messageSource.getMessage(
						"sequencingruns.status." + s.getUploadStatus()
								.toString(), new Object[] {}, locale)))
				.collect(Collectors.toList());

		return new TableResponse(runs, list.getTotalElements());
	}
}
