import {
  createAction,
  createAsyncThunk,
  createReducer,
} from "@reduxjs/toolkit";
import { validateSampleName } from "../../../../apis/metadata/sample-utils";
import {
  createSamples,
  getLockedSamples,
  LockedSamplesResponse,
  MetadataItem,
  updateSamples,
  ValidateSampleNameModel,
  validateSamples,
  ValidateSamplesResponse,
} from "../../../../apis/projects/samples";
import { ImportDispatch, ImportState } from "./store";
import { chunkArray } from "../../../../utilities/array-utilities";
import {
  createMetadataFieldsForProject,
  getMetadataFieldsForProject,
  MetadataField,
} from "../../../../apis/metadata/field";
import { Restriction } from "../../../../utilities/restriction-utilities";

export interface MetadataHeaderItem {
  name: string;
  existingRestriction: Restriction | undefined;
  targetRestriction: Restriction;
  rowKey: string;
}

interface MetadataValidateDetailsItem {
  isSampleNameValid: boolean;
  foundSampleId?: number;
  locked: boolean;
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

    await createMetadataFieldsForProject({
      projectId,
      body: headers
        .filter((header) => header.name !== sampleNameColumn)
        .map((header) => ({
          label: header.name,
          restriction: header.targetRestriction,
        })),
    });

    const selectedSampleList = metadata.filter((metadataItem) => {
      const name: string = metadataItem[sampleNameColumn];
      return (
        selectedMetadataKeys.includes(metadataItem.rowKey) &&
        metadataSaveDetails[name]?.saved !== true
      );
    });

    const chunkSize = 10;

    const updateSamplesPromises: Promise<void>[] = [];
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
          .filter(
            ([key]) =>
              headers.map((header) => header.name).includes(key) &&
              key !== sampleNameColumn
          )
          .map(([key, value]) => ({
            field: key,
            value,
            restriction: headers.filter((header) => header.name === key)[0]
              .targetRestriction,
          }));
        return { name, sampleId, metadata: metadataFields };
      });
    if (updateSampleList.length > 0) {
      const chunkedUpdateSampleList = chunkArray(updateSampleList, chunkSize);
      for (const chunk of chunkedUpdateSampleList) {
        updateSamplesPromises.push(
          updateSamples({
            projectId,
            body: chunk,
          })
            .then(() => {
              chunk
                .map((item) => item.name)
                .forEach(
                  (name) => (metadataSaveDetails[name] = { saved: true })
                );
            })
            .catch((error) => {
              const { errors } = error.response.data;
              Object.keys(errors).map((key) => {
                metadataSaveDetails[key] = {
                  saved: false,
                  error: errors[key],
                };
              });
            })
        );
      }
      const chunkedUpdateSamplesPromises = chunkArray(
        updateSamplesPromises,
        chunkSize
      );
      for (const chunk of chunkedUpdateSamplesPromises) {
        await Promise.all(chunk).then(() => {
          dispatch(
            setMetadataSaveDetails(Object.assign({}, metadataSaveDetails))
          );
        });
      }
    }

    const createSamplesPromises: Promise<void>[] = [];
    const createSampleList = selectedSampleList
      .filter((metadataItem) => {
        const name = metadataItem[sampleNameColumn];
        const sampleId = metadataValidateDetails[name].foundSampleId;
        return !sampleId;
      })
      .map((metadataItem) => {
        const name = metadataItem[sampleNameColumn];
        const metadataFields = Object.entries(metadataItem)
          .filter(
            ([key]) =>
              headers.map((header) => header.name).includes(key) &&
              key !== sampleNameColumn
          )
          .map(([key, value]) => ({
            field: key,
            value,
            restriction: headers.filter((header) => header.name === key)[0]
              .targetRestriction,
          }));
        return { name, metadata: metadataFields };
      });

    if (createSampleList.length > 0) {
      const chunkedCreateSampleList = chunkArray(createSampleList, chunkSize);
      for (const chunk of chunkedCreateSampleList) {
        createSamplesPromises.push(
          createSamples({
            projectId,
            body: chunk,
          })
            .then(() => {
              chunk
                .map((item) => item.name)
                .forEach(
                  (name) => (metadataSaveDetails[name] = { saved: true })
                );
            })
            .catch((error) => {
              const { errors } = error.response.data;
              Object.keys(errors).map((key) => {
                metadataSaveDetails[key] = {
                  saved: false,
                  error: errors[key],
                };
              });
            })
        );
      }
      const chunkedCreateSamplesPromises = chunkArray(
        createSamplesPromises,
        chunkSize
      );
      for (const chunk of chunkedCreateSamplesPromises) {
        await Promise.all(chunk).then(() => {
          dispatch(
            setMetadataSaveDetails(Object.assign({}, metadataSaveDetails))
          );
        });
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
    state.sampleNameColumn = "";
    state.headers = [];
    state.metadata = [];
    state.metadataValidateDetails = {};
    state.metadataSaveDetails = {};
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
