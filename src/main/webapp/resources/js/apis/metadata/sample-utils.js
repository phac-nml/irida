/*
 * Utility file for samples
 */

export const sampleNameRegex = new RegExp("^[A-Za-z0-9-_]{3,}$");

export function validateSampleName(sampleName) {
   return sampleNameRegex.test(sampleName);
 }