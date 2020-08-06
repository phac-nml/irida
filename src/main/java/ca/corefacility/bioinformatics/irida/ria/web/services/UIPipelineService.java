package ca.corefacility.bioinformatics.irida.ria.web.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import ca.corefacility.bioinformatics.irida.exceptions.IridaWorkflowNotFoundException;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.workflow.IridaWorkflow;
import ca.corefacility.bioinformatics.irida.model.workflow.analysis.type.AnalysisType;
import ca.corefacility.bioinformatics.irida.model.workflow.description.IridaWorkflowDescription;
import ca.corefacility.bioinformatics.irida.pipeline.results.AnalysisSubmissionSampleProcessor;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.UIPipelineDetailsResponse;
import ca.corefacility.bioinformatics.irida.ria.web.cart.components.Cart;
import ca.corefacility.bioinformatics.irida.service.ReferenceFileService;
import ca.corefacility.bioinformatics.irida.service.workflow.IridaWorkflowsService;

@Component
@Scope("session")
public class UIPipelineService {
	private final Cart cart;
	private final IridaWorkflowsService workflowsService;
	private final AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor;
	private final ReferenceFileService referenceFileService;

	@Autowired
	public UIPipelineService(Cart cart, IridaWorkflowsService workflowsService,
			AnalysisSubmissionSampleProcessor analysisSubmissionSampleProcessor,
			ReferenceFileService referenceFileService) {
		this.cart = cart;
		this.workflowsService = workflowsService;
		this.analysisSubmissionSampleProcessor = analysisSubmissionSampleProcessor;
		this.referenceFileService = referenceFileService;
	}

	public ResponseEntity<UIPipelineDetailsResponse> getPipelineDetails(UUID workflowId) {
		IridaWorkflow workflow;

		/*
		Need to get a list of all the projects in the cart
		 */
		List<Project> projects = cart.getProjects();

		try {
			workflow = workflowsService.getIridaWorkflow(workflowId);
			UIPipelineDetailsResponse details = new UIPipelineDetailsResponse();

			IridaWorkflowDescription workflowDescription = workflow.getWorkflowDescription();
			details.setId(workflowDescription.getId());
			details.setName(workflowDescription.getName());

			AnalysisType type = workflowDescription.getAnalysisType();

			/*
			This will let us know if the pipeline is able to write back information to the samples.  If this is possible
			then we should display a checkbox in the UI asking the user if they want this to occur or not.  User might
			not want it to happen if they are re-running an analysis or just don't want it written back into the sample
			metadata.
			 */
			boolean workflowCanUpdatesAllSamples = analysisSubmissionSampleProcessor.hasRegisteredAnalysisSampleUpdater(
					type);
			details.setCanPipelineWriteToSamples(workflowCanUpdatesAllSamples);

			/*
			REFERENCE FILES.
			Check to see if the pipeline requires reference files.  If it does then go through each project and get all
			possible reference files.
			 */
			if (workflowDescription.requiresReference()) {
				List<ReferenceFile> files = new ArrayList<>();
				projects.forEach(project -> files.addAll(referenceFileService.getReferenceFilesForProject(project)
						.stream()
						.map(Join::getObject)
						.collect(Collectors.toList())));
				details.setFiles(files);
			}

			return ResponseEntity.ok(details);

		} catch (IridaWorkflowNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(null);
		}

	}
}
