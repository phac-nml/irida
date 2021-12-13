import { createAction, createSlice } from "@reduxjs/toolkit";

/**
 * Action to set the target sample files
 */
export const setSampleFiles = createAction(
  `sampleFiles/setSampleFiles`,
  (data) => ({
    payload: { data },
  })
);

/**
 * Action to remove files from sample
 */
export const removeSampleFiles = createAction(
  `sampleFiles/removeSampleFiles`,
  ({ sequencingObjectId }) => ({
    payload: { sequencingObjectId },
  })
);

/**
 * Set up the initial state.
 */
const initialState = (() => {
  return {
    files: {},
    loading: true,
  };
})();

const sampleFilesSlice = createSlice({
  name: "sampleFiles",
  initialState,
  extraReducers: (builder) => {
    builder.addCase(setSampleFiles, (state, action) => {
      console.log(action.payload.data);
      if (typeof action.payload.data !== "undefined") {
        let fileData = action.payload.data;
        Object.keys(fileData).forEach(
          (key) => !fileData[key].length && delete fileData[key]
        );

        state.files = fileData;
      }
      state.loading = false;
    });

    builder.addCase(removeSampleFiles, (state, action) => {
      const fileTypes = state.files;
      if (Boolean(fileTypes.paired)) {
        fileTypes.paired = fileTypes.paired.filter(
          (pair) =>
            pair.fileInfo.identifier !== action.payload.sequencingObjectId
        );

        if (fileTypes.paired.length) {
          state.files.paired = fileTypes.paired;
        } else {
          delete state.files["paired"];
        }
      }

      if (Boolean(fileTypes.singles)) {
        fileTypes.singles = fileTypes.singles.filter(
          (single) =>
            single.fileInfo.identifier !== action.payload.sequencingObjectId
        );

        if (fileTypes.singles.length) {
          state.files.singles = fileTypes.singles;
        } else {
          delete state.files["singles"];
        }
      }

      if (Boolean(fileTypes.assemblies)) {
        fileTypes.assemblies = fileTypes.assemblies.filter(
          (assembly) =>
            assembly.fileInfo.identifier !== action.payload.sequencingObjectId
        );

        if (fileTypes.assemblies.length) {
          state.files.assemblies = fileTypes.assemblies;
        } else {
          delete state.files["assemblies"];
        }
      }

      if (Boolean(fileTypes.fast5)) {
        fileTypes.fast5 = fileTypes.fast5.filter(
          (fast5) =>
            fast5.fileInfo.identifier !== action.payload.sequencingObjectId
        );

        if (fileTypes.fast5.length) {
          state.files.fast5 = fileTypes.fast5;
        } else {
          delete state.files["fast5"];
        }
      }
    });
  },
});

export default sampleFilesSlice.reducer;
