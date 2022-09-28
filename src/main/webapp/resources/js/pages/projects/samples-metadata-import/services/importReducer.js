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
  metadataValidateDetails: {},
  metadataSaveDetails: {},
};

/*
Redux async thunk for saving the metadata to samples.
For more information on redux async thunks see: https://redux-toolkit.js.org/api/createAsyncThunk
 */
export const saveMetadata = createAsyncThunk(
  `importReducer/saveMetadata`,
  async ({ projectId, selectedMetadataKeys }, { dispatch, getState }) => {
    const state = getState();
    const sampleNameColumn = state.importReducer.sampleNameColumn;
    const headers = state.importReducer.headers;
    const metadata = state.importReducer.metadata;
    const metadataValidateDetails = state.importReducer.metadataValidateDetails;
    const metadataSaveDetails = {};

    const chunkSize = 100;
    for (let i = 0; i < metadata.length; i = i + chunkSize) {
      const promises = [];
      for (let j = i; j < i + chunkSize && j < metadata.length; j++) {
        const metadataItem = metadata[j];
        const index = metadataItem.rowKey;
        if (
          selectedMetadataKeys.includes(index) &&
          metadataSaveDetails[index]?.saved !== true
        ) {
          const name = metadataItem[sampleNameColumn];
          const metadataFields = Object.entries(metadataItem)
            .filter(
              ([key, value]) =>
                headers.includes(key) && key !== sampleNameColumn
            )
            .map(([key, value]) => ({ field: key, value }));
          const sampleId = metadataValidateDetails[index].foundSampleId;
          if (sampleId) {
            promises.push(
              updateSample({
                projectId,
                sampleId,
                body: {
                  name,
                  // TODO: Don't overwrite organism & description
                  metadata: metadataFields,
                },
              })
                .then((response) => {
                  metadataSaveDetails[index] = { saved: true };
                })
                .catch((error) => {
                  metadataSaveDetails[index] = {
                    saved: false,
                    error: error.response.data.error,
                  };
                })
            );
          } else {
            promises.push(
              createSample({
                projectId,
                body: {
                  name,
                  metadata: metadataFields,
                },
              })
                .then((response) => {
                  metadataSaveDetails[index] = { saved: true };
                })
                .catch((error) => {
                  metadataSaveDetails[index] = {
                    saved: false,
                    error: error.response.data.error,
                  };
                })
            );
          }
        }
      }
      await Promise.all(promises).then(() => {
        dispatch(
          setMetadataSaveDetails(Object.assign({}, metadataSaveDetails))
        );
      });
    }

    return { metadataSaveDetails };
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
    const metadataValidateDetails = {};
    const response = await validateSamples({
      projectId: projectId,
      body: {
        samples: metadata
          .filter((row) => row[column])
          .map((row) => ({
            name: row[column],
          })),
      },
    });
    for (let i = 0; i < metadata.length; i++) {
      const metadataItem = metadata[i];
      const index = metadataItem.rowKey;
      const foundSample = response.data.samples.find(
        (sample) => metadataItem[column] === sample.name
      );
      metadataValidateDetails[index] = {
        isSampleNameValid: validateSampleName(metadataItem[column]),
        foundSampleId: foundSample?.ids?.at(0),
      };
    }

    return {
      sampleNameColumn: column,
      metadataValidateDetails,
    };
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
    payload: {
      metadata: metadata.map((metadataItem, index) => {
        return {
          ...metadataItem,
          rowKey: `metadata-uploader-row-${index}`,
        };
      }),
    },
  })
);

/*
Redux action for setting the project metadata save details.
For more information on redux actions see: https://redux-toolkit.js.org/api/createAction
 */
export const setMetadataSaveDetails = createAction(
  `importReducer/setMetadataSaveDetails`,
  (metadataSaveDetails) => ({
    payload: { metadataSaveDetails },
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
  builder.addCase(setMetadataSaveDetails, (state, action) => {
    state.metadataSaveDetails = action.payload.metadataSaveDetails;
  });
  builder.addCase(setSampleNameColumn.fulfilled, (state, action) => {
    state.sampleNameColumn = action.payload.sampleNameColumn;
    state.metadataValidateDetails = action.payload.metadataValidateDetails;
  });
  builder.addCase(saveMetadata.fulfilled, (state, action) => {
    state.metadataSaveDetails = action.payload.metadataSaveDetails;
  });
});
