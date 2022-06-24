import { createAction, createReducer } from "@reduxjs/toolkit";

/**
 * Set up the initial state
 */
const initialState = { files: [], samples: [] };

/**
 * Action to set the files
 */
export const setFiles = createAction(`rootReducers/setFiles`, (files) => ({
  payload: { files },
}));

/**
 * Action to add a file
 */
export const addFile = createAction(`rootReducers/addFile`, (fileId) => ({
  payload: { fileId },
}));

/**
 * Action to remove a file
 */
export const removeFile = createAction(`rootReducers/removeFile`, (fileId) => ({
  payload: { fileId },
}));

/**
 * Action to set the samples
 */
export const setSamples = createAction(`rootReducers/setSample`, (samples) => ({
  payload: { samples },
}));

/**
 * Action to add a sample
 */
export const addSample = createAction(`rootReducers/addSample`, (sample) => ({
  payload: { sample },
}));

/**
 * Action to update a sample
 */
export const updateSample = createAction(
  `rootReducers/updateSample`,
  (newSample, index) => ({
    payload: { newSample, index },
  })
);

/**
 * Action to delete a sample
 */
export const deleteSample = createAction(
  `rootReducers/deleteSample`,
  (index) => ({
    payload: { index },
  })
);

/**
 * Action to move a file within a sample
 */
export const moveFileWithinSample = createAction(
  `rootReducers/moveFileWithinSample`,
  (file, sampleIndex, prevPairIndex, pairIndex) => ({
    payload: { file, sampleIndex, prevPairIndex, pairIndex },
  })
);

/**
 * Action to remove a file from a sample
 */
export const removeFileFromSample = createAction(
  `rootReducers/removeFileFromSample`,
  (fileId, sampleIndex, pairIndex) => ({
    payload: { fileId, sampleIndex, pairIndex },
  })
);

/**
 * Action to add file to new pair in sample
 */
export const addFileToNewPairInSample = createAction(
  `rootReducers/addFileToNewPairInSample`,
  (file, sampleIndex) => ({
    payload: { file, sampleIndex },
  })
);

/**
 * Action to add file to existing pair in sample
 */
export const addFileToExistingPairInSample = createAction(
  `rootReducers/addFileToExistingPairInSample`,
  (file, sampleIndex, pairIndex) => ({
    payload: { file, sampleIndex, pairIndex },
  })
);

/*
Redux reducer for creating sequencing run samples.
For more information on redux reducers see: https://redux-toolkit.js.org/api/createReducer
 */
export const runReducer = createReducer(initialState, (builder) => {
  builder.addCase(setFiles, (state, action) => {
    state.files = action.payload.files;
  });

  builder.addCase(addFile, (state, action) => {
    state.files = state.files.map((file) => {
      return {
        ...file,
        show: file.id === action.payload.fileId ? true : file.show,
      };
    });
  });

  builder.addCase(removeFile, (state, action) => {
    state.files = state.files.map((file) => {
      return {
        ...file,
        show: file.id === action.payload.fileId ? false : file.show,
      };
    });
  });

  builder.addCase(setSamples, (state, action) => {
    state.samples = action.payload.samples;
  });

  builder.addCase(addSample, (state, action) => {
    state.samples = [action.payload.sample, ...state.samples];
  });

  builder.addCase(updateSample, (state, action) => {
    const { newSample, index } = action.payload;
    state.samples[index] = newSample;
  });

  builder.addCase(deleteSample, (state, action) => {
    state.samples.splice(action.payload.index, 1);
  });

  builder.addCase(moveFileWithinSample, (state, action) => {
    const { file, sampleIndex, prevPairIndex, pairIndex } = action.payload;
    const sample = state.samples[sampleIndex];
    const updatedPairs = [...sample.pairs];
    const prevPair = sample.pairs[prevPairIndex];
    const pair = sample.pairs[pairIndex];

    updatedPairs[pairIndex] = {
      forward: pair.forward,
      reverse: file,
    };

    if (prevPair.reverse === null) {
      //the pair is going to be empty
      //removing it from the pair list
      updatedPairs.splice(prevPairIndex, 1);
    } else {
      //converting the pair into single
      updatedPairs[prevPairIndex] = {
        forward:
          prevPair.forward?.id === file.id
            ? prevPair.reverse
            : prevPair.forward,
        reverse: null,
      };
    }
    const updatedSample = {
      sampleName: sample.sampleName,
      pairs: updatedPairs,
    };
    state.samples[sampleIndex] = updatedSample;
  });

  builder.addCase(removeFileFromSample, (state, action) => {
    const { fileId, sampleIndex, pairIndex } = action.payload;
    const sample = state.samples[sampleIndex];
    const updatedPairs = [...sample.pairs];
    const pair = sample.pairs[pairIndex];

    if (pair.reverse === null) {
      //the pair is going to be empty
      //removing pair from the pair list
      updatedPairs.splice(pairIndex, 1);
    } else {
      //converting the pair into single
      updatedPairs[pairIndex] = {
        forward: pair.forward?.id === fileId ? pair.reverse : pair.forward,
        reverse: null,
      };
    }
    const updatedSample = {
      sampleName: sample.sampleName,
      pairs: updatedPairs,
    };
    state.samples[sampleIndex] = updatedSample;
  });

  builder.addCase(addFileToNewPairInSample, (state, action) => {
    const { file, sampleIndex } = action.payload;
    const sample = state.samples[sampleIndex];
    const newPair = { forward: file, reverse: null };
    const updatedSample = {
      sampleName: sample.sampleName,
      pairs: [...sample.pairs, newPair],
    };
    state.samples[sampleIndex] = updatedSample;
  });

  builder.addCase(addFileToExistingPairInSample, (state, action) => {
    const { file, sampleIndex, pairIndex } = action.payload;
    const sample = state.samples[sampleIndex];
    const updatedPairs = [...sample.pairs];
    const pair = sample.pairs[pairIndex];
    //assume the pair already has a forward file
    updatedPairs[pairIndex] = { forward: pair.forward, reverse: file };
    const updatedSample = {
      sampleName: sample.sampleName,
      pairs: updatedPairs,
    };
    state.samples[sampleIndex] = updatedSample;
  });
});
