import {
  createAsyncThunk,
  createSlice,
  PayloadAction,
  SerializedError,
} from "@reduxjs/toolkit";
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { TreeTypes } from "@phylocanvas/phylocanvas.gl";
import { Metadata } from "../../../apis/analysis/analysis";
import {
  MetadataColourMap,
  Template,
  TreeProperties,
  TreeType,
} from "../../../types/phylocanvas";
import { RootState } from "../store";
import {
  fetchMetadataTemplateFields,
  FetchMetadataTemplateFieldsResponse,
  fetchTreeAndMetadata,
  FetchTreeAndMetadataResponse,
  formatMetadata,
} from "./tree-utilities";

const ZOOM_STEP_SIZE = 0.1;

export const fetchTreeAndMetadataThunk = createAsyncThunk<
  FetchTreeAndMetadataResponse,
  number,
  { rejectValue: string }
>(
  `tree/fetchTreeAndMetadata`,
  async (analysisId: number, { rejectWithValue }) => {
    try {
      return await fetchTreeAndMetadata(analysisId);
    } catch (e: unknown) {
      return rejectWithValue("Cannot fetch tree and metadata");
    }
  }
);

export const fetchMetadataTemplateFieldsThunk = createAsyncThunk<
  FetchMetadataTemplateFieldsResponse,
  number,
  { state: RootState; rejectValue: string }
>(
  `tree/fetchMetadataTemplateFields`,
  async (index, { getState, rejectWithValue }) => {
    const {
      tree: { analysisId, terms, templates },
    } = getState();

    try {
      return await fetchMetadataTemplateFields({
        index,
        analysisId,
        terms,
        templates,
      });
    } catch (e: unknown) {
      return rejectWithValue("Cannot fetch metadata template fields");
    }
  }
);

export enum LoadingState {
  "fetching",
  "complete",
  "error-loading",
  "empty",
}

export type TreeState = {
  analysisId: number;
  metadata: Metadata;
  metadataColourMap: MetadataColourMap;
  state: {
    error: undefined | SerializedError;
    loadingState: LoadingState;
  };
  treeProps: TreeProperties;
  templates: Template[];
  terms: string[];
  zoomMode: number;
};

const initialState = {
  analysisId: -1,
  metadata: {},
  metadataColourMap: {},
  state: {
    error: undefined,
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
    metadata: {},
    nodeShape: "dot",
    padding: 20,
    showBlockHeaders: true,
    showLabels: true,
    showLeafLabels: true,
    stepZoom: 0,
    size: { height: 0, width: 0 },
    source: "",
    type: TreeTypes.Rectangular,
    zoom: -0.1,
  },
  templates: [],
  terms: [],
  zoomMode: 0,
} as TreeState;

type UpdateTreeTypePayload = {
  treeType: TreeType;
};

type SelectAllTermsPayload = {
  checked: boolean;
};

type SetFieldVisibilityPayload = {
  field: string;
  visible: boolean;
  only: boolean;
};

type SetMetadataColourForTermWithValuePayload = {
  item: string;
  key: string;
  colour: string;
};

type FetchTreeAndMetadataThunkPayload = {
  loadingState: LoadingState;
  analysisId: number;
  treeProps: Partial<TreeProperties>;
  metadata: Metadata;
  metadataColourMap: MetadataColourMap;
  terms: string[];
  templates: Template[];
};

type FetchMetadataTemplateFieldsThunkPayload = {
  fields: string[];
  index?: number | undefined;
};

export const treeSlice = createSlice({
  name: "treeSlice",
  initialState,
  reducers: {
    updateTreeType: (state, action: PayloadAction<UpdateTreeTypePayload>) => {
      state.treeProps.type = action.payload.treeType;
    },
    selectAllTerms: (state, action: PayloadAction<SelectAllTermsPayload>) => {
      if (action.payload.checked) {
        state.treeProps.blocks = state.terms;
      } else {
        state.treeProps.blocks = [];
      }
    },
    setFieldVisibility: (
      state,
      action: PayloadAction<SetFieldVisibilityPayload>
    ) => {
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
    setMetadataColourForTermWithValue: (
      state,
      action: PayloadAction<SetMetadataColourForTermWithValuePayload>
    ) => {
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
    setZoomMode: (state, action: PayloadAction<number>) => {
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
    builder.addCase(
      fetchTreeAndMetadataThunk.fulfilled,
      (state, action: PayloadAction<FetchTreeAndMetadataThunkPayload>) => {
        state.state.loadingState = action.payload.loadingState;
        state.analysisId = action.payload.analysisId;
        state.treeProps = { ...state.treeProps, ...action.payload.treeProps };
        state.metadata = action.payload.metadata;
        state.metadataColourMap = action.payload.metadataColourMap;
        state.terms = action.payload.terms;
        state.templates = action.payload.templates;
      }
    );
    builder.addCase(fetchTreeAndMetadataThunk.rejected, (state, action) => {
      state.state.error = action.error;
      state.state.loadingState = LoadingState["error-loading"];
    });
    builder.addCase(
      fetchMetadataTemplateFieldsThunk.fulfilled,
      (
        state,
        action: PayloadAction<FetchMetadataTemplateFieldsThunkPayload>
      ) => {
        state.treeProps.blocks = action.payload.fields;
        if (action.payload.index) {
          state.templates[action.payload.index].fields = action.payload.fields;
        }
      }
    );
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

export const getTreeProps = (state: RootState): TreeProperties =>
  state.tree.treeProps;
