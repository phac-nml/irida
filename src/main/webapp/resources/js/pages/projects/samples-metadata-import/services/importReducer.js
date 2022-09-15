import {
  createAction,
  createAsyncThunk,
  createReducer,
  current,
} from "@reduxjs/toolkit";
import { validateSampleName } from "../../../../apis/metadata/sample-utils";
import { validateSamples } from "../../../../apis/projects/samples";

const initialState = {
  sampleNameColumn: "",
  headers: [],
  metadata: [],
};

/*
Redux sync thunk for setting the sample name column and enriching the metadata.
For more information on redux async thunks see: https://redux-toolkit.js.org/api/createAsyncThunk
*/
export const setSampleNameColumn = createAsyncThunk(
  `importReducer/setSampleNameColumn`,
  async ({ projectId, column }, { getState }) => {
    const state = getState();
    const metadata = state.importReducer.metadata;
    const response = await validateSamples({
      projectId: projectId,
      body: {
        samples: metadata.map((row) => ({
          name: row[column],
        })),
      },
    });

    const updatedMetadata = metadata.map((metadataItem, index) => {
      return {
        ...metadataItem,
        rowKey: `metadata-uploader-row-${index}`,
        isSampleNameValid: validateSampleName(metadataItem[column]),
        foundSampleId: response.data.samples
          .find((sample) => metadataItem[column] === sample.name)
          .ids?.at(0),
        saved: null,
      };
    });

    return { sampleNameColumn: column, metadata: updatedMetadata };
  }
);

/*
Redux action for setting the metadata headers.
For more information on redux actions see: https://redux-toolkit.js.org/api/createAction
 */
export const setHeaders = createAction(
  `importReducer/setHeaders`,
  (headers) => ({
    payload: { headers },
  })
);

/*
Redux action for setting the project metadata.
For more information on redux actions see: https://redux-toolkit.js.org/api/createAction
 */
export const setMetadata = createAction(
  `importReducer/setMetadata`,
  (metadata) => ({
    payload: { metadata },
  })
);

/*
Redux reducer for project metadata.
For more information on redux reducers see: https://redux-toolkit.js.org/api/createReducer
 */
export const importReducer = createReducer(initialState, (builder) => {
  builder.addCase(setHeaders, (state, action) => {
    state.headers = action.payload.headers;
  });
  builder.addCase(setMetadata, (state, action) => {
    state.metadata = action.payload.metadata;
  });
  builder.addCase(setSampleNameColumn.fulfilled, (state, action) => {
    console.log("before", current(state));
    state.sampleNameColumn = action.payload.sampleNameColumn;
    state.metadata = action.payload.metadata;
    console.log("after", current(state));
  });
});
