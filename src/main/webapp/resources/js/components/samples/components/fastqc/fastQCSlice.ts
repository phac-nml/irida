import { createAction, createSlice } from "@reduxjs/toolkit";

/**
 * Action to set the fast qc details
 */
export const setFastQCDetails = createAction(
  `fastQC/setFastQCDetails`,
  ({ fastQC, file, processingState }) => ({
    payload: { fastQC, file, processingState },
  })
);

/**
 * Action to set the fast qc details
 */
export const setAnalysisFastQC = createAction(
  `fastQC/setAnalysisFastQC`,
  ({ fastQC }) => ({
    payload: { fastQC },
  })
);

/**
 * Action to set fastqc modal data
 */
export const setFastQCModalData = createAction(
  `fastQC/setFastQCModalData`,
  ({
    sequencingObjectId,
    fileId,
    fileLabel,
    fastQCModalVisible,
    file,
    processingState,
  }) => ({
    payload: {
      sequencingObjectId,
      fileId,
      fileLabel,
      fastQCModalVisible,
      file,
      processingState,
    },
  })
);

/**
 * Action to clear fastqc modal data
 */
export const clearFastQCData = createAction(`fastQC/clearFastQCData`, () => ({
  payload: {},
}));

/**
 * Set up the initial state.
 */
const initialState = (() => {
  return {
    loading: true,
    fastQC: {},
    file: {},
    processingState: null,
    fastQCModalVisible: false,
    sequencingObjectId: null,
    fileId: null,
    fileLabel: null,
  };
})();

const fastQCSlice = createSlice({
  reducers: {},
  name: "fastQC",
  initialState,
  extraReducers: (builder) => {
    builder.addCase(setFastQCDetails, (state, action) => {
      state.fastQC = action.payload.fastQC;
      state.file = action.payload.file;
      state.processingState = action.payload.processingState;
      state.loading = false;
    });

    builder.addCase(setFastQCModalData, (state, action) => {
      state.sequencingObjectId = action.payload.sequencingObjectId;
      state.fileId = action.payload.fileId;
      state.fileLabel = action.payload.fileLabel;
      state.fastQCModalVisible = action.payload.fastQCModalVisible;
      state.processingState = action.payload.processingState;
    });

    builder.addCase(setAnalysisFastQC, (state, action) => {
      state.fastQC = action.payload.fastQC;
      state.loading = false;
    });

    builder.addCase(clearFastQCData, (state) => {
      state.fastQC = {};
      state.file = {};
      state.processingState = null;
      state.fastQCModalVisible = false;
    });
  }
});

export default fastQCSlice.reducer;
