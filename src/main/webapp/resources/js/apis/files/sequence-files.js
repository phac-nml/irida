/**
 * FastQC related API functions
 */

import { setBaseUrl } from "../../utilities/url-utilities";
import axios from "axios";
import {
  sequence_file_fastqc_charts_route,
  sequence_file_fastqc_details_route,
  sequence_file_overrepresented_sequences_route,
} from "../routes";
import { get } from "../requests";

const BASE_URL = setBaseUrl(`/ajax/sequenceFiles`);

/*
 * Get the fastqc details.
 * @param {number} sequencingObjectId SequencingObject ID
 * @param {number} sequenceFileId SequenceFile ID
 * @return {Promise<*>} `data` contains the OK response and a dto with the fastqc details;
 *                      `error` contains error information if an error occurred.
 */
export async function getFastQCDetails(sequencingObjectId, sequenceFileId) {
  return await get(sequence_file_fastqc_details_route(), {
    params: {
      sequencingObjectId,
      sequenceFileId,
    },
  });
}

/*
 * Get the fastqc charts.
 * @param {number} sequencingObjectId SequencingObject ID
 * @param {number} sequenceFileId SequenceFile ID
 * @return {Promise<*>} `data` contains the OK response and a dto with the fastqc charts;
 *                      `error` contains error information if an error occurred.
 */
export async function getFastQCImages(sequencingObjectId, sequenceFileId) {
  return await get(sequence_file_fastqc_charts_route(), {
    params: {
      sequencingObjectId,
      sequenceFileId,
    },
  });
}

/*
 * Get the overrepresented sequences.
 * @param {number} sequencingObjectId SequencingObject ID
 * @param {number} sequenceFileId SequenceFile ID
 * @return {Promise<*>} `data` contains the OK response and a AnalysisFastQC model object;
 *                      `error` contains error information if an error occurred.
 */
export async function getOverRepresentedSequences(
  sequencingObjectId,
  sequenceFileId
) {
  return await get(sequence_file_overrepresented_sequences_route(), {
    params: {
      sequencingObjectId,
      sequenceFileId,
    },
  });
}
