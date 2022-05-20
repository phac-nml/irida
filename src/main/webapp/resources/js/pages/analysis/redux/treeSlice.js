import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { getMetadata, getMetadataTemplateFields, getMetadataTemplates, getNewickTree } from "../../../apis/analysis/analysis";
import { formatMetadata } from "../metadata-utilities";

export const fetchTreeAndMetadata = createAsyncThunk(`tree/fetchTreeAndMetadata`, async (id, {rejectWithValue}) => {
  const promises = [getNewickTree(id), getMetadata(id), getMetadataTemplates(id)];

  const [newickData, metadataData, metadataTemplateData] = await Promise.all(promises);

  // Check for errors
  if (!newickData.newick) {
    return rejectWithValue(newickData.message ? newickData.message : newickData.error.message)

  }

  return {
    analysisId: id,
    treeProps: {
      source: newickData.newick,
      showBlockHeaders: true,
      metadata: metadataData.metadata ? formatMetadata(metadataData.metadata, metadataData.terms) : null,
      blocks: metadataData.terms,
    },
    terms: metadataData.terms,
    metadata: metadataData.metadata,
    templates: metadataTemplateData.templates
  };
});

export const fetchMetadataTemplateFields = createAsyncThunk(`tree/fetchMetadataTemplateFields`, async (index, {getState}) => {
  const { tree } = getState();
  const { analysisId, terms, templates } = tree;


  if (index === -1) {
    return {fields: terms};
  } else if (index > templates.length) {
      return {fields: []};
  } else {
    if ("fields" in templates[index]) {
      return {fields: templates[index].fields};
    } else {
      const data = await getMetadataTemplateFields(analysisId, templates[index].id)
      let fields = []
      if (data.fields) {
        fields = data.fields.filter(field => terms.includes(field));
      }
      return {
        fields: fields,
        index: index
      };
    }
  }

});

const initialState = {
  fetching: true,
  treeProps: {
    alignLabels: true,
    interactive: true,
    showLabels: true,
    showLeafLabels: true,
    nodeShape: "dot",
    blocks: []
  },
  terms: []
}

export const treeSlice = createSlice({
  name: "treeSlice",
  initialState,
  reducers: {
    resize: (state, action) => {
      state.treeProps.size = action.payload;
    },
    selectAllTerms: (state, action) => {
      if (action.payload.checked) {
        state.treeProps.blocks = state.terms;
      } else {
        state.treeProps.blocks = [];
      }
    },
    setFieldVisibility: (state, action) => {
      if (action.payload.visible) {
        state.treeProps.blocks.push(action.payload.field);
      } else {
        state.treeProps.blocks = state.treeProps.blocks.filter(field => field !== action.payload.field);
      }
    }
  },
  extraReducers: builder => {
    builder.addCase(fetchTreeAndMetadata.fulfilled, (state, action ) => {
      state.fetching = false,
      state.analysisId = action.payload.analysisId;
      state.treeProps = {...state.treeProps, ...action.payload.treeProps};
      state.metadata = action.payload.metadata;
      state.terms = action.payload.terms;
      state.templates = action.payload.templates;
    });
    builder.addCase(fetchTreeAndMetadata.rejected, (state, action) => {
      state.error = action.payload;
    });
    builder.addCase(fetchMetadataTemplateFields.fulfilled, (state, action) => {
      state.treeProps.blocks = action.payload.fields;
      if (action.payload.index) {
        state.templates[action.payload.index].fields = action.payload.fields;
      }
    })
  }
});

export const { resize, selectAllTerms, setFieldVisibility } = treeSlice.actions;

export default treeSlice.reducer;