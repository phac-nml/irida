/**
 * Determine valid and invalid samples for merging samples.
 *
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

export function validateSamplesForRemove(samples, projectId) {
  const values = Object.values(samples),
    valid = [],
    locked = [],
    associated = [];
  values?.forEach((sample) => {
    if (Number(sample.projectId) !== Number(projectId)) {
      associated.push(sample);
    } else if (sample.owner) {
      valid.push(sample);
    } else {
      locked.push(sample);
    }
  });
  return { valid, locked, associated };
}
