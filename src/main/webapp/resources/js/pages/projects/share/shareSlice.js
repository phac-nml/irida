import { createAction, createSlice } from "@reduxjs/toolkit";

/**
 * Action to set the target project for the samples
 */
export const setProject = createAction(`share/setProject`, (projectId) => ({
  payload: { projectId },
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
 * Action to update whether the samples are to be movied or just copied
 */
export const updateMoveSamples = createAction(
  `share/updateMoveSamples`,
  (remove) => ({
    payload: { remove },
  })
);

/**
 * Set up the initial state.  This is pulled from session storage which should
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

  const { samples, projectId: currentProject } = JSON.parse(stringData);
  return {
    originalSamples: samples,
    samples,
    currentProject,
    locked: false,
    remove: false,
  };
})();

const shareSlice = createSlice({
  name: "share",
  initialState,
  extraReducers: (builder) => {
    builder.addCase(setProject, (state, action) => {
      state.projectId = action.payload.projectId;
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
  },
});

export default shareSlice.reducer;
