import { SelectedSample } from "../types/irida";

/**
 * Determine which samples are locked and which are unlocked based on the
 */
export function separateLockedAndUnlockedSamples(
  samples: Array<SelectedSample>
) {
  const unlocked: Array<SelectedSample> = [];
  const locked: Array<SelectedSample> = [];

  samples.forEach((sample) => {
    if (sample.owner) {
      unlocked.push(sample);
    } else {
      locked.push(sample);
    }
  });
  return [unlocked, locked];
}

/**
 * Determine if samples are valid, locked, or associated
 *  valid => samples user has ownership
 *  locked => sample user does not have ownership
 *  associated => samples that do not belong to the current project.
 * @param {array} samples
 * @param {number | string} projectId
 * @returns {{valid: *[], associated: *[]}}
 */
export function validateSamplesForRemove(samples, projectId) {
  const values = Object.values(samples),
    valid = [],
    associated = [];
  values?.forEach((sample) => {
    if (!isSampleFromCurrentProject(sample.projectId, projectId)) {
      associated.push(sample);
    } else {
      valid.push(sample);
    }
  });
  return { valid, associated };
}

/**
 * Determine if samples are valid or associated for using the linker command
 *  Valid => Not associated
 *  Associated   => Belongs to a different project
 * @param {array} samples
 * @param {number | string} projectId
 * @returns {{valid: *[], associated: *[]}}
 */
export function validateSamplesForLinker(samples, projectId) {
  const values = Object.values(samples),
    valid = [],
    associated = [];
  values.forEach((sample) => {
    if (isSampleFromCurrentProject(sample.projectId, projectId)) {
      valid.push(sample.id);
    } else {
      associated.push(sample);
    }
  });
  return { valid, associated };
}

/**
 * Checks id from a sample against the current project
 * @param {number | string} sampleProjectId
 * @param {number | string} projectId
 * @returns {boolean}
 */
const isSampleFromCurrentProject = (sampleProjectId, projectId) =>
  Number(sampleProjectId) === Number(projectId);
