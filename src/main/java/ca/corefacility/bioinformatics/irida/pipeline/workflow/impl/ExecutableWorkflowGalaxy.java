package ca.corefacility.bioinformatics.irida.pipeline.workflow.impl;

public class ExecutableWorkflowGalaxy
{
	private String jsonString = null;
	
	public ExecutableWorkflowGalaxy(String workflowString)
	{
		this.jsonString = workflowString;
	}

	public String getJson()
	{
		return jsonString;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((jsonString == null) ? 0 : jsonString.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExecutableWorkflowGalaxy other = (ExecutableWorkflowGalaxy) obj;
		if (jsonString == null)
		{
			if (other.jsonString != null)
				return false;
		} else if (!jsonString.equals(other.jsonString))
			return false;
		return true;
	}
}
