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
    if (!isSampleFromCurrentProject(sample.projectId, projectId)) {
      associated.push(sample);
    } else if (sample.owner) {
      valid.push(sample);
    } else {
      locked.push(sample);
    }
  });
  return { valid, locked, associated };
}

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

const isSampleFromCurrentProject = (sampleProjectId, projectId) =>
  Number(sampleProjectId) === Number(projectId);
