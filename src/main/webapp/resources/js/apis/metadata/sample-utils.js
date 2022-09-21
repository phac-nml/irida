/*
 * Utility file for samples
 */

export const sampleNameRegex = new RegExp("^[A-Za-z0-9-_]{3,}$");

/**
 * Checks is the sample name is valid
 * @param {string} sampleName
 * @returns {boolean}
 */
export function validateSampleName(sampleName) {
  if (sampleName) {
    return sampleNameRegex.test(sampleName);
  } else {
    return false;
  }
}
