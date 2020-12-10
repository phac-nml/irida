import axios from "axios";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * Get all the reference files for the project id provided
 * @param {projectId} project for which to get reference files for.
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function getProjectReferenceFiles(projectId) {
  return await axios
    .get(setBaseUrl(`ajax/reference-files/${projectId}`))
    .then(({ data }) => data)
    .catch((error) => {
      throw new Error(error.response.data.error);
    });
}

/**
 * Downloads the reference file to the users machine
 * @param {fileId} reference file to download.
 */
export async function downloadProjectReferenceFile(fileId) {
  window.open(setBaseUrl(`ajax/reference-files/download/${fileId}`), "_blank");
}

/**
 * Remove the reference file from the project id provided
 * @param {projectId} project for which to remove reference file from
 * @param {fileId} reference file to remove
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function removeProjectReferenceFile(projectId, fileId) {
  return await axios
    .delete(
      setBaseUrl(
        `ajax/reference-files/?fileId=${fileId}&projectId=${projectId}`
      )
    )
    .then(({ data }) => data.responseMessage)
    .catch((error) => {
      throw new Error(error.response.data.error);
    });
}

/**
 * Remove the reference file from the project id provided
 * @param {projectId} project for which to remove reference file from
 * @param {formData} List of files to upload
 * @param config Configuration options for upload
 * @return {Promise<*>} `data` contains the OK response; `error` contains error information if an error occurred.
 */
export async function uploadProjectReferenceFiles(projectId, formData, config) {
  try {
    const { data } = await axios.post(
      setBaseUrl(
        `ajax/reference-files${projectId ? `?projectId=${projectId}` : ""}`
      ),
      formData,
      config
    );
    return data;
  } catch (error) {
    return Promise.reject(error.response.data.error);
  }
}
