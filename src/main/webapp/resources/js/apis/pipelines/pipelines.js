/**
 * Pipeline and workflow related API functions
 */
import axios from "axios";

/**
 * Get the IRIDA workflow description info for a workflow
 * @param workflowUUID Workflow UUID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getIridaWorkflowDescription(workflowUUID) {
  try {
    const { data } = await axios({
      method: "get",
      url: `${window.PAGE.URLS.base}pipelines/ajax/${workflowUUID}`
    });
    return { data };
  } catch (error) {
    return { error: error };
  }
}
