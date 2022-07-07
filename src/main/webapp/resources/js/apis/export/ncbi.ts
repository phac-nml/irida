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

export interface FullNcbiPlatforms {
  [k: string]: NcbiInstrument[];
}

/**
 * Fetch a list of all NCBI SRA Submission for a given project
 * @param projectId - Identifier for the current project
 */
export async function getProjectNCBIExports(
  projectId: number
): Promise<NcbiExportSubmissionTableModel[]> {
  return get(`/ncbi/project/${projectId}/list`);
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
  return get(`/ncbi/project/${projectId}/details/${uploadId}`);
}

/**
 * Fetch NCBI listed sequencing platforms.
 */
export async function getNCBIPlatforms(): Promise<FullNcbiPlatforms> {
  const data = await get(`/ncbi/platforms`);
  return data.platforms;
}

/**
 * Fetch list of NCBI listed sources
 */
export async function getNCBISources(): Promise<NcbiSource[]> {
  return await get(`/ncbi/sources`);
}

/**
 * Fetch list of NCBI sequencing strategies
 */
export async function getNCBIStrategies(): Promise<NcbiStrategy[]> {
  return await get(`/ncbi/strategies`);
}

/**
 * Fetch list of NCBI selections
 */
export async function getNCBISelections(): Promise<NcbiSelection[]> {
  return await get(`/ncbi/selections`);
}
