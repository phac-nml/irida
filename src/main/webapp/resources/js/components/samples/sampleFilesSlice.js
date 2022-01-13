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
export const removeFileObjectFromSample = createAction(
  `sampleFiles/removeSampleFiles`,
  ({ fileObjectId, type }) => ({
    payload: { fileObjectId, type },
  })
);

export const updatedSequencingObjects = createAction(
  `sampleFiles/updatedSequencingObjects`,
  ({ updatedSeqObjects }) => ({
    payload: { updatedSeqObjects },
  })
);

export const addToSequenceFiles = createAction(
  `sampleFiles/addToSequenceFiles`,
  ({ sequenceFiles }) => ({
    payload: { sequenceFiles },
  })
);

export const addToAssemblyFiles = createAction(
  `sampleFiles/addToAssemblyFiles`,
  ({ assemblies }) => ({
    payload: { assemblies },
  })
);

export const addToFast5Files = createAction(
  `sampleFiles/addToFast5Files`,
  ({ fast5 }) => ({
    payload: { fast5 },
  })
);

export const fetchUpdatedSeqObjectsDelay = 30000; // 30 seconds

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
      if (typeof action.payload.data !== "undefined") {
        let fileData = action.payload.data;
        Object.keys(fileData).forEach(
          (key) => !fileData[key].length && delete fileData[key]
        );

        state.files = fileData;
      }
      state.loading = false;
    });

    builder.addCase(addToSequenceFiles, (state, action) => {
      let seqFiles = action.payload.sequenceFiles;

      seqFiles.map((seqFile) => {
        if (seqFile.secondFileSize !== null) {
          if (!state.files["paired"]) {
            state.files.paired = [];
          }
          state.files.paired = [...state.files.paired, seqFile];
        } else {
          if (!state.files["singles"]) {
            state.files.singles = [];
          }

          state.files.singles = [...state.files.singles, seqFile];
        }
      });
    });

    builder.addCase(addToAssemblyFiles, (state, action) => {
      let newAssemblies = action.payload.assemblies;

      if (!state.files["assemblies"]) {
        state.files.assemblies = [];
      }
      newAssemblies.map((newAssembly) => {
        state.files.assemblies = [...state.files.assemblies, newAssembly];
      });
    });

    builder.addCase(addToFast5Files, (state, action) => {
      let newFast5s = action.payload.fast5;

      newFast5s.map((newFast5) => {
        if (!state.files["fast5"]) {
          state.files.fast5 = [];
        }
        state.files.fast5 = [...state.files.fast5, newFast5];
      });
    });

    builder.addCase(updatedSequencingObjects, (state, action) => {
      let updatedSeqObjects = action.payload.updatedSeqObjects;

      if (Boolean(updatedSeqObjects.paired.length)) {
        updatedSeqObjects.paired.map((updatedPairedObj) => {
          state.files.paired = state.files.paired.map((currentPairedObj) =>
            currentPairedObj.fileInfo.identifier ===
            updatedPairedObj.fileInfo.identifier
              ? {
                  ...currentPairedObj,
                  fileInfo: updatedPairedObj.fileInfo,
                  firstFileSize: updatedPairedObj.firstFileSize,
                  secondFileSize: updatedPairedObj.secondFileSize,
                }
              : currentPairedObj
          );
        });
      }

      if (updatedSeqObjects.singles.length) {
        updatedSeqObjects.singles.map((updatedSingleEndFileObj) => {
          state.files.singles = state.files.singles.map(
            (currentSingleFileObj) =>
              currentSingleFileObj.fileInfo.identifier ===
              updatedSingleEndFileObj.fileInfo.identifier
                ? {
                    ...currentSingleFileObj,
                    fileInfo: updatedSingleEndFileObj.fileInfo,
                    firstFileSize: updatedSingleEndFileObj.firstFileSize,
                  }
                : currentSingleFileObj
          );
        });
      }

      if (Boolean(updatedSeqObjects.fast5.length)) {
        updatedSeqObjects.fast5.map((updatedFast5FileObj) => {
          state.files.fast5 = state.files.fast5.map((currentFast5FileObj) =>
            currentFast5FileObj.fileInfo.identifier ===
            updatedFast5FileObj.fileInfo.identifier
              ? {
                  ...currentFast5FileObj,
                  fileInfo: updatedFast5FileObj.fileInfo,
                  firstFileSize: updatedFast5FileObj.firstFileSize,
                }
              : currentFast5FileObj
          );
        });
      }
    });

    builder.addCase(removeFileObjectFromSample, (state, action) => {
      const fileTypes = state.files;

      if (action.payload.type === "sequencingObject") {
        if (Boolean(fileTypes.paired)) {
          fileTypes.paired = fileTypes.paired.filter(
            (pair) => pair.fileInfo.identifier !== action.payload.fileObjectId
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
              single.fileInfo.identifier !== action.payload.fileObjectId
          );

          if (fileTypes.singles.length) {
            state.files.singles = fileTypes.singles;
          } else {
            delete state.files["singles"];
          }
        }

        if (Boolean(fileTypes.fast5)) {
          fileTypes.fast5 = fileTypes.fast5.filter(
            (fast5) => fast5.fileInfo.identifier !== action.payload.fileObjectId
          );

          if (fileTypes.fast5.length) {
            state.files.fast5 = fileTypes.fast5;
          } else {
            delete state.files["fast5"];
          }
        }
      } else {
        if (Boolean(fileTypes.assemblies)) {
          fileTypes.assemblies = fileTypes.assemblies.filter(
            (assembly) =>
              assembly.fileInfo.identifier !== action.payload.fileObjectId
          );

          if (fileTypes.assemblies.length) {
            state.files.assemblies = fileTypes.assemblies;
          } else {
            delete state.files["assemblies"];
          }
        }
      }
    });
  },
});

export default sampleFilesSlice.reducer;
