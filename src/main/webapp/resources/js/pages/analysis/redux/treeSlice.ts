import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { Shapes, TreeTypes } from "@phylocanvas/phylocanvas.gl";
import { getMetadataTemplateFields } from "../../../apis/analysis/analysis";
import { formatMetadata } from "../tree-utilities";
import { TreeProperties } from "../../../types/phylocanvas";
import { fetchTreeAndMetadata } from "./tree-utilities";

const ZOOM_STEP_SIZE = 0.1;

export const fetchTreeAndMetadataThunk = createAsyncThunk(
  `tree/fetchTreeAndMetadata`,
  fetchTreeAndMetadata
);

export const fetchMetadataTemplateFields = createAsyncThunk<
  {
    fields: string[];
    index: number;
  },
  number
>(`tree/fetchMetadataTemplateFields`, async (index, { getState }) => {
  const { tree } = getState();
  const { analysisId, terms, templates } = tree;

  if (index === -1) {
    return { fields: terms };
  } else if (index > templates.length) {
    return { fields: [] };
  } else {
    if ("fields" in templates[index]) {
      return { fields: templates[index].fields };
    } else {
      const data = await getMetadataTemplateFields(
        analysisId,
        templates[index].id
      );
      let fields = [];
      if (data.fields) {
        fields = data.fields.filter((field) => terms.includes(field));
      }
      return {
        fields,
        index,
      };
    }
  }
});

export enum LoadingState {
  "fetching",
  "complete",
  "error-loading",
  "empty",
}

export type TreeState = {
  state: {
    loadingState: LoadingState;
  };
  treeProps: TreeProperties & {
    source: string;
  };
  terms: string[];
  metadataColourMap: {};
  zoomMode: number;
};

const initialState: TreeState = {
  state: {
    loadingState: LoadingState.fetching,
  },
  treeProps: {
    alignLabels: true,
    blocks: [],
    blockLength: 16,
    branchZoom: 0,
    fontFamily: "sans-serif",
    fontSize: 16,
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
};

export const treeSlice = createSlice({
  name: "treeSlice",
  initialState,
  reducers: {
    updateTreeType: (state, action) => {
      state.treeProps.type = action.payload.treeType;
    },
    selectAllTerms: (state, action) => {
      if (action.payload.checked) {
        state.treeProps.blocks = state.terms;
      } else {
        state.treeProps.blocks = [];
      }
    },
    setFieldVisibility: (state, action) => {
      const { field, visible, only } = action.payload;
      if (only && visible) {
        state.treeProps.blocks = [field];
      } else if (!only && visible) {
        state.treeProps.blocks.push(field);
      } else {
        state.treeProps.blocks = state.treeProps.blocks.filter(
          (visibleField) => visibleField !== field
        );
      }
    },
    setMetadataColourForTermWithValue: (state, action) => {
      const { item, key, colour } = action.payload;
      if (colour !== state.metadataColourMap[item][key]) {
        state.metadataColourMap[item][key] = colour;
        state.treeProps.metadata = formatMetadata(
          state.metadata,
          state.terms,
          state.metadataColourMap
        );
      }
    },
    setZoomMode: (state, action) => {
      state.zoomMode = action.payload;
    },
    zoomIn: (state) => {
      if (state.zoomMode === 0) {
        // normal zoom
        state.treeProps.zoom += ZOOM_STEP_SIZE;
      } else if (state.zoomMode === 1) {
        // horizontal zoom aka branch zoom
        state.treeProps.branchZoom += ZOOM_STEP_SIZE;
      } else if (state.zoomMode === 2) {
        // vertical zoom aka step zoom
        state.treeProps.stepZoom += ZOOM_STEP_SIZE;
      }
    },
    zoomOut: (state) => {
      if (state.zoomMode === 0) {
        // normal zoom
        state.treeProps.zoom -= ZOOM_STEP_SIZE;
      } else if (state.zoomMode === 1) {
        // horizontal zoom aka branch zoom
        state.treeProps.branchZoom -= ZOOM_STEP_SIZE;
      } else if (state.zoomMode === 2) {
        // vertical zoom aka step zoom
        state.treeProps.stepZoom -= ZOOM_STEP_SIZE;
      }
    },
  },
  extraReducers: (builder) => {
    builder.addCase(fetchTreeAndMetadataThunk.fulfilled, (state, action) => {
      state.state.loadingState = action.payload.loadingState;
      state.analysisId = action.payload.analysisId;
      state.treeProps = { ...state.treeProps, ...action.payload.treeProps };
      state.metadata = action.payload.metadata;
      state.metadataColourMap = action.payload.metadataColourMap;
      state.terms = action.payload.terms;
      state.templates = action.payload.templates;
    });
    builder.addCase(fetchTreeAndMetadataThunk.rejected, (state, action) => {
      state.state.error = action.payload;
      state.state.loadingState = LoadingState["error-loading"];
    });
    builder.addCase(fetchMetadataTemplateFields.fulfilled, (state, action) => {
      state.treeProps.blocks = action.payload.fields;
      if (action.payload.index) {
        state.templates[action.payload.index].fields = action.payload.fields;
      }
    });
  },
});

export const {
  updateTreeType,
  selectAllTerms,
  setFieldVisibility,
  setMetadataColourForTermWithValue,
  setZoomMode,
  zoomIn,
  zoomOut,
} = treeSlice.actions;

export default treeSlice.reducer;
