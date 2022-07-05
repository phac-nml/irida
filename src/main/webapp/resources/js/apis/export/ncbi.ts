import { NcbiSubmission, UserMinimal } from "../../types/irida";
import { ExportUploadState } from "../../types/irida/ExportUpoadState";
import { get } from "../requests";
import {
  export_ncbi_details_route,
  export_ncbi_project_route,
} from "../routes";

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
    return await get(export_ncbi_project_route({ projectId }));
  } catch (error) {
    return Promise.reject(i18n("generic.ajax-unexpected-error"));
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
    return await get(export_ncbi_details_route({ projectId, uploadId }));
  } catch (error) {
    return Promise.reject(i18n("generic.ajax-unexpected-error"));
  }
}
