import {
  createAction,
  createAsyncThunk,
  createReducer,
} from "@reduxjs/toolkit";
import { validateSampleName } from "../../../../apis/metadata/sample-utils";
import {
  createSample,
  FieldUpdate,
  MetadataItem,
  updateSample,
  ValidateSampleNameModel,
  validateSamples,
  ValidateSamplesResponse,
} from "../../../../apis/projects/samples";
import { ImportDispatch, ImportState } from "../store";
import {
  getMetadataFieldsForProject,
  MetadataField,
} from "../../../../apis/metadata/field";

export interface MetadataHeaderItem {
  name: string;
  restriction: string;
  rowKey: string;
}

interface MetadataValidateDetailsItem {
  isSampleNameValid: boolean;
  foundSampleId?: number;
}

interface MetadataSaveDetailsItem {
  saved: boolean;
  error?: string;
}
interface SaveMetadataResponse {
  metadataSaveDetails: Record<string, MetadataSaveDetailsItem>;
}

interface SetSampleNameColumnResponse {
  sampleNameColumn: string;
  metadataValidateDetails: Record<string, MetadataValidateDetailsItem>;
}

export interface InitialState {
  projectId: string;
  sampleNameColumn: string;
  headers: MetadataHeaderItem[];
  metadata: MetadataItem[];
  metadataValidateDetails: Record<string, MetadataValidateDetailsItem>;
  metadataSaveDetails: Record<string, MetadataSaveDetailsItem>;
}

const initialState: InitialState = {
  projectId: "",
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
export const saveMetadata = createAsyncThunk<
  SaveMetadataResponse,
  { projectId: string; selectedMetadataKeys: string[] },
  { dispatch: ImportDispatch; state: ImportState }
>(
  `importReducer/saveMetadata`,
  async ({ projectId, selectedMetadataKeys }, { dispatch, getState }) => {
    const state: ImportState = getState();
    const { sampleNameColumn, headers, metadata, metadataValidateDetails } =
      state.importReducer;
    const metadataSaveDetails: Record<string, MetadataSaveDetailsItem> = {};

    const chunkSize = 100;
    for (let i = 0; i < metadata.length; i = i + chunkSize) {
      const promises: Promise<void>[] = [];
      for (let j = i; j < i + chunkSize && j < metadata.length; j++) {
        const metadataItem: MetadataItem = metadata[j];
        const index: string = metadataItem.rowKey;
        if (
          selectedMetadataKeys.includes(index) &&
          metadataSaveDetails[index]?.saved !== true
        ) {
          const name: string = metadataItem[sampleNameColumn];
          const metadataFields: FieldUpdate[] = Object.entries(metadataItem)
            .filter(
              ([key]) =>
                headers.map((header) => header.name).includes(key) &&
                key !== sampleNameColumn
            )
            .map(([key, value]) => ({
              field: key,
              value,
              restriction: headers.filter((header) => header.name === key)[0]
                .restriction,
            }));
          const sampleId = metadataValidateDetails[index].foundSampleId;
          if (sampleId) {
            promises.push(
              updateSample({
                projectId,
                sampleId,
                body: {
                  name,
                  metadata: metadataFields,
                },
              })
                .then(() => {
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
                .then(() => {
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
export const setSampleNameColumn = createAsyncThunk<
  SetSampleNameColumnResponse,
  { projectId: string; updatedSampleNameColumn: string },
  { state: ImportState }
>(
  `importReducer/setSampleNameColumn`,
  async ({ projectId, updatedSampleNameColumn }, { getState }) => {
    const state: ImportState = getState();
    const { metadata } = state.importReducer;
    const metadataValidateDetails: Record<string, MetadataValidateDetailsItem> =
      {};
    const samples: ValidateSampleNameModel[] = metadata
      .filter((row) => row[updatedSampleNameColumn])
      .map((row) => ({
        name: row[updatedSampleNameColumn],
      }));
    const response: ValidateSamplesResponse = await validateSamples({
      projectId: projectId,
      body: {
        samples: samples,
      },
    });
    for (const metadataItem of metadata) {
      const index: string = metadataItem.rowKey;
      const sampleName: string = metadataItem[updatedSampleNameColumn];
      const foundSample: ValidateSampleNameModel | undefined =
        response.samples.find(
          (sample: ValidateSampleNameModel) => sampleName === sample.name
        );
      metadataValidateDetails[index] = {
        isSampleNameValid: validateSampleName(sampleName),
        foundSampleId: foundSample?.ids?.at(0),
      };
    }

    return {
      sampleNameColumn: updatedSampleNameColumn,
      metadataValidateDetails,
    };
  }
);

/*
Redux async thunk for setting the metadata headers.
For more information on redux async thunks see: https://redux-toolkit.js.org/api/createAsyncThunk
*/
export const setHeaders = createAsyncThunk<
  { headers: MetadataHeaderItem[] },
  { headers: string[] },
  { state: ImportState }
>(`importReducer/setHeaders`, async ({ headers }, { getState }) => {
  const state: ImportState = getState();
  const { projectId } = state.importReducer;
  const response: MetadataField[] = await getMetadataFieldsForProject(
    projectId
  );
  const updatedHeaders = headers.map((header, index) => {
    const metadataField = response.find(
      (metadataField) => metadataField.label === header
    );
    return {
      name: header,
      restriction: metadataField?.restriction
        ? metadataField.restriction
        : "LEVEL_1",
      rowKey: `metadata-uploader-header-row-${index}`,
    };
  });
  return { headers: updatedHeaders };
});

/*
Redux action for updating the metadata headers.
For more information on redux actions see: https://redux-toolkit.js.org/api/createAction
 */
export const updateHeaders = createAction(
  `importReducer/updateHeaders`,
  (headers: MetadataHeaderItem[]) => ({
    payload: { headers },
  })
);

/*
Redux action for setting the projectId.
For more information on redux actions see: https://redux-toolkit.js.org/api/createAction
 */
export const setProjectId = createAction(
  `importReducer/setProjectID`,
  (projectId: string) => ({
    payload: { projectId },
  })
);

/*
Redux action for setting the project metadata.
For more information on redux actions see: https://redux-toolkit.js.org/api/createAction
 */
export const setMetadata = createAction(
  `importReducer/setMetadata`,
  (metadata: MetadataItem[]) => ({
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
  (metadataSaveDetails: Record<string, MetadataSaveDetailsItem>) => ({
    payload: { metadataSaveDetails },
  })
);

/*
Redux reducer for project metadata.
For more information on redux reducers see: https://redux-toolkit.js.org/api/createReducer
 */
export const importReducer = createReducer(initialState, (builder) => {
  builder.addCase(updateHeaders, (state, action) => {
    state.headers = action.payload.headers;
  });
  builder.addCase(setProjectId, (state, action) => {
    state.projectId = action.payload.projectId;
  });
  builder.addCase(setMetadata, (state, action) => {
    state.metadata = action.payload.metadata;
  });
  builder.addCase(setMetadataSaveDetails, (state, action) => {
    state.metadataSaveDetails = action.payload.metadataSaveDetails;
  });
  builder.addCase(setHeaders.fulfilled, (state, action) => {
    state.headers = action.payload.headers;
  });
  builder.addCase(setSampleNameColumn.fulfilled, (state, action) => {
    state.sampleNameColumn = action.payload.sampleNameColumn;
    state.metadataValidateDetails = action.payload.metadataValidateDetails;
  });
  builder.addCase(saveMetadata.fulfilled, (state, action) => {
    state.metadataSaveDetails = action.payload.metadataSaveDetails;
  });
});
