import {
  createAction,
  createAsyncThunk,
  createReducer,
} from "@reduxjs/toolkit";
import { validateSampleName } from "../../../../apis/metadata/sample-utils";
import {
  createSamples,
  MetadataItem,
  updateSamples,
  ValidateSampleNameModel,
  validateSamples,
  ValidateSamplesResponse,
} from "../../../../apis/projects/samples";
import { ImportDispatch, ImportState } from "../store";
import { chunkArray } from "../../../../utilities/array-utilities";

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
  headers: string[];
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

    const selectedSampleList = metadata.filter((metadataItem) => {
      const name: string = metadataItem[sampleNameColumn];
      return (
        selectedMetadataKeys.includes(metadataItem.rowKey) &&
        metadataSaveDetails[name]?.saved !== true
      );
    });
    const chunkSize = 100;

    const updateSampleList = selectedSampleList
      .filter((metadataItem) => {
        const name = metadataItem[sampleNameColumn];
        const sampleId = metadataValidateDetails[name].foundSampleId;
        return sampleId;
      })
      .map((metadataItem) => {
        const name = metadataItem[sampleNameColumn];
        const sampleId = metadataValidateDetails[name].foundSampleId;
        const metadataFields = Object.entries(metadataItem)
          .filter(([key]) => headers.includes(key) && key !== sampleNameColumn)
          .map(([key, value]) => ({ field: key, value }));
        return { name, sampleId, metadata: metadataFields };
      });

    if (updateSampleList.length > 0) {
      const chunkedUpdateSampleList = chunkArray(updateSampleList, chunkSize);
      for (const chunk of chunkedUpdateSampleList) {
        await updateSamples({
          projectId,
          body: chunk,
        })
          .then(() => {
            chunk
              .map((item) => item.name)
              .forEach((name) => (metadataSaveDetails[name] = { saved: true }));
          })
          .catch((error) => {
            const { errors } = error.response.data;
            Object.keys(errors).map((key) => {
              metadataSaveDetails[key] = {
                saved: false,
                error: errors[key],
              };
            });
          });
        dispatch(
          setMetadataSaveDetails(Object.assign({}, metadataSaveDetails))
        );
      }
    }

    const createSampleList = selectedSampleList
      .filter((metadataItem) => {
        const name = metadataItem[sampleNameColumn];
        const sampleId = metadataValidateDetails[name].foundSampleId;
        return !sampleId;
      })
      .map((metadataItem) => {
        const name = metadataItem[sampleNameColumn];
        const metadataFields = Object.entries(metadataItem)
          .filter(([key]) => headers.includes(key) && key !== sampleNameColumn)
          .map(([key, value]) => ({ field: key, value }));
        return { name, metadata: metadataFields };
      });

    if (createSampleList.length > 0) {
      const chunkedCreateSampleList = chunkArray(createSampleList, chunkSize);
      for (const chunk of chunkedCreateSampleList) {
        await createSamples({
          projectId,
          body: chunk,
        })
          .then(() => {
            chunk
              .map((item) => item.name)
              .forEach((name) => (metadataSaveDetails[name] = { saved: true }));
          })
          .catch((error) => {
            const { errors } = error.response.data;
            Object.keys(errors).map((key) => {
              metadataSaveDetails[key] = {
                saved: false,
                error: errors[key],
              };
            });
          });
        dispatch(
          setMetadataSaveDetails(Object.assign({}, metadataSaveDetails))
        );
      }
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
  { projectId: string; column: string },
  { state: ImportState }
>(
  `importReducer/setSampleNameColumn`,
  async ({ projectId, column }, { getState }) => {
    const state: ImportState = getState();
    const { metadata } = state.importReducer;
    const metadataValidateDetails: Record<string, MetadataValidateDetailsItem> =
      {};
    const samples: ValidateSampleNameModel[] = metadata
      .filter((row) => row[column])
      .map((row) => ({
        name: row[column],
      }));
    const response: ValidateSamplesResponse = await validateSamples({
      projectId: projectId,
      body: {
        samples: samples,
      },
    });
    for (const metadataItem of metadata) {
      const name: string = metadataItem[column];
      const foundSample: ValidateSampleNameModel | undefined =
        response.samples.find(
          (sample: ValidateSampleNameModel) => name === sample.name
        );
      metadataValidateDetails[name] = {
        isSampleNameValid: validateSampleName(name),
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
Redux action for setting the metadata headers.
For more information on redux actions see: https://redux-toolkit.js.org/api/createAction
 */
export const setHeaders = createAction(
  `importReducer/setHeaders`,
  (headers: string[]) => ({
    payload: { headers },
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
  builder.addCase(setProjectId, (state, action) => {
    state.projectId = action.payload.projectId;
  });
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
