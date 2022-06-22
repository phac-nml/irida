import {
  NcbiInstrument,
  NcbiPlatform, NcbiSelection,
  NcbiSource,
  NcbiStrategy,
  NcbiSubmission,
  UserMinimal,
} from "../../types/irida";
import { ExportUploadState } from "../../types/irida/export/ExportUploadState";
import { get } from "../axios-default";

export interface NcbiExportSubmissionTableModel {
  exportedSamples: number;
  state: ExportUploadState;
  submitter: UserMinimal;
  bioProjectId: string;
}

export interface FullNcbiPlatforms {
  [k :string] : NcbiInstrument[]
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

// TODO: this is not the right return type
export async function getNCBIPlatforms(): Promise<FullNcbiPlatforms> {
  const data =  await get(`/ncbi/platforms`);
  return data.platforms;
}

export async function getNCBISources(): Promise<NcbiSource[]> {
  return await get(`/ncbi/sources`);
}

export async function getNCBIStrategies(): Promise<NcbiStrategy[]> {
  return await get(`/ncbi/strategies`);
}

export async function getNCBISelections() : Promise<NcbiSelection[]> {
  return await get(`/ncbi/selections`);
}
