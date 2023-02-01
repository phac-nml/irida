import type { SelectedSample } from "../../types";

/**
 * Determine valid and invalid samples for merging samples.
 * Valid => samples that the user has ownership of
 * locked => no ownership
 */
export function validateSamplesForMergeOrShare(
  samples: Record<number, SelectedSample>
) {
  const values = Object.values(samples);
  const valid: SelectedSample[] = [];
  const locked: SelectedSample[] = [];

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
 * @returns {{valid: *[], associated: *[]}}
 */
export function validateSamplesForRemove(
  samples: Record<number, SelectedSample>,
  projectId: number
) {
  const values = Object.values(samples);
  const valid: SelectedSample[] = [];
  const associated: SelectedSample[] = [];

  values.forEach((sample) => {
    if (sample.projectId !== projectId) {
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
 *  Associated => Belongs to a different project
 * @param  samples
 * @param  projectId
 */
export function validateSamplesForLinker(
  samples: Record<number, SelectedSample>,
  projectId: number
) {
  const values = Object.values(samples);
  const valid: number[] = [];
  const associated: SelectedSample[] = [];
  values.forEach((sample) => {
    if (sample.projectId === projectId) {
      valid.push(sample.id);
    } else {
      associated.push(sample);
    }
  });
  return { valid, associated };
}
