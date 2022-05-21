import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { Shapes, TreeTypes } from "@phylocanvas/phylocanvas.gl";
import { getMetadata, getMetadataTemplateFields, getMetadataTemplates, getNewickTree } from "../../../apis/analysis/analysis";
import { formatMetadata } from "../metadata-utilities";

const zoomStepSize = 0.1;

export const fetchTreeAndMetadata = createAsyncThunk(`tree/fetchTreeAndMetadata`, async (id, {rejectWithValue}) => {
  const promises = [getNewickTree(id), getMetadata(id), getMetadataTemplates(id)];

  const [newickData, metadataData, metadataTemplateData] = await Promise.all(promises);

  // Check for errors
  if (!newickData.newick) {
    return rejectWithValue(newickData.message ? newickData.message : newickData.error.message)

  }

  const { formattedMetadata, metadataColourMap } = formatMetadata(metadataData.metadata, metadataData.terms);

  return {
    analysisId: id,
    treeProps: {
      source: newickData.newick,
      showBlockHeaders: true,
      metadata: formattedMetadata,
      blocks: metadataData.terms,
    },
    terms: metadataData.terms,
    metadata: metadataData.metadata,
    metadataColourMap: metadataColourMap,
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
    blocks: [],
    branchZoom: 0,
    fontFamily: "sans-serif",
    interactive: true,
    nodeShape: Shapes.Dot,
    padding: 20,
    showLabels: true,
    showLeafLabels: true,
    stepZoom: 0,
    type: TreeTypes.Rectangular,
    zoom: -0.1,
  },
  terms: [],
  metadataColourMap: {},
  zoomMode: 0, // normal zoom
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
    },
    setZoomMode: (state, action) => {
      state.zoomMode = action.payload;
    },
    zoomIn: (state) => {
      if (state.zoomMode === 0) {
        // normal zoom
        state.treeProps.zoom += zoomStepSize;
      } else if (state.zoomMode === 1) {
        // horizontal zoom aka branch zoom
        state.treeProps.branchZoom += zoomStepSize;
      } else if (state.zoomMode === 2) {
        // vertical zoom aka step zoom
        state.treeProps.stepZoom += zoomStepSize;
      }
    },
    zoomOut: (state) => {
      if (state.zoomMode === 0) {
        // normal zoom
        state.treeProps.zoom -= zoomStepSize;
      } else if (state.zoomMode === 1) {
        // horizontal zoom aka branch zoom
        state.treeProps.branchZoom -= zoomStepSize;
      } else if (state.zoomMode === 2) {
        // vertical zoom aka step zoom
        state.treeProps.stepZoom -= zoomStepSize;
      }
    },
  },
  extraReducers: builder => {
    builder.addCase(fetchTreeAndMetadata.fulfilled, (state, action ) => {
      state.fetching = false,
      state.analysisId = action.payload.analysisId;
      state.treeProps = {...state.treeProps, ...action.payload.treeProps};
      state.metadata = action.payload.metadata;
      state.metadataColourMap = action.payload.metadataColourMap;
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

export const { resize, selectAllTerms, setFieldVisibility, setZoomMode, zoomIn, zoomOut } = treeSlice.actions;

export default treeSlice.reducer;