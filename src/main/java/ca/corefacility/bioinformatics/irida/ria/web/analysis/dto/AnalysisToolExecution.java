package ca.corefacility.bioinformatics.irida.ria.web.analysis.dto;

import java.util.ArrayList;

/**
 * Used as a response for encapsulating the tools used in the provenance
 * as well as the execution parameters for these tools
 */

public class AnalysisToolExecution {
	private String toolName;
	private ArrayList<AnalysisToolExecutionParameters> executionParameters;
	private ArrayList<AnalysisToolExecution> previousExecutionTools;

	public AnalysisToolExecution(){
	}

	public AnalysisToolExecution(String toolName, ArrayList<AnalysisToolExecutionParameters> executionParameters, ArrayList<AnalysisToolExecution> previousExecutionTools ){
		this.toolName=toolName;
		this.executionParameters=executionParameters;
		this.previousExecutionTools=previousExecutionTools;
	}

	public String getToolName() {
		return toolName;
	}

	public void setToolName(String toolName) {
		this.toolName = toolName;
	}

	public ArrayList<AnalysisToolExecutionParameters> getExecutionParameters() {
		return executionParameters;
	}

	public void setExecutionParameters(ArrayList<AnalysisToolExecutionParameters> executionParameters) {
		this.executionParameters = executionParameters;
	}

	public ArrayList<AnalysisToolExecution> getPreviousExecutionTools() {
		return previousExecutionTools;
	}

	public void setPreviousExecutionTools(ArrayList<AnalysisToolExecution> previousExecutionTools) {
		this.previousExecutionTools = previousExecutionTools;
	}
}
