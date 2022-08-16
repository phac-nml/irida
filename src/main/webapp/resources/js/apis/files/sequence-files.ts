/**
 * FastQC related API functions
 */

import { setBaseUrl } from "../../utilities/url-utilities";
import axios from "axios";
import { SequencingFile, SequencingObject } from "../samples/samples";
import { get } from "../requests";

export interface FastQC {
  additionalProperties: Record<string, unknown>[];
  analysisType: Record<string, string>;
  createdDate: Date;
  description: string;
  encoding: string;
  executionManagerAnalysisId: string;
  fastqcVersion: string;
  fileType: string;
  filteredSequences: number;
  gcContent: number;
  identifier: number;
  label: string;
  links: any[];
  maxLength: number;
  minLength: number;
  overrepresentedSequences: OverrepresentedSequences[];
  totalBases: number;
  totalSequences: number;
}

export interface FastQCDetails {
  analysisFastQC: FastQC;
  sequenceFile: SequencingFile;
  sequencingObject: SequencingObject;
}

export interface FastQCImages {
  perbaseChart: Uint8Array;
  persequenceChart: Uint8Array;
  duplicationlevelChart: Uint8Array;
  fastQCVersion: string;
}

export interface OverrepresentedSequences {
  sequence: string;
  overrepresentedSequenceCount: number;
  percentage: number;
  possibleSource: string;
  createdDate: Date;
  identifier: number;
}

const BASE_URL = setBaseUrl(`/ajax/sequenceFiles`);

/**
 * Get the fastqc details.
 * @param {number} sequencingObjectId SequencingObject ID
 * @param {number} sequenceFileId SequenceFile ID
 * @return {Promise<FastQCDetails>} `data` contains the OK response and a dto with the fastqc details;
 *                      `error` contains error information if an error occurred.
 */
export const getFastQCDetails = async (
  sequencingObjectId: number,
  sequenceFileId: number
): Promise<FastQCDetails> => {
  return get(
    `${BASE_URL}/fastqc-details?sequencingObjectId=${sequencingObjectId}&sequenceFileId=${sequenceFileId}`
  );
};

/**
 * Get the fastqc charts.
 * @param {number} sequencingObjectId SequencingObject ID
 * @param {number} sequenceFileId SequenceFile ID
 * @return {Promise<FastQCImages>} `data` contains the OK response and a dto with the fastqc charts;
 *                      `error` contains error information if an error occurred.
 */
export async function getFastQCImages(
  sequencingObjectId: number,
  sequenceFileId: number
): Promise<FastQCImages> {
  return get(
    `${BASE_URL}/fastqc-charts?sequencingObjectId=${sequencingObjectId}&sequenceFileId=${sequenceFileId}`
  );
}
