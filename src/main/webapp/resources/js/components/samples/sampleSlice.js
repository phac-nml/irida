import { createAction, createSlice } from "@reduxjs/toolkit";

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
 * Action to set the sample metadata
 */
export const setSampleMetadata = createAction(
  `sample/setSampleMetadata`,
  (metadata) => ({
    payload: { metadata },
  })
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
 * Action to remove metadata from samples
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
const sampleSlice = createSlice({
  name: "sample",
  initialState: {
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
  },
  extraReducers: (builder) => {
    builder.addCase(setSample, (state, action) => {
      state.sample = action.payload.sample;
      state.modifiable = action.payload.modifiable;
    });

    builder.addCase(setProject, (state, action) => {
      state.projectId = action.payload.projectId;
    });

    builder.addCase(setSampleMetadata, (state, action) => {
      state.metadata = action.payload.metadata;
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
      state.metadata = state.metadata.map((el) =>
        el.fieldId === action.payload.fieldId &&
        el.entryId === action.payload.entryId
          ? {
              ...el,
              metadataTemplateField: action.payload.field,
              metadataEntry: action.payload.entry,
              metadataRestriction: action.payload.restriction,
            }
          : el
      );
    });

    builder.addCase(addSampleMetadataField, (state, action) => {
      state.metadata = state.metadata.push({
        ...state.metadata,
        metadataTemplateField: action.payload.field,
        fieldId: action.payload.fieldId,
        metadataEntry: action.payload.entry,
        entryId: action.payload.entryId,
        metadataRestriction: action.payload.restriction,
      });
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
