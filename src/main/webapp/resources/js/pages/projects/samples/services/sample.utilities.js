/**
 * Determine valid and invalid samples
 */
export function validateSamples(samples) {
  const values = Object.values(samples),
    valid = [],
    invalid = [];
  values?.forEach(sample => {
    if (sample.owner) {
      valid.push(sample);
    } else {
      invalid.push(sample);
    }
  });
  return [valid, invalid];
}

export function validateSamplesForRemove(samples, projectId) {
  const values = Object.values(samples),
    valid = [],
    locked = [],
    associated = [];
  values?.forEach(sample => {
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
