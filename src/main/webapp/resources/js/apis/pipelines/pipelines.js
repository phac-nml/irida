/**
 * Pipeline and workflow related API functions
 */
import axios from "axios";

const URL = `${window.TL.BASE_URL}pipelines/ajax/`;

/**
 * Get the IRIDA workflow description info for a workflow
 * @param workflowUUID Workflow UUID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getIridaWorkflowDescription(workflowUUID) {
  try {
    const { data } = await axios({
      method: "get",
      url: `${URL}/${workflowUUID}`
    });
    return { data };
  } catch (error) {
    return { error };
  }
}

/**
 * Get a listing of all Pipelines in IRIDA.
 * @returns {Promise<AxiosResponse<any> | never>}
 */
export const fetchIridaAnalysisWorkflows = async () =>
  axios.get(URL).then(response => response.data);
