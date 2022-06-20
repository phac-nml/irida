import axios from "axios";
import { NcbiSubmission, UserMinimal } from "../../types/irida";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ExportUploadState } from "../../types/irida/ExportUpoadState";

const BASE_URL = setBaseUrl(`/ajax/ncbi`);

export interface NcbiExportSubmissionTableModel {
  exportedSamples: number;
  state: ExportUploadState;
  submitter: UserMinimal;
  bioProjectId: string;
}

/**
 * Fetch a list of all NCBI SRA Submission for a given project
 * @param projectId - Identifier for the current project
 */
export async function getProjectNCBIExports(
  projectId: number
): Promise<NcbiExportSubmissionTableModel[]> {
  try {
    const { data } = await axios.get(`${BASE_URL}/project/${projectId}/list`);
    return data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error.response) {
        return Promise.reject(error.response.data.error);
      } else {
        return Promise.reject(error.message);
      }
    } else {
      return Promise.reject("An unexpected error occurred");
    }
  }
}

/**
 * Get the details for an NCBI SRA Submission
 * @param projectId - Identifier for the current project
 * @param uploadId - Identifier for the submission
 */
export async function getNcbiSubmission(
  projectId: number,
  uploadId: number
): Promise<NcbiSubmission> {
  try {
    const { data } = await axios.get(
      `${BASE_URL}/project/${projectId}/details/${uploadId}`
    );
    return data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      if (error.response) {
        return Promise.reject(error.response.data.error);
      } else {
        return Promise.reject(error.message);
      }
    } else {
      return Promise.reject("An unexpected error occurred");
    }
  }
}
