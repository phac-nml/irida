import {
  createAction,
  createAsyncThunk,
  createReducer,
} from "@reduxjs/toolkit";
import { validateSampleName } from "../../../../apis/metadata/sample-utils";
import {
  createSample,
  updateSample,
  validateSamples,
} from "../../../../apis/projects/samples";

const initialState = {
  sampleNameColumn: "",
  headers: [],
  metadata: [],
};

/*
Redux async thunk for saving the metadata to samples.
For more information on redux async thunks see: https://redux-toolkit.js.org/api/createAsyncThunk
 */
export const saveMetadata = createAsyncThunk(
  `importReducer/saveMetadata`,
  async ({ projectId, selectedMetadataKeys }, { getState }) => {
    const state = getState();
    const sampleNameColumn = state.importReducer.sampleNameColumn;
    const headers = state.importReducer.headers;
    const metadata = state.importReducer.metadata;
    const updatedMetadata = JSON.parse(JSON.stringify(metadata));

    for (const [index, metadataItem] of updatedMetadata.entries()) {
      if (selectedMetadataKeys.includes(metadataItem.rowKey)) {
        const name = metadataItem[sampleNameColumn];
        const metadataFields = Object.entries(metadataItem)
          .filter(
            ([key, value]) => headers.includes(key) && key !== sampleNameColumn
          )
          .map(([key, value]) => ({ field: key, value }));
        const sampleId = metadataItem.foundSampleId;

        if (sampleId) {
          await updateSample({
            projectId,
            sampleId,
            body: {
              name,
              // TODO: Don't overwrite organism & description
              metadata: metadataFields,
            },
          })
            .then((response) => {
              console.log("UPDATE SAMPLE RESPONSE");
              console.log(response);
              updatedMetadata[index].saved = true;
            })
            .catch((error) => {
              console.log("UPDATE SAMPLE ERROR");
              console.log(error);
              updatedMetadata[index].saved = false;
              updatedMetadata[index].error = error.response.data.error;
            });
        } else {
          await createSample({
            projectId,
            body: {
              name,
              metadata: metadataFields,
            },
          })
            .then((response) => {
              console.log("CREATE SAMPLE RESPONSE");
              console.log(response);
              updatedMetadata[index].saved = true;
            })
            .catch((error) => {
              console.log("CREATE SAMPLE ERROR");
              console.log(error);
              updatedMetadata[index].saved = false;
              updatedMetadata[index].error = error.response.data.error;
            });
        }
      }
    }

    return { metadata: updatedMetadata };
  }
);

/*
Redux async thunk for setting the sample name column and enriching the metadata.
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
    state.sampleNameColumn = action.payload.sampleNameColumn;
    state.metadata = action.payload.metadata;
  });
  builder.addCase(saveMetadata.fulfilled, (state, action) => {
    state.metadata = action.payload.metadata;
  });
});
