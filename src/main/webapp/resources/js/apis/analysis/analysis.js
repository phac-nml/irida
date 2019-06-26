/**
 * Analysis related API functions
 */
import axios from "axios";

export async function updateAnalysisEmailPipelineResult(
  submissionId,
  emailPipelineResult
) {
  axios.patch(`${window.TL.BASE_URL}analysis/ajax/updateemailpipelineresult/`, {
    analysisSubmissionId: submissionId,
    emailPipelineResult: emailPipelineResult
  });
}

export async function updateAnalysisName(submissionId, analysisName) {
  axios.patch(`${window.TL.BASE_URL}analysis/ajax/updateanalysisname/`, {
    analysisSubmissionId: submissionId,
    analysisName: analysisName
  });
}

export async function deleteAnalysis(submissionId) {
  const res = await axios.delete(
    `${window.TL.BASE_URL}analysis/ajax/delete/${submissionId}`
  );
  return res.data;
}

/**
 * Get all single sample analysis output file info for the principal user.
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getPrincipalUserSingleSampleAnalysisOutputs() {
  try {
    const { data } = await axios.get(
      `${window.PAGE.URLS.base}analysis/ajax/user/analysis-outputs`
    );
    return { data };
  } catch (error) {
    return { error: error };
  }
}

/**
 * Get all shared single sample analysis output file info for a project.
 * @param projectId Project ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getProjectSharedSingleSampleAnalysisOutputs(projectId) {
  try {
    const { data } = await axios.get(
      `${
        window.TL.BASE_URL
      }analysis/ajax/project/${projectId}/shared-analysis-outputs`
    );
    return { data };
  } catch (error) {
    return { error: error };
  }
}

/**
 * Get all automated single sample analysis output file info for a project.
 * @param {number} projectId Project ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getProjectAutomatedSingleSampleAnalysisOutputs(
  projectId
) {
  try {
    const { data } = await axios.get(
      `${
        window.PAGE.URLS.base
      }analysis/ajax/project/${projectId}/automated-analysis-outputs`
    );
    return { data };
  } catch (error) {
    return { error: error };
  }
}

/**
 * Prepare download of multiple analysis output files using a list of analysis output file info object.
 * @param {Array<Object>} outputs List of analysis output file info to prepare download of.
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function prepareAnalysisOutputsDownload(outputs) {
  try {
    const { data } = await axios({
      method: "post",
      url: `${window.PAGE.URLS.base}analysis/ajax/download/prepare`,
      data: outputs
    });
    return { data };
  } catch (error) {
    return { error: error };
  }
}
