/**
 * FastQC related API functions
 */

import { setBaseUrl } from "../../utilities/url-utilities";
import axios from "axios";

const BASE_URL = setBaseUrl(`/ajax/sequenceFiles`);

/*
 * Get the fastqc details.
 * @param {number} sequencingObjectId SequencingObject ID
 * @param {number} sequenceFileId SequenceFile ID
 * @return {Promise<*>} `data` contains the OK response and a dto with the fastqc details;
 *                      `error` contains error information if an error occurred.
 */
export async function getFastQCDetails(
  sequencingObjectId: number,
  sequenceFileId: number
) {
  return await axios
    .get(`${BASE_URL}/fastqc-details`, {
      params: {
        sequencingObjectId,
        sequenceFileId,
      },
    })
    .then(({ data }) => data)
    .catch((error) => {
      throw new Error(error.response.data.error);
    });
}

/*
 * Get the fastqc charts.
 * @param {number} sequencingObjectId SequencingObject ID
 * @param {number} sequenceFileId SequenceFile ID
 * @return {Promise<*>} `data` contains the OK response and a dto with the fastqc charts;
 *                      `error` contains error information if an error occurred.
 */
export async function getFastQCImages(
  sequencingObjectId: number,
  sequenceFileId: number
) {
  return await axios
    .get(`${BASE_URL}/fastqc-charts`, {
      params: {
        sequencingObjectId,
        sequenceFileId,
      },
    })
    .then(({ data }) => data)
    .catch((error) => {
      throw new Error(error.response.data.error);
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
  sequencingObjectId: number,
  sequenceFileId: number
) {
  return await axios
    .get(`${BASE_URL}/overrepresented-sequences`, {
      params: {
        sequencingObjectId,
        sequenceFileId,
      },
    })
    .then(({ data }) => data)
    .catch((error) => {
      throw new Error(error.response.data.error);
    });
}
