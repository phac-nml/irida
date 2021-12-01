import { createAction, createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { fetchMetadataForSample } from "../../apis/samples/samples";

/**
 * Action to set the target sample
 */
export const setSample = createAction(
  `sample/setSample`,
  ({ sample, modifiable }) => ({
    payload: { sample, modifiable },
  })
);

/**
 * Action to set the target project for the sample
 */
export const setProject = createAction(`sample/setProjectId`, (projectId) => ({
  payload: { projectId },
}));

/**
 * Get all metadata for the specified sample.
 * @type {AsyncThunk<unknown, void, {}>}
 */
export const fetchSampleMetadata = createAsyncThunk(
  `sample/fetchSampleMetadata`,
  async ({ sampleId, projectId }) => {
    return await fetchMetadataForSample({ sampleId, projectId });
  }
);

/**
 * Action to edit sample metadata
 */
export const editSampleMetadata = createAction(
  `sample/editSampleMetadata`,
  ({ field, fieldId, entryId, entry, restriction }) => ({
    payload: { field, fieldId, entryId, entry, restriction },
  })
);

/**
 * Action to set the metadata field, entry, and restriction for editing
 */
export const setEditSampleMetadata = createAction(
  `sample/setEditSampleMetadata`,
  ({ editModalVisible, field, fieldId, entryId, entry, restriction }) => ({
    payload: { editModalVisible, field, fieldId, entryId, entry, restriction },
  })
);

/**
 * Action to remove metadata from sample
 */
export const removeSampleMetadataField = createAction(
  `sample/removeSampleMetadataField`,
  ({ field, entryId }) => ({
    payload: { field, entryId },
  })
);

/**
 * Action to add metadata to a sample
 */
export const addSampleMetadataField = createAction(
  `sample/addSampleMetadataField`,
  ({
    metadataTemplateField,
    fieldId,
    metadataEntry,
    entryId,
    metadataRestriction,
  }) => ({
    payload: {
      metadataTemplateField,
      fieldId,
      metadataEntry,
      entryId,
      metadataRestriction,
    },
  })
);

/**
 * Set up the initial state.
 */
const initialState = (() => {
  return {
    sample: {},
    modifiable: false,
    projectId: null,
    editModalVisible: false,
    field: null,
    fieldId: null,
    entryId: null,
    entry: null,
    restriction: "LEVEL_1",
    metadata: [],
    loading: true,
  };
})();

const sampleSlice = createSlice({
  name: "sample",
  initialState,
  extraReducers: (builder) => {
    builder.addCase(fetchSampleMetadata.fulfilled, (state, action) => {
      state.metadata = action.payload.metadata;
      state.loading = false;
    });

    builder.addCase(setSample, (state, action) => {
      state.sample = action.payload.sample;
      state.modifiable = action.payload.modifiable;
    });

    builder.addCase(setProject, (state, action) => {
      state.projectId = action.payload.projectId;
    });

    builder.addCase(setEditSampleMetadata, (state, action) => {
      state.editModalVisible = action.payload.editModalVisible;
      state.field = action.payload.field;
      state.fieldId = action.payload.fieldId;
      state.entryId = action.payload.entryId;
      state.entry = action.payload.entry;
      state.restriction = action.payload.restriction;
    });

    builder.addCase(editSampleMetadata, (state, action) => {
      state.metadata = state.metadata.map((metadataFieldEntry) =>
        metadataFieldEntry.fieldId === action.payload.fieldId &&
        metadataFieldEntry.entryId === action.payload.entryId
          ? {
              ...metadataFieldEntry,
              metadataTemplateField: action.payload.field,
              metadataEntry: action.payload.entry,
              metadataRestriction: action.payload.restriction,
            }
          : metadataFieldEntry
      );
    });

    builder.addCase(addSampleMetadataField, (state, action) => {
      const newMetadataFieldEntry = {
        metadataTemplateField: action.payload.metadataTemplateField,
        fieldId: action.payload.fieldId,
        metadataEntry: action.payload.metadataEntry,
        entryId: action.payload.entryId,
        metadataRestriction: action.payload.metadataRestriction,
      };
      state.metadata = [...state.metadata, newMetadataFieldEntry];
    });

    builder.addCase(removeSampleMetadataField, (state, action) => {
      state.metadata = state.metadata.filter(
        (metadataFieldEntry) =>
          metadataFieldEntry.field !== action.payload.field &&
          metadataFieldEntry.entryId !== action.payload.entryId
      );
    });
  },
});

export default sampleSlice.reducer;
