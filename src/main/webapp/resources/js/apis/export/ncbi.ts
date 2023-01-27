import {
  ExportUploadState,
  NcbiBioSample,
  NcbiInstrument,
  NcbiSelection,
  NcbiSource,
  NcbiStrategy,
  NcbiSubmission,
  UserMinimal,
} from "../../types/irida";
import { setBaseUrl } from "../../utilities/url-utilities";
import { get, post } from "../requests";

export interface NcbiExportSubmissionTableModel {
  exportedSamples: number;
  state: ExportUploadState;
  submitter: UserMinimal;
  bioProjectId: string;
}

export type NcbiSubmissionBioSample = Omit<
  NcbiBioSample,
  "id" | "accession" | "status" | "singles" | "pairs"
> & {
  pairs: number[];
  singles: number[];
};

export interface NcbiSubmissionRequest {
  projectId: number;
  bioProject: string;
  namespace: string;
  organization: string;
  releaseDate: number;
  samples: Array<NcbiSubmissionBioSample>;
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
  return get(setBaseUrl(`ajax/ncbi/project/${projectId}/list`));
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
  return get(setBaseUrl(`ajax/ncbi/project/${projectId}/details/${uploadId}`));
}

/**
 * Fetch NCBI listed sequencing platforms.
 */
export async function getNCBIPlatforms(): Promise<FullNcbiPlatforms> {
  return await get(setBaseUrl(`ajax/ncbi/platforms`));
}

/**
 * Fetch a list of NCBI listed sources
 */
export async function getNCBISources(): Promise<NcbiSource[]> {
  return await get(setBaseUrl(`ajax/ncbi/sources`));
}

/**
 * Fetch a list of NCBI sequencing strategies
 */
export async function getNCBIStrategies(): Promise<NcbiStrategy[]> {
  return await get(setBaseUrl(`ajax/ncbi/strategies`));
}

/**
 * Fetch a list of NCBI selections
 */
export async function getNCBISelections(): Promise<NcbiSelection[]> {
  return await get(setBaseUrl(`ajax/ncbi/selections`));
}

/**
 * Submit a request for a new NCBI SRA Submission
 * @param request
 */
export async function submitNcbiSubmissionRequest(
  request: NcbiSubmissionRequest
) {
  return await post<void, NcbiSubmissionRequest>(
    setBaseUrl(`ajax/ncbi/submit`),
    request
  );
}
