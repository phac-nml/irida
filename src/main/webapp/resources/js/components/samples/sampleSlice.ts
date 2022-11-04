import { createAction, createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  fetchMetadataForSample,
  SampleMetadataFieldEntry,
} from "../../apis/samples/samples";
import { Sample } from "../../types/irida";

/**
 * Action to set the target sample
 */
export const setSample = createAction(
  `sample/setSample`,
  ({ sample, modifiable, inCart }) => ({
    payload: { sample, modifiable, inCart },
  })
);

/**
 * Action to set the target project for the sample
 */
export const setProjectDetails = createAction(
  `sample/setProjectDetails`,
  ({ projectId, projectName }) => ({
    payload: { projectId, projectName },
  })
);

/**
 * Get all metadata for the specified sample.
 * @type {AsyncThunk<unknown, void, {}>}
 */
export const fetchSampleMetadata = createAsyncThunk(
  `sample/fetchSampleMetadata`,
  async ({ sampleId, projectId }: { sampleId: number; projectId: number }) => {
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
  ({ entryId }) => ({
    payload: { entryId },
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
 * Action to set the default sequencing object for a sample
 */
export const setDefaultSequencingObject = createAction(
  `sample/setDefaultSequencingObject`,
  (sequencingObject) => ({
    payload: { sequencingObject },
  })
);

/**
 * Action to set the default genome assembly for a sample
 */
export const setDefaultGenomeAssembly = createAction(
  `sample/setDefaultGenomeAssembly`,
  (genomeAssembly) => ({
    payload: { genomeAssembly },
  })
);

/**
 * Action to update sample details
 */
export const updateDetails = createAction(
  `sample/updateDetails`,
  ({ field, value }) => ({
    payload: { field, value },
  })
);

export const updateSampleInCart = createAction(
  `sample/updateSampleInCart`,
  ({ inCart }) => ({
    payload: { inCart },
  })
);

/**
 * Set up the initial state.
 */
const initialState: {
  entry: string;
  metadata: SampleMetadataFieldEntry[];
  field: string;
  editModalVisible: boolean;
  restriction: string;
  modifiable: boolean;
  projectName: string;
  loading: boolean;
  sample: Sample;
  projectId: number;
  fieldId: number;
  entryId: number;
  inCart: boolean;
} = (() => {
  return {
    sample: {} as Sample,
    modifiable: false,
    projectId: 0,
    projectName: "",
    editModalVisible: false,
    field: "",
    fieldId: 0,
    entryId: 0,
    entry: "",
    restriction: "LEVEL_1",
    metadata: [] as SampleMetadataFieldEntry[],
    loading: true,
    inCart: false,
  };
})();

const sampleSlice = createSlice({
  reducers: {},
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
      state.inCart = action.payload.inCart;
    });

    builder.addCase(setDefaultSequencingObject, (state, action) => {
      state.sample.defaultSequencingObject = action.payload.sequencingObject;
    });

    builder.addCase(setDefaultGenomeAssembly, (state, action) => {
      state.sample.defaultGenomeAssembly = action.payload.genomeAssembly;
    });

    builder.addCase(setProjectDetails, (state, action) => {
      state.projectId = action.payload.projectId;
      state.projectName = action.payload.projectName;
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
      /*
       If an existing field has the same label then we filter it out before adding to the state
       */
      state.metadata = state.metadata.filter(
        (metadataFieldEntry) =>
          metadataFieldEntry.metadataTemplateField !==
          action.payload.metadataTemplateField
      );

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
          metadataFieldEntry.entryId !== action.payload.entryId
      );
    });

    builder.addCase(updateDetails, (state, action) => {
      state.sample = {
        ...state.sample,
        [action.payload.field]: action.payload.value,
      };
    });

    builder.addCase(updateSampleInCart, (state, action) => {
      state.inCart = action.payload.inCart;
    });
  },
});

export default sampleSlice.reducer;
