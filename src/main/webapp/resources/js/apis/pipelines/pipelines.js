/**
 * Pipeline and workflow related API functions
 */
import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

const URL = setBaseUrl(`pipelines/ajax`);

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
export const fetchIridaAnalysisWorkflows = async function() {
  var ajaxUrl = URL;
  if (window.PAGE.automatedProject !== null) {
    ajaxUrl = `${ajaxUrl}?automatedProject=${window.PAGE.automatedProject}`;
  }
  return axios.get(ajaxUrl).then(response => response.data);
};
