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

  builder.addCase(addSample, (state, action) => {
    state.samples = [action.payload.sample, ...state.samples];
  });

  builder.addCase(updateSample, (state, action) => {
    const { newSample, index } = action.payload;
    state.samples[index] = newSample;
  });
});
