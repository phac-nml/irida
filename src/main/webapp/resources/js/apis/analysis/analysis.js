/**
 * Analysis related API functions
 */
import axios from "axios";

// Ajax URL for Analysis
const URL = `${window.TL.BASE_URL}ajax/analysis`;
const BASE_URL = `${window.TL.BASE_URL}ajax/analyses`;

/*
 * Get all the data required for the analysis -> details page.
 * @param {number} submissionId Submission ID
 * @return {Promise<*>} `data` contains the OK response and the details map;
 *                      `error` contains error information if an error occurred.
 */
export async function getVariablesForDetails(submissionId) {
  const { data } = await axios.get(`${URL}/details/${submissionId}`);
  return data;
}

/*
 * Get analysis input files
 * @param {number} submissionId Submission ID
 * @return {Promise<*>} `data` contains the OK response and input files data;
 *                      `error` contains error information if an error occurred.
 */
export async function getAnalysisInputFiles(submissionId) {
  try {
    const { data } = await axios.get(`${URL}/inputs/${submissionId}`);
    return data;
  } catch (error) {
    return { samples: [], referenceFile: null };
  }
}

/*
 * Updates user preference to either receive or not receive an email on
 * analysis error or completion.
 * @param {number} submissionId Submission ID
 * @param {boolean} emailPipelineResult True or False
 * @return {Promise<*>} `data` contains the OK response; error` contains error information if an error occurred.
 */
export async function updateAnalysisEmailPipelineResult(params) {
  try {
    const { data } = await axios.patch(`${URL}/update-email-pipeline-result`, {
      analysisSubmissionId: params.submissionId,
      emailPipelineResult: params.emailPipelineResult
    });
    return data.message;
  } catch (error) {
    return { text: error.response.data.message, type: "error" };
  }
}

/*
 * Updates analysis name and/or analysis priority.
 * @param {number} submissionId Submission ID
 * @param {string} analysisName Name of analysis
 * @param {string} priority [LOW, MEDIUM, HIGH]
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function updateAnalysis(params) {
  try {
    const { data } = await axios.patch(`${URL}/update-analysis/`, {
      analysisSubmissionId: params.submissionId,
      analysisName: params.analysisName,
      priority: params.priority
    });
    return data.message;
  } catch (error) {
    return { text: error.response.data.message, type: "error" };
  }
}

/*
 * Deletes the analysis.
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function deleteAnalysis(submissionId) {
  const { data } = await axios.delete(`${URL}/delete/${submissionId}`);
  return data;
}

/*
 * Gets all projects that this analysis can be shared with.
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getSharedProjects(submissionId) {
  const { data } = await axios.get(`${URL}/${submissionId}/share`);
  return data;
}

/*
 * Updates whether or not an analysis is shared with a project.
 * @param {number} submissionID Submission ID
 * @param {number} projectID Project ID
 * @param {boolean} shareStatus True of False
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function updateSharedProject(params) {
  const { data } = await axios.post(`${URL}/${params.submissionId}/share`, {
    projectId: params.projectId,
    shareStatus: params.shareStatus
  });
  return data;
}

/*
 * Saves analysis to related samples.
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function saveToRelatedSamples(submissionId) {
  try {
    const { data } = await axios.post(`${URL}/${submissionId}/save-results`);
    return data.message;
  } catch (error) {
    return { text: error.response.data.message, type: "error" };
  }
}

/**
 * Get the job errors.
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response.
 */
export async function getJobErrors(submissionId) {
  try {
    const { data } = await axios.get(`${URL}/${submissionId}/job-errors`);
    return data;
  } catch (error) {
    return { error: error };
  }
}

/**
 * Get the sistr results.
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getSistrResults(submissionId) {
  try {
    const { data } = await axios.get(`${URL}/sistr/${submissionId}`);
    return data;
  } catch (error) {
    return { error: error };
  }
}

/**
 * Get the bio hansel results.
 * @param {number} submissionID Submission ID
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getOutputInfo(submissionId) {
  try {
    const res = await axios.get(`${URL}/${submissionId}/outputs`);
    return res.data;
  } catch (error) {
    return { error: error };
  }
}

export async function getBioHanselResults(outputFileInfoObject) {
  try {
    const res = await axios.get(
      `${URL}/${outputFileInfoObject.submissionId}/outputs/${
        outputFileInfoObject.fileId
      }
            ?${$.param({
              seek: outputFileInfoObject.seek,
              chunk: outputFileInfoObject.chunk
            })}`
    );
    return res.data;
  } catch (error) {
    return { error: error };
  }
}
/**
 * Get all single sample analysis output file info for the principal user.
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getPrincipalUserSingleSampleAnalysisOutputs() {
  try {
    const { data } = await axios.get(`${BASE_URL}user/analysis-outputs`);
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
      `${URL}/project/${projectId}/shared-analysis-outputs`
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
      `${URL}/project/${projectId}/automated-analysis-outputs`
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
      url: `${URL}/download/prepare`,
      data: outputs
    });
    return { data };
  } catch (error) {
    return { error: error };
  }
}

export async function fetchAllPipelinesStates() {
  return axios.get(`${BASE_URL}/states`).then(response => response.data);
}

export async function fetchAllPipelinesTypes() {
  return axios.get(`${BASE_URL}/types`).then(response => response.data);
}

export async function deleteAnalysisSubmissions({ ids }) {
  return axios.delete(`${BASE_URL}/delete?ids=${ids.join(",")}`);
}
