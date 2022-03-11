import { createAction, createSlice } from "@reduxjs/toolkit";
import { compareRestrictionLevels } from "../../../utilities/restriction-utilities";

/**
 * Action to set the target project for the samples
 */
export const setProject = createAction(`share/setProject`, (targetProject) => ({
  payload: { targetProject },
}));

/**
 * Action to remove a sample from being shared / moved
 */
export const removeSample = createAction(`share/removeSample`, (sampleId) => ({
  payload: { sampleId },
}));

/**
 * Action to set the status of locking samples to be copied
 */
export const updatedLocked = createAction(
  `share/updateOwnership`,
  (locked) => ({
    payload: { locked },
  })
);

/**
 * Action to update whether the samples are to be moved or just copied
 */
export const updateMoveSamples = createAction(
  `share/updateMoveSamples`,
  (remove) => ({
    payload: { remove },
  })
);

/**
 * Sets up the original restrictions to be the same as what are on the source project.
 * This will change once a target project is selected
 */
export const setMetadataRestrictions = createAction(
  `share/setMetadataRestrictions`,
  (metadataRestrictions) => ({
    payload: {
      metadataRestrictions: metadataRestrictions.map((r) => ({
        ...r,
        initial: true,
      })),
    },
  })
);

/**
 * Update one of the metadata restrictions with a specific value
 */
export const updateMetadataRestriction = createAction(
  `share/updateMetadataRestriction`,
  ({ field, value }) => ({
    payload: {
      field,
      value,
    },
  })
);

/**
 * Set up the initial state. This is pulled from session storage which should
 * be accessed by the key "share".  The stringified object should be of the format:
 * <code>
 *   {
 *     samples: [],
 *     projectId: 1,
 *     timestamp: 1629374711590
 *   }
 * </code>
 * @type {{currentProject, samples}}
 */
const initialState = (() => {
  const stringData = window.sessionStorage.getItem("share");

  if (stringData === null) {
    return {};
  }

  const { samples: allSamples, projectId: currentProject } =
    JSON.parse(stringData);
  const samples = [];
  const associated = [];
  allSamples.forEach((sample) => {
    if (Number(sample.projectId) === Number(currentProject)) {
      samples.push(sample);
    } else {
      associated.push(sample);
    }
  });

  return {
    originalSamples: samples,
    samples,
    associated,
    currentProject,
    locked: false,
    remove: false,
    metadataRestrictions: [],
  };
})();

const shareSlice = createSlice({
  name: "share",
  initialState,
  extraReducers: (builder) => {
    builder.addCase(setProject, (state, action) => {
      state.targetProject = action.payload.targetProject;
    });

    builder.addCase(removeSample, (state, action) => {
      state.originalSamples = state.originalSamples.filter(
        (sample) => sample.id !== action.payload.sampleId
      );
    });

    builder.addCase(updatedLocked, (state, action) => {
      state.locked = action.payload.locked;
    });

    builder.addCase(updateMoveSamples, (state, action) => {
      state.remove = action.payload.remove;
      if (action.payload.remove) {
        state.locked = false;
      }
    });

    builder.addCase(setMetadataRestrictions, (state, action) => {
      state.metadataRestrictions = action.payload.metadataRestrictions;
    });

    builder.addCase(updateMetadataRestriction, (state, action) => {
      const { field, value } = action.payload;
      const index = state.metadataRestrictions.findIndex(
        (f) => f.fieldKey === field.fieldKey
      );
      if (index >= 0) {
        field.restriction = value;
        field.difference = compareRestrictionLevels(
          state.metadataRestrictions[index].restriction,
          value
        );
        const updatedFields = [...state.metadataRestrictions];
        updatedFields[index] = field;
        state.metadataRestrictions = updatedFields;
      }
    });
  },
});

export default shareSlice.reducer;
