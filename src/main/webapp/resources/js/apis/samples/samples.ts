import {
  PairedEndSequenceFile,
  SingleEndSequenceFile,
} from "../../types/irida";
import { setBaseUrl } from "../../utilities/url-utilities";
import { get } from "../requests";

const URL = setBaseUrl(`ajax/samples`);

export interface SamplesFiles {
  singles: SingleEndSequenceFile[];
  pairs: PairedEndSequenceFile[];
}

/**
 * Get details about a particular sample
 * NOTE: Does not include file information.
 * @param  id - identifier for a sample
 * @returns {Promise<any>}
 */
export const fetchSampleDetails = async (id: number): Promise<any> => {
  return get(`${URL}/${id}/details`);
};

/**
 * Get file details for a sample
 * @param sampleId - identifier for a sample
 * @param projectId - identifier for a project (if the sample is in the cart), not required.
 * @returns {Promise<any>}
 */
export async function fetchSampleFiles({
  sampleId,
  projectId,
}: {
  sampleId: number;
  projectId?: number;
}): Promise<SamplesFiles> {
  return await get(
    `${URL}/${sampleId}/files${projectId ? `?projectId=${projectId}` : ""}`
  );
}
