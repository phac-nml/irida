/**
 * Determine valid and invalid samples for merging samples.
 * Valid => samples that the user has ownership of
 * Invalid => no ownership
 */
export function validateSamplesForMerge(samples) {
  const values = Object.values(samples),
    valid = [],
    locked = [];
  values?.forEach((sample) => {
    if (sample.owner) {
      valid.push(sample);
    } else {
      locked.push(sample);
    }
  });
  return { valid, locked };
}

/**
 * Determine if samples are valid, locked, or associated
 *  valid => samples user has ownership
 *  locked => sample user does not have ownership
 *  associated => samples that do not belong to the current project.
 * @param {array} samples
 * @param {number | string} projectId
 * @returns {{valid: *[], associated: *[], locked: *[]}}
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
  return { valid, locked, associated };
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
