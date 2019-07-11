/**
 * Analysis related API functions
 */
import axios from "axios";

/*
 * Get all the data required for the analysis -> details page.
 * @param {number} submissionId Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getVariablesForDetails(submissionId) {
  const res = await axios.get(
    `${window.TL.BASE_URL}analysis/ajax/getDataForDetailsTab/${submissionId}`
  );
  return res;
}

/*
 * Get analysis input files
 * @param {number} submissionId Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getAnalysisInputFiles(submissionId) {
  const res = await axios.get(
    `${window.TL.BASE_URL}analysis/ajax/getAnalysisInputFiles/${submissionId}`
  );
  return res;
}

/*
 * Updates user preference to either receive or not receive an email on
 * analysis error or completion.
 * @param {number} submissionId Submission ID
 * @param {boolean} emailPipelineResult True or False
 */
export async function updateAnalysisEmailPipelineResult(
  submissionId,
  emailPipelineResult
) {
  const res = await axios.patch(
    `${window.TL.BASE_URL}analysis/ajax/updateemailpipelineresult/`,
    {
      analysisSubmissionId: submissionId,
      emailPipelineResult: emailPipelineResult
    }
  );
  return res.data;
}

/*
 * Updates analysis name and/or analysis priority.
 * @param {number} submissionId Submission ID
 * @param {string} analysisName Name of analysis
 * @param {string} priority [LOW, MEDIUM, HIGH]
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */

export async function updateAnalysis(submissionId, analysisName, priority) {
  const res = await axios.patch(
    `${window.TL.BASE_URL}analysis/ajax/updateanalysis/`,
    {
      analysisSubmissionId: submissionId,
      analysisName: analysisName,
      priority: priority
    }
  );
  return res.data;
}

/*
 * Deletes the analysis.
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function deleteAnalysis(submissionId) {
  const res = await axios.delete(
    `${window.TL.BASE_URL}analysis/ajax/delete/${submissionId}`
  );
  return res.data;
}

/*
 * Gets all projects that this analysis can be shared with.
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getSharedProjects(submissionId) {
  const res = await axios.get(
    `${window.TL.BASE_URL}analysis/ajax/${submissionId}/share`
  );
  return res;
}

/*
 * Updates whether or not an analysis is shared with a project.
 * @param {number} submissionID Submission ID
 * @param {number} projectID Project ID
 * @param {boolean} shareStatus True of False
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function updateSharedProjects(
  submissionId,
  projectId,
  shareStatus
) {
  const res = await axios.post(
    `${window.TL.BASE_URL}analysis/ajax/${submissionId}/share`,
    {
      projectId: projectId,
      shareStatus: shareStatus
    }
  );
  return res.data;
}

/*
 * Saves analysis to related samples.
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function saveToRelatedSamples(submissionId) {
  const res = await axios.post(
    `${window.TL.BASE_URL}analysis/ajax/${submissionId}/save-results`
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
