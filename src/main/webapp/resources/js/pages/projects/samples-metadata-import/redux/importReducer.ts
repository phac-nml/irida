import {
  createAction,
  createAsyncThunk,
  createReducer,
} from "@reduxjs/toolkit";
import { validateSampleName } from "../../../../apis/metadata/sample-utils";
import {
  CreateSampleItem,
  createSamples,
  getLockedSamples,
  LockedSamplesResponse,
  MetadataItem,
  UpdateSampleItem,
  updateSamples,
  ValidateSampleNameModel,
  validateSamples,
  ValidateSamplesResponse,
} from "../../../../apis/projects/samples";
import { ImportDispatch, ImportState } from "./store";
import {
  createMetadataFieldsForProject,
  getMetadataFieldsForProject,
} from "../../../../apis/metadata/field";
import { Restriction } from "../../../../utilities/restriction-utilities";
import { createMetadataFields, generatePromiseList } from "./import-utilities";
import { MetadataField } from "../../../../types/irida";

export interface MetadataHeaderItem {
  name: string;
  existingRestriction: Restriction | undefined;
  targetRestriction: Restriction;
  rowKey: string;
}

export interface MetadataValidateDetailsItem {
  isSampleNameValid: boolean;
  foundSampleId?: number;
  locked: boolean;
}

export interface MetadataSaveDetailsItem {
  saved: boolean;
  error?: string;
}

export interface SaveMetadataResponse {
  metadataSaveDetails: Record<string, MetadataSaveDetailsItem>;
}

export interface SetSampleNameColumnResponse {
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
  percentComplete: number;
}

const initialState: InitialState = {
  projectId: "",
  sampleNameColumn: "",
  headers: [],
  metadata: [],
  metadataValidateDetails: {},
  metadataSaveDetails: {},
  percentComplete: 0,
};

/*
Redux async thunk for saving the metadata to samples.
For more information on redux async thunks see: https://redux-toolkit.js.org/api/createAsyncThunk
 */
export const saveMetadata = createAsyncThunk<
  SaveMetadataResponse,
  { projectId: string; selectedMetadataKeys: string[] },
  { dispatch: ImportDispatch; state: ImportState; rejectValue: string }
>(
  `importReducer/saveMetadata`,
  async (
    { projectId, selectedMetadataKeys },
    { dispatch, getState, rejectWithValue }
  ) => {
    const state: ImportState = getState();
    const {
      sampleNameColumn,
      headers,
      metadata,
      metadataValidateDetails,
      metadataSaveDetails,
    } = state.importReducer;

    try {
      //save header details (metadata field & restriction)
      //if failure display error notification on page
      await createMetadataFieldsForProject({
        projectId,
        body: headers
          .filter((header) => header.name !== sampleNameColumn)
          .map((header) => ({
            label: header.name,
            restriction: header.targetRestriction,
          })),
      }).catch((error) => {
        throw new Error(error.response.data.error);
      });

      //save selected metadata entry rows
      //during a partial failure only save data that has not already been saved
      const selectedSampleList = metadata.filter((metadataItem) => {
        const name: string = metadataItem[sampleNameColumn];
        return (
          selectedMetadataKeys.includes(metadataItem.rowKey) &&
          metadataSaveDetails[name]?.saved !== true
        );
      });

      const createSampleList: CreateSampleItem[] = [];
      const updateSampleList: UpdateSampleItem[] = [];

      selectedSampleList.forEach((metadataItem) => {
        const name = metadataItem[sampleNameColumn];
        const sampleId = metadataValidateDetails[name].foundSampleId;
        const metadata = createMetadataFields(
          sampleNameColumn,
          headers,
          metadataItem
        );

        if (typeof sampleId === "number") {
          updateSampleList.push({ name, sampleId, metadata });
        } else {
          createSampleList.push({ name, metadata });
        }
      });

      const {
        promiseList: createPromiseList,
        newMetadataSaveDetails: createMetadataSaveDetails,
      } = generatePromiseList(
        createSampleList,
        createSamples,
        projectId,
        selectedSampleList.length,
        metadataSaveDetails,
        dispatch
      );
      await Promise.all(createPromiseList);

      const {
        promiseList: updatePromiseList,
        newMetadataSaveDetails: updateMetadataSaveDetails,
      } = generatePromiseList(
        updateSampleList,
        updateSamples,
        projectId,
        selectedSampleList.length,
        createMetadataSaveDetails,
        dispatch
      );
      await Promise.all(updatePromiseList);
      return { metadataSaveDetails: updateMetadataSaveDetails };
    } catch (error) {
      let message;
      if (error instanceof Error) {
        ({ message } = error);
      } else {
        message = String(error);
      }
      return rejectWithValue(message);
    }
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
    const validatedSamples: ValidateSamplesResponse = await validateSamples({
      projectId: projectId,
      body: {
        samples: samples,
      },
    });
    const lockedSamples: LockedSamplesResponse = await getLockedSamples({
      projectId,
    });
    for (const metadataItem of metadata) {
      const sampleName: string = metadataItem[updatedSampleNameColumn];
      const foundValidatedSamples = validatedSamples.samples.find(
        (sample) => sampleName === sample.name
      );
      const foundSampleId = foundValidatedSamples?.ids?.at(0);
      const foundLockedSamples = lockedSamples.sampleIds.find(
        (sampleId) => sampleId === foundSampleId
      );
      metadataValidateDetails[sampleName] = {
        isSampleNameValid: validateSampleName(sampleName),
        foundSampleId: foundSampleId,
        locked: !!foundLockedSamples,
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
      existingRestriction: metadataField?.restriction,
      targetRestriction: metadataField?.restriction
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
    payload: { metadata },
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
Redux action for updating the progress bar.
For more information on redux actions see: https://redux-toolkit.js.org/api/createAction
 */
export const updatePercentComplete = createAction(
  `importReducer/updatePercentComplete`,
  (amount: number) => ({
    payload: { amount },
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
    state.sampleNameColumn = "";
    state.headers = [];
    state.metadata = [];
    state.metadataValidateDetails = {};
    state.metadataSaveDetails = {};
    state.percentComplete = 0;
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
  builder.addCase(updatePercentComplete, (state, action) => {
    state.percentComplete = state.percentComplete + action.payload.amount;
  });
});
