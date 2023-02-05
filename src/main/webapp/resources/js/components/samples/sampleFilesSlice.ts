import { createAction, createSlice } from "@reduxjs/toolkit";
import {
  SampleGenomeAssembly,
  SampleSequencingObject,
  SequencingObject,
} from "../../apis/samples/samples";

// The default width for action buttons
export const DEFAULT_ACTION_WIDTH = 75;

/**
 * Action to get updated sequencing objects
 */
export const updatedSequencingObjects = createAction(
  `sampleFiles/updatedSequencingObjects`,
  ({ updatedSeqObjects }) => ({
    payload: { updatedSeqObjects },
  })
);

/**
 * Action to add sequence files to sample
 */
export const addToSequenceFiles = createAction(
  `sampleFiles/addToSequenceFiles`,
  ({ sequenceFiles }) => ({
    payload: { sequenceFiles },
  })
);

/**
 * Action to add assembly files to sample
 */
export const addToAssemblyFiles = createAction(
  `sampleFiles/addToAssemblyFiles`,
  ({ assemblies }) => ({
    payload: { assemblies },
  })
);

/**
 * Action to add fast5 files to sample
 */
export const addToFast5Files = createAction(
  `sampleFiles/addToFast5Files`,
  ({ fast5 }) => ({
    payload: { fast5 },
  })
);

/**
 * Action to add sequencingobject to concatenation selected list
 */
export const addToConcatenateSelected = createAction(
  `sampleFiles/addToConcatenateSelected`,
  ({ seqObject }) => ({
    payload: { seqObject },
  })
);

/**
 * Action to remove sequencingobject from concatenation selected list
 */
export const removeFromConcatenateSelected = createAction(
  `sampleFiles/removeFromConcatenateSelected`,
  ({ seqObject }) => ({
    payload: { seqObject },
  })
);

/**
 * Action to reset concatenation selected list to an empty array
 */
export const resetConcatenateSelected = createAction(
  `sampleFiles/resetConcatenateSelected`,
  () => ({
    payload: {},
  })
);

export const fetchFilesForSample = createAction(
  `sampleFiles/fetchFilesForSample`,
  ({ sampleFiles }) => ({
    payload: { sampleFiles },
  })
);

export const fetchUpdatedSeqObjectsDelay = 30000; // 30 seconds

/**
 * Set up the initial state.
 */
const initialState: {
  loading: boolean;
  files: {
    singles?: SampleSequencingObject[];
    paired?: SampleSequencingObject[];
    fast5?: SampleSequencingObject[];
    assemblies?: SampleGenomeAssembly[];
  };
  concatenateSelected: SequencingObject[];
} = (() => {
  return {
    files: { singles: [], paired: [], fast5: [], assemblies: [] },
    loading: true,
    concatenateSelected: [],
  };
})();

const sampleFilesSlice = createSlice({
  reducers: {},
  name: "sampleFiles",
  initialState,
  extraReducers: (builder) => {
    builder.addCase(fetchFilesForSample, (state, action) => {
      if (typeof action.payload.sampleFiles !== "undefined") {
        const fileData = action.payload.sampleFiles;
        const fileTypesWithLength = Object.fromEntries(
          Object.entries(fileData).filter(([key]) => fileData[key].length)
        );

        state.files = fileTypesWithLength;
      }
      state.loading = false;
    });

    builder.addCase(addToSequenceFiles, (state, action) => {
      const seqFiles = action.payload.sequenceFiles;

      seqFiles.map((seqFile: SampleSequencingObject) => {
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
      const newAssemblies = action.payload.assemblies;

      newAssemblies.map((newAssembly: SampleGenomeAssembly) => {
        if (!state.files["assemblies"]) {
          state.files.assemblies = [];
        }
        state.files.assemblies = [...state.files.assemblies, newAssembly];
      });
    });

    builder.addCase(addToFast5Files, (state, action) => {
      const newFast5s = action.payload.fast5;

      newFast5s.map((newFast5: SampleSequencingObject) => {
        if (!state.files["fast5"]) {
          state.files.fast5 = [];
        }
        state.files.fast5 = [...state.files.fast5, newFast5];
      });
    });

    builder.addCase(updatedSequencingObjects, (state, action) => {
      const { updatedSeqObjects } = action.payload;

      if (updatedSeqObjects.paired.length) {
        updatedSeqObjects.paired.map(
          (updatedPairedObj: SampleSequencingObject) => {
            state.files.paired = state.files.paired?.map(
              (currentPairedObj: SampleSequencingObject) =>
                currentPairedObj.fileInfo.identifier ===
                updatedPairedObj.fileInfo.identifier
                  ? {
                      ...currentPairedObj,
                      fileInfo: updatedPairedObj.fileInfo,
                      firstFileSize: updatedPairedObj.firstFileSize,
                      secondFileSize: updatedPairedObj.secondFileSize,
                      qcEntries: updatedPairedObj.qcEntries,
                      automatedAssembly: updatedPairedObj.automatedAssembly,
                    }
                  : currentPairedObj
            );
          }
        );
      }

      if (updatedSeqObjects.singles.length) {
        updatedSeqObjects.singles.map(
          (updatedSingleEndFileObj: SampleSequencingObject) => {
            state.files.singles = state.files.singles?.map(
              (currentSingleFileObj: SampleSequencingObject) =>
                currentSingleFileObj.fileInfo.identifier ===
                updatedSingleEndFileObj.fileInfo.identifier
                  ? {
                      ...currentSingleFileObj,
                      fileInfo: updatedSingleEndFileObj.fileInfo,
                      firstFileSize: updatedSingleEndFileObj.firstFileSize,
                      qcEntries: updatedSingleEndFileObj.qcEntries,
                    }
                  : currentSingleFileObj
            );
          }
        );
      }

      if (updatedSeqObjects.fast5.length) {
        updatedSeqObjects.fast5.map(
          (updatedFast5FileObj: SampleSequencingObject) => {
            state.files.fast5 = state.files.fast5?.map(
              (currentFast5FileObj: SampleSequencingObject) =>
                currentFast5FileObj.fileInfo.identifier ===
                updatedFast5FileObj.fileInfo.identifier
                  ? {
                      ...currentFast5FileObj,
                      fileInfo: updatedFast5FileObj.fileInfo,
                      firstFileSize: updatedFast5FileObj.firstFileSize,
                      qcEntries: updatedFast5FileObj.qcEntries,
                    }
                  : currentFast5FileObj
            );
          }
        );
      }
    });

    builder.addCase(addToConcatenateSelected, (state, action) => {
      state.concatenateSelected = [
        ...state.concatenateSelected,
        action.payload.seqObject,
      ];
    });

    builder.addCase(removeFromConcatenateSelected, (state, action) => {
      state.concatenateSelected = state.concatenateSelected.filter(
        (seqObj) => seqObj.identifier !== action.payload.seqObject.identifier
      );
    });

    builder.addCase(resetConcatenateSelected, (state) => {
      state.concatenateSelected = [];
    });
  },
});

export default sampleFilesSlice.reducer;
