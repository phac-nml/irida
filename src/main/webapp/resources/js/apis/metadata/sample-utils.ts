/*
 * Utility file for samples
 */

export const sampleNameRegex = new RegExp("^[A-Za-z0-9-_]{3,}$");

/**
 * Checks is the sample name is valid
 * @param sampleName the name of the sample
 * @returns if the sample name is valid
 */
export function validateSampleName(sampleName: string): boolean {
  if (sampleName) {
    return sampleNameRegex.test(sampleName);
  } else {
    return false;
  }
}
