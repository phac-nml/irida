package ca.corefacility.bioinformatics.irida.ria.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.ImmutableMap;

import ca.corefacility.bioinformatics.irida.model.run.MiseqRun;
import ca.corefacility.bioinformatics.irida.model.run.SequencingRun;
import ca.corefacility.bioinformatics.irida.service.SequencingRunService;

@Controller
@RequestMapping("/sequencingRuns")
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_MANAGER')")
public class SequencingRunController {
	private static final Map<Class<? extends SequencingRun>, String> SEQUENCER_TYPES = ImmutableMap.of(MiseqRun.class, "MiSeq");
	public static final String LIST_VIEW = "sequencingRuns/list";
	@Autowired
	SequencingRunService sequencingRunService;

	@RequestMapping
	public String getListPage() {
		return LIST_VIEW;
	}

	@RequestMapping("/ajax/list")
	@ResponseBody
	public Collection<Map<String,Object>> getSequencingRuns() {
		return getRunsMap(sequencingRunService.findAll());
	}
	
	private Collection<Map<String,Object>> getRunsMap(Iterable<SequencingRun> runs){
		List<Map<String,Object>> runsList = new ArrayList<>();
		for(SequencingRun run : runs){
			Map<String,Object> runMap = new HashMap<>();
			runMap.put("id", run.getId());
			runMap.put("createdDate", run.getCreatedDate());
			runMap.put("sequencerType", SEQUENCER_TYPES.get(run.getClass()));
			
			runsList.add(runMap);
		}
		return runsList;
	}
}
