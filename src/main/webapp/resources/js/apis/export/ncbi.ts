import {
  NcbiInstrument,
  NcbiSelection,
  NcbiSource,
  NcbiStrategy,
  NcbiSubmission,
  UserMinimal,
} from "../../types/irida";
import { ExportUploadState } from "../../types/irida/export/ExportUploadState";
import { setBaseUrl } from "../../utilities/url-utilities";
import { get } from "../requests";

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
 * Fetch a list of all NCBI SRA Submissions for a given project
 * @param projectId - Identifier for the current project
 */
export async function getProjectNCBIExports(
  projectId: number
): Promise<NcbiExportSubmissionTableModel[]> {
  return get(setBaseUrl(`/ncbi/project/${projectId}/list`));
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
  return get(setBaseUrl(`/ncbi/project/${projectId}/details/${uploadId}`));
}

/**
 * Fetch NCBI listed sequencing platforms.
 */
export async function getNCBIPlatforms(): Promise<FullNcbiPlatforms> {
  return await get(setBaseUrl(`/ncbi/platforms`));
}

/**
 * Fetch a list of NCBI listed sources
 */
export async function getNCBISources(): Promise<NcbiSource[]> {
  return await get(setBaseUrl(`/ncbi/sources`));
}

/**
 * Fetch a list of NCBI sequencing strategies
 */
export async function getNCBIStrategies(): Promise<NcbiStrategy[]> {
  return await get(setBaseUrl(`/ncbi/strategies`));
}

/**
 * Fetch a list of NCBI selections
 */
export async function getNCBISelections(): Promise<NcbiSelection[]> {
  return await get(setBaseUrl(`/ncbi/selections`));
}
